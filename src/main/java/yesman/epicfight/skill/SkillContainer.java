package yesman.epicfight.skill;

import java.util.Set;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SkillContainer {
	protected Skill containingSkill;
	private PlayerPatch<?> executer;
	protected int prevDuration = 0;
	protected int duration = 0;
	protected int maxDuration = 0;
	protected float resource = 0;
	protected float prevResource = 0;
	protected boolean isActivated = false;
	protected int stack;
	protected SkillDataManager skillDataManager;
	protected boolean disabled;
	
	public SkillContainer(PlayerPatch<?> executer, int slotIndex) {
		this.executer = executer;
		this.skillDataManager = new SkillDataManager(slotIndex);
	}
	
	public void setExecuter(PlayerPatch<?> executer) {
		this.executer = executer;
	}
	
	public PlayerPatch<?> getExecuter() {
		return this.executer;
	}
	
	public boolean setSkill(Skill skill) {
		if (this.containingSkill == skill) {
			return false;
		}
		
		if (this.containingSkill != null) {
			this.containingSkill.onRemoved(this);
		}
		this.containingSkill = skill;
		this.resetValues();
		this.skillDataManager.reset();
		
		if (skill != null) {
			skill.onInitiate(this);
		}
		
		this.stack = 0;
		
		return true;
	}
	
	public boolean isDisabled() {
		return this.disabled;
	}
	
	public void setDisabled(boolean disable) {
		this.disabled = disable;
	}
	
	public void resetValues() {
		this.isActivated = false;
		this.prevDuration = 0;
		this.duration = 0;
		this.prevResource = 0.0F;
		this.resource = 0.0F;
	}
	
	public boolean isEmpty() {
		return this.containingSkill == null;
	}
	
	public void setResource(float value) {
		if(this.containingSkill != null) {
			this.containingSkill.setConsumption(this, value);
		} else {
			this.prevResource = 0;
			this.resource = 0;
		}
	}
	
	public void setMaxDuration(int value) {
		this.maxDuration = Math.max(value, 0);
	}
	
	public void setDuration(int value) {
		if (this.containingSkill != null) {
			if (!this.isActivated() && value > 0) {
				this.isActivated = true;
			}
			
			this.duration = Math.min(this.maxDuration, Math.max(value, 0));
		} else {
			this.duration = 0;
		}
	}
	
	public void setStack(int stack) {
		if (this.containingSkill != null) {
			this.stack = Math.min(this.containingSkill.maxStackSize, Math.max(stack, 0));
			if (this.stack <= 0 && this.containingSkill.shouldDeactivateAutomatically(this.executer)) {
				this.deactivate();
				this.containingSkill.onReset(this);
			}
		} else {
			this.stack = 0;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean sendExecuteRequest(LocalPlayerPatch executer, Set<Object> packetStorage) {
		if (this.canExecute(executer)) {
			ClientEngine.instance.renderEngine.unlockRotation(executer.getOriginal());
			Object packet = this.containingSkill.getExecutionPacket(executer, this.containingSkill.gatherArguments(executer, ClientEngine.instance.controllEngine));
			
			if (packet != null) {
				packetStorage.add(packet);
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean requestExecute(ServerPlayerPatch executer, FriendlyByteBuf buf) {
		if (this.canExecute(executer)) {
			this.containingSkill.executeOnServer(executer, buf);
			return true;
		}
		return false;
	}
	
	public SkillDataManager getDataManager() {
		return this.skillDataManager;
	}

	public float getResource() {
		return this.resource;
	}

	public int getRemainDuration() {
		return this.duration;
	}
	
	public boolean canExecute(PlayerPatch<?> executer) {
		if (this.containingSkill == null) {
			return false;
		} else {
			return (this.containingSkill.resourcePredicate(executer) || executer.getOriginal().isCreative()) && this.containingSkill.canExecute(executer) && this.containingSkill.isExecutableState(executer);
		}
	}
	
	public void update() {
		if (this.containingSkill != null) {
			this.containingSkill.updateContainer(this);
		}
	}
	
	public int getStack() {
		return this.stack;
	}

	public Skill getSkill() {
		return this.containingSkill;
	}
	
	public void activate() {
		if (!this.isActivated) {
			this.prevDuration = this.maxDuration;
			this.duration = this.maxDuration;
			this.isActivated = true;
		}
	}
	
	public void deactivate() {
		if (this.isActivated) {
			this.prevDuration = 0;
			this.duration = 0;
			this.isActivated = false;
		}
	}
	
	public boolean isActivated() {
		return this.isActivated;
	}
	
	public boolean hasSkill(Skill skill) {
		return this.containingSkill != null ? this.containingSkill.equals(skill) : false;
	}
	
	public boolean isFull() {
		return this.containingSkill != null ? this.stack >= this.containingSkill.maxStackSize : true;
	}
	
	public boolean isReady() {
		return this.containingSkill != null ? this.stack > 0 : true;
	}
	
	public float getResource(float partialTicks) {
		return this.containingSkill != null && this.containingSkill.consumption > 0 ? (this.prevResource + ((this.resource - this.prevResource)
				* partialTicks)) / this.containingSkill.consumption : 0;
	}
	
	public float getNeededResource() {
		return this.containingSkill != null ? this.containingSkill.consumption - this.resource : 0;
	}

	public float getDurationRatio(float partialTicks) {
		return this.containingSkill != null && this.maxDuration > 0 ? (this.prevDuration + ((this.duration - this.prevDuration) * partialTicks))
				/ this.maxDuration : 0;
	}
}