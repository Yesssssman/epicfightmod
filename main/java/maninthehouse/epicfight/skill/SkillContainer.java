package maninthehouse.epicfight.skill;

import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkillContainer {
	protected Skill containingSkill;
	protected PlayerData<?> executer;
	protected int prevDuration = 0;
	protected int duration = 0;
	protected float cooldown = 0;
	protected float prevCooldown = 0;
	protected boolean isActivated = false;
	protected boolean consumeDuration;
	protected int stack;
	protected NBTTagCompound skillVariables;
	
	public SkillContainer(PlayerData<?> executer) {
		this.executer = executer;
		this.skillVariables = new NBTTagCompound();
	}
	
	public void setExecuter(PlayerData<?> executer) {
		this.executer = executer;
	}
	
	public SkillContainer setSkill(Skill skill) {
		if(this.containingSkill != null) {
			this.containingSkill.onDeleted(this);
		}
		
		this.containingSkill = skill;
		if(skill != null) {
			skill.onInitiate(this);
		}
		
		this.reset(false);
		this.stack = 0;
		
		for(String key : this.skillVariables.getKeySet()) {
			this.skillVariables.removeTag(key);
		}
		
		return this;
	}
	
	public void reset(boolean consume) {
		if (consume && this.stack > 0) {
			--this.stack;
		}
		this.isActivated = false;
		this.consumeDuration = true;
		this.prevDuration = 0;
		this.duration = 0;

		if (this.getContaining() != null && this.getContaining().maxStackSize <= 1) {
			this.prevCooldown = 0;
			this.cooldown = 0;
			this.containingSkill.onReset(this);
		}
	}

	public boolean isEmpty() {
		return this.containingSkill == null;
	}
	
	public void setCooldown(float value) {
		if (this.containingSkill != null) {
			this.containingSkill.setCooldown(this, value);
		} else {
			this.prevCooldown = 0;
			this.cooldown = 0;
		}
	}
	
	public void setDuration(int value) {
		if (this.containingSkill != null) {
			if(!this.isActivated && value > 0) {
				this.isActivated = true;
			}
			
			this.duration = value;
			this.duration = Math.min(this.containingSkill.duration, Math.max(this.duration, 0));
		} else {
			this.duration = 0;
		}
	}
	
	public void setDurationConsume(boolean set) {
		this.consumeDuration = set;
	}
	
	@SideOnly(Side.CLIENT)
	public void execute(ClientPlayerData executer) {
		if(this.canExecute(executer)) {
			this.containingSkill.executeOnClient((ClientPlayerData)executer, this.containingSkill.gatherArguments(executer, ClientEngine.INSTANCE.inputController));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void cancel(ClientPlayerData executer, PacketBuffer pb) {
		this.containingSkill.cancelOnClient(executer, pb);
	}

	public boolean requestExecute(ServerPlayerData executer, PacketBuffer buf) {
		if (this.canExecute(executer)) {
			this.containingSkill.execute(this);
			this.containingSkill.executeOnServer(executer, buf);
			return true;
		}

		return false;
	}
	
	public NBTTagCompound getVariableNBT() {
		return this.skillVariables;
	}

	public float getRemainCooldown() {
		return this.cooldown;
	}

	public int getRemainDuration() {
		return this.duration;
	}

	public boolean canExecute(PlayerData<?> executer) {
		if(containingSkill == null) {
			return false;
		} else {
			return (this.stack > 0 || executer.getOriginalEntity().isCreative()) && containingSkill.canExecute(executer);
		}
	}

	public void update() {
		if (this.containingSkill != null)
			this.containingSkill.update(this);
	}

	public int getStack() {
		return this.stack;
	}

	public Skill getContaining() {
		return this.containingSkill;
	}

	public boolean hasSkill(Skill skill) {
		return this.containingSkill != null ? this.containingSkill.equals(skill) : false;
	}
	
	public float getCooldownRatio(float partialTicks) {
		return containingSkill != null && containingSkill.cooldown > 0 ? (prevCooldown + ((cooldown - prevCooldown) *
				partialTicks)) / containingSkill.cooldown : 0;
	}
	
	public float getCooldownSec() {
		return containingSkill != null ? containingSkill.cooldown - this.cooldown : 0;
	}

	public float getDurationRatio(float partialTicks) {
		return containingSkill != null && containingSkill.duration > 0 ? (prevDuration + ((duration - prevDuration) *
				partialTicks)) / containingSkill.duration : 0;
	}
}