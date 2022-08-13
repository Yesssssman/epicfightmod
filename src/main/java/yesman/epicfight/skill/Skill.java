package yesman.epicfight.skill;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.math.Formulars;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.network.server.SPSetSkillValue;
import yesman.epicfight.network.server.SPSetSkillValue.Target;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

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
	public FriendlyByteBuf gatherArguments(LocalPlayerPatch executer, ControllEngine controllEngine) {
		return null;
	}
	
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		return !(executer.isUnstable() || !playerState.canUseSkill());
	}
	
	public boolean canExecute(PlayerPatch<?> executer) {
		return true;
	}
	
	/**
	 * Get packet to send to the server
	 */
	@OnlyIn(Dist.CLIENT)
	public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
		return new CPExecuteSkill(this.category.universalOrdinal(), true, args);
	}
	
	public void cancelOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		EpicFightNetworkManager.sendToPlayer(new SPSkillExecutionFeedback(this.category.universalOrdinal(), false), executer.getOriginal());
	}
	
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		this.resource.consume.accept(this, executer);
		executer.getSkill(this.category).activate();
	}
	
	public void cancelOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
		
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
		PlayerPatch<?> executer = container.getExecuter();
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
				if (container.stack <= 0 && !executer.getOriginal().isCreative()) {
					isEnd = true;
				}
			} else {
				if (container.duration <= 0) {
					isEnd = true;
				}
			}
			
			if (isEnd) {
				if (!container.getExecuter().isLogicalClient()) {
					container.containingSkill.cancelOnServer((ServerPlayerPatch)executer, null);
				}
				container.deactivate();
			}
		}
	}

	public void setConsumptionSynchronize(ServerPlayerPatch executer, float amount) {
		setConsumptionSynchronize(executer, this.category, amount);
	}
	
	public void setMaxDurationSynchronize(ServerPlayerPatch executer, int amount) {
		setMaxDurationSynchronize(executer, this.category, amount);
	}
	
	public void setDurationSynchronize(ServerPlayerPatch executer, int amount) {
		setDurationSynchronize(executer, this.category, amount);
	}
	
	public void setStackSynchronize(ServerPlayerPatch executer, int amount) {
		setStackSynchronize(executer, this.category, amount);
	}
	
	public static void setConsumptionSynchronize(ServerPlayerPatch executer, SkillCategory slot, float amount) {
		executer.getSkill(slot).setResource(amount);
		EpicFightNetworkManager.sendToPlayer(new SPSetSkillValue(Target.COOLDOWN, slot.universalOrdinal(), amount, false), executer.getOriginal());
	}
	
	public static void setDurationSynchronize(ServerPlayerPatch executer, SkillCategory slot, int amount) {
		executer.getSkill(slot).setDuration(amount);
		EpicFightNetworkManager.sendToPlayer(new SPSetSkillValue(Target.DURATION, slot.universalOrdinal(), amount, false), executer.getOriginal());
	}
	
	public static void setMaxDurationSynchronize(ServerPlayerPatch executer, SkillCategory slot, int amount) {
		executer.getSkill(slot).setMaxDuration(amount);
		EpicFightNetworkManager.sendToPlayer(new SPSetSkillValue(Target.MAX_DURATION, slot.universalOrdinal(), amount, false), executer.getOriginal());
	}
	
	public static void setStackSynchronize(ServerPlayerPatch executer, SkillCategory slot, int amount) {
		executer.getSkill(slot).setStack(amount);
		EpicFightNetworkManager.sendToPlayer(new SPSetSkillValue(Target.STACK, slot.universalOrdinal(), amount, false), executer.getOriginal());
	}
	
	@OnlyIn(Dist.CLIENT)
	public void onScreen(LocalPlayerPatch playerpatch, float resolutionX, float resolutionY) {
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
		return Lists.<Component>newArrayList();
	}
	
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}
	
	public String getTranslatableText() {
		return String.format("skill.%s.%s", this.getRegistryName().getNamespace(), this.getRegistryName().getPath());
	}
	
	public float getCooldownRegenPerSecond(PlayerPatch<?> player) {
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
	
	public boolean resourcePredicate(PlayerPatch<?> playerpatch) {
		return this.resource.predicate.apply(this, playerpatch);
	}
	
	public boolean shouldDeactivateAutomatically(PlayerPatch<?> executer) {
		return !executer.getOriginal().isCreative();
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
		list.add(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.getConsumption()));
		return list;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack matStackIn, float x, float y, float scale, int width, int height) {
		
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
		return this.getRegistryName().toString();
	}
	
	public Component getDisplayName() {
		return new TranslatableComponent(String.format("%s.%s.%s", "skill", this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));
	}
	
	public static enum ActivateType {
		PASSIVE, ONE_SHOT, DURATION, DURATION_INFINITE, TOGGLE;
	}
	
	public static enum Resource {
		NONE((skill, playerpatch) -> true, (skill, playerpatch) -> {}),
		SPECIAL_GAUAGE((skill, playerpatch) -> playerpatch.getSkill(skill.category).stack > 0, (skill, playerpatch) -> {
			skill.setStackSynchronize(playerpatch, playerpatch.getSkill(skill.category).getStack() - 1);
			skill.setDurationSynchronize(playerpatch, skill.maxDuration);
		}),
		COOLDOWN((skill, playerpatch) -> playerpatch.getSkill(skill.category).stack > 0, (skill, playerpatch) -> {
			skill.setConsumptionSynchronize(playerpatch, 0);
			skill.setStackSynchronize(playerpatch, playerpatch.getSkill(skill.category).getStack() - 1);
			skill.setDurationSynchronize(playerpatch, skill.maxDuration);
		}),
		STAMINA((skill, playerpatch) -> playerpatch.getStamina() >= Formulars.getStaminarConsumePenalty(playerpatch.getWeight(), skill.consumption, playerpatch), (skill, playerpatch) -> {
			playerpatch.setStamina(playerpatch.getStamina() - Formulars.getStaminarConsumePenalty(playerpatch.getWeight(), skill.consumption, playerpatch));
			skill.setDurationSynchronize(playerpatch, skill.maxDuration);
		});
		
		final BiFunction<Skill, PlayerPatch<?>, Boolean> predicate;
		final BiConsumer<Skill, ServerPlayerPatch> consume;
		
		Resource(BiFunction<Skill, PlayerPatch<?>, Boolean> predicate, BiConsumer<Skill, ServerPlayerPatch> action) {
			this.predicate = predicate;
			this.consume = action;
		}
	}
}