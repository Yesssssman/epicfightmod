package yesman.epicfight.skill;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCSetSkillValue;
import yesman.epicfight.network.server.STCSetSkillValue.Target;
import yesman.epicfight.network.server.STCSkillExecutionFeedback;
import yesman.epicfight.utils.math.Formulars;

public abstract class Skill {
	public static class Builder<T extends Skill> {
		protected final ResourceLocation registryName;
		protected SkillCategory category;
		protected float consumption;
		protected int maxDuration;
		protected int maxStack;
		protected int requiredXp;
		protected ActivateType activateType;
		protected Resource resource;
		
		public Builder(ResourceLocation resourceLocation) {
			this.registryName = resourceLocation;
			this.maxDuration = 0;
			this.maxStack = 1;
		}
		
		public Builder<T> setCategory(SkillCategory category) {
			this.category = category;
			return this;
		}
		
		public Builder<T> setConsumption(float consumption) {
			this.consumption = consumption;
			return this;
		}
		
		public Builder<T> setMaxDuration(int maxDuration) {
			this.maxDuration = maxDuration;
			return this;
		}
		
		public Builder<T> setMaxStack(int maxStack) {
			this.maxStack = maxStack;
			return this;
		}
		
		public Builder<T> setRequiredXp(int requiredXp) {
			this.requiredXp = requiredXp;
			return this;
		}
		
		public Builder<T> setActivateType(ActivateType activateType) {
			this.activateType = activateType;
			return this;
		}
		
		public Builder<T> setResource(Resource resource) {
			this.resource = resource;
			return this;
		}
	}
	
	public static Builder<? extends Skill> createBuilder(ResourceLocation registryName) {
		return new Builder<Skill>(registryName);
	}
	
	protected final ResourceLocation registryName;
	protected final SkillCategory category;
	protected final float consumption;
	protected final int maxDuration;
	protected final int maxStackSize;
	protected final int requiredXp;
	protected final ActivateType activateType;
	protected final Resource resource;
	
	public Skill(Builder<? extends Skill> builder) {
		this.registryName = builder.registryName;
		this.category = builder.category;
		this.consumption = builder.consumption;
		this.maxDuration = builder.maxDuration;
		this.maxStackSize = builder.maxStack;
		this.requiredXp = builder.requiredXp;
		this.activateType = builder.activateType;
		this.resource = builder.resource;
	}
	
	@OnlyIn(Dist.CLIENT)
	public PacketBuffer gatherArguments(ClientPlayerData executer, ControllEngine controllEngine) {
		return null;
	}
	
	public boolean isExecutableState(PlayerData<?> executer) {
		executer.updateEntityState();
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginalEntity().isElytraFlying() || executer.currentMotion == LivingMotion.FALL || !playerState.canExecuteSkill());
	}
	
	public boolean canExecute(PlayerData<?> executer) {
		return true;
	}
	
	/**
	 * Gather arguments in client side and send packet to server.
	 * Process the skill execution with given arguments.
	 */
	@OnlyIn(Dist.CLIENT)
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {
		
	}
	
	public void cancelOnServer(ServerPlayerData executer, PacketBuffer args) {
		ModNetworkManager.sendToPlayer(new STCSkillExecutionFeedback(this.category.getIndex(), false), executer.getOriginalEntity());
	}
	
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		this.resource.consume.accept(this, executer);
		executer.getSkill(this.category).activate();
	}
	
	public void cancelOnClient(ClientPlayerData executer, PacketBuffer args) {
		
	}
	
	public void onInitiate(SkillContainer container) {
		container.maxDuration = this.maxDuration;
	}
	
	/**
	 * when Skill removed from the container
	 * @param container
	 */
	public void onRemoved(SkillContainer container) {
		
	}
	
	/**
	 * when duration or stack reach zero
	 * @param container
	 */
	public void onReset(SkillContainer container) {
		
	}
	
	public void setConsumption(SkillContainer container, float value) {
		container.resource = Math.min(Math.max(value, 0), this.consumption);
		if (value >= this.consumption) {
			if (container.stack < this.maxStackSize) {
				container.stack++;	
				container.resource = 0;
				container.prevResource = 0;
			} else {
				container.resource = this.consumption;
				container.prevResource = this.consumption;
			}
		} else if (value == 0 && container.stack > 0) {
			--container.stack;
		}
	}
	
	public void updateContainer(SkillContainer container) {
		PlayerData<?> executer = container.executer;
		container.prevResource = container.resource;
		container.prevDuration = container.duration;
		
		if (this.resource == Resource.COOLDOWN) {
			if (container.stack < container.containingSkill.maxStackSize) {
				container.setResource(container.resource + this.getCooldownRegenPerSecond(executer) * ConfigurationIngame.A_TICK);
			}
		}
		
		if (container.isActivated()) {
			if (this.activateType == ActivateType.DURATION) {
				container.duration--;
			}
			
			boolean isEnd = false;
			
			if (this.activateType == ActivateType.TOGGLE) {
				if (container.stack <= 0 && !executer.getOriginalEntity().isCreative()) {
					isEnd = true;
				}
			} else {
				if (container.duration <= 0) {
					isEnd = true;
				}
			}
			
			if (isEnd) {
				if(!container.executer.isRemote()) {
					container.containingSkill.cancelOnServer((ServerPlayerData)executer, null);
				}
				container.deactivate();
			}
		}
	}

	public void setConsumptionSynchronize(ServerPlayerData executer, float amount) {
		setConsumptionSynchronize(executer, this.category, amount);
	}

	public void setDurationSynchronize(ServerPlayerData executer, int amount) {
		setDurationSynchronize(executer, this.category, amount);
	}
	
	public void setStackSynchronize(ServerPlayerData executer, int amount) {
		setStackSynchronize(executer, this.category, amount);
	}
	
	public static void setConsumptionSynchronize(ServerPlayerData executer, SkillCategory slot, float amount) {
		executer.getSkill(slot).setResource(amount);
		ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.COOLDOWN, slot.getIndex(), amount, false), executer.getOriginalEntity());
	}
	
	public static void setDurationSynchronize(ServerPlayerData executer, SkillCategory slot, int amount) {
		executer.getSkill(slot).setDuration(amount);
		ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.DURATION, slot.getIndex(), amount, false), executer.getOriginalEntity());
	}
	
	public static void setStackSynchronize(ServerPlayerData executer, SkillCategory slot, int amount) {
		executer.getSkill(slot).setStack(amount);
		ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.STACK, slot.getIndex(), amount, false), executer.getOriginalEntity());
	}
	
	@OnlyIn(Dist.CLIENT)
	public void onScreen(ClientPlayerData playerdata, float resolutionX, float resolutionY) {
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerData) {
		return Lists.<ITextComponent>newArrayList();
	}
	
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}
	
	public String getName() {
		return this.registryName.getPath();
	}
	
	public float getCooldownRegenPerSecond(PlayerData<?> player) {
		return 1.0F;
	}
	
	public SkillCategory getCategory() {
		return this.category;
	}
	
	public int getMaxStack() {
		return this.maxStackSize;
	}
	
	public float getConsumption() {
		return this.consumption;
	}
	
	public int getRequiredXp() {
		return this.requiredXp;
	}
	
	public boolean resourcePredicate(PlayerData<?> playerdata) {
		return this.resource.predicate.apply(this, playerdata);
	}
	
	public boolean shouldDeactivateAutomatically(PlayerData<?> executer) {
		return !executer.getOriginalEntity().isCreative();
	}
	
	public ActivateType getActivateType() {
		return this.activateType;
	}
	
	public Skill getPriorSkill() {
		return null;
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<Object> getTooltipArgs() {
		List<Object> list = Lists.newArrayList();
		list.add(ItemStack.DECIMALFORMAT.format(this.getConsumption()));
		return list;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void drawOnGui(BattleModeGui gui, SkillContainer container, MatrixStack matStackIn, float x, float y, float scale, int width, int height) {
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public ResourceLocation getSkillTexture() {
		ResourceLocation name = this.getRegistryName();
		return new ResourceLocation(name.getNamespace(), "textures/gui/skills/" + name.getPath() + ".png");
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean shouldDraw(SkillContainer container) {
		return false;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	public static enum ActivateType {
		PASSIVE, ONE_SHOT, DURATION, DURATION_INFINITE, TOGGLE;
	}
	
	public static enum Resource {
		NONE((skill, playerdata) -> true, (skill, playerdata) -> {}),
		SPECIAL_GAUAGE((skill, playerdata) -> {
			return playerdata.getSkill(skill.category).stack > 0;
		}, (skill, playerdata) -> {
			skill.setStackSynchronize(playerdata, playerdata.getSkill(skill.category).getStack() - 1);
			skill.setDurationSynchronize(playerdata, skill.maxDuration);
		}),
		COOLDOWN((skill, playerdata) -> {
			return playerdata.getSkill(skill.category).stack > 0;
		}, (skill, playerdata) -> {
			skill.setConsumptionSynchronize(playerdata, 0);
			skill.setStackSynchronize(playerdata, playerdata.getSkill(skill.category).getStack() - 1);
			skill.setDurationSynchronize(playerdata, skill.maxDuration);
		}),
		STAMINA((skill, playerdata) -> {
			return playerdata.getStamina() >= Formulars.getStaminarConsumePenalty(playerdata.getWeight(), skill.consumption, playerdata);
		}, (skill, playerdata) -> {
			playerdata.setStamina(playerdata.getStamina() - Formulars.getStaminarConsumePenalty(playerdata.getWeight(), skill.consumption, playerdata));
			skill.setDurationSynchronize(playerdata, skill.maxDuration);
		});
		
		BiFunction<Skill, PlayerData<?>, Boolean> predicate;
		BiConsumer<Skill, ServerPlayerData> consume;
		
		Resource(BiFunction<Skill, PlayerData<?>, Boolean> predicate, BiConsumer<Skill, ServerPlayerData> action) {
			this.predicate = predicate;
			this.consume = action;
		}
	}
}