package yesman.epicfight.skill;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.network.server.SPSetSkillValue;
import yesman.epicfight.network.server.SPSetSkillValue.Target;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillCancelEvent;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;
import yesman.epicfight.world.item.EpicFightCreativeTabs;

public abstract class Skill {
	public static class Builder<T extends Skill> {
		protected ResourceLocation registryName;
		protected SkillCategory category;
		protected ActivateType activateType;
		protected Resource resource;
		protected CreativeModeTab tab;
		
		public Builder<T> setRegistryName(ResourceLocation registryName) {
			this.registryName = registryName;
			return this;
		}
		
		public Builder<T> setCategory(SkillCategory category) {
			this.category = category;
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
		
		public Builder<T> setCreativeTab(CreativeModeTab tab) {
			this.tab = tab;
			return this;
		}
		
		public CreativeModeTab getCreativeTab() {
			return this.tab == null ? EpicFightCreativeTabs.ITEMS.get() : this.tab;
		}
		
		public ResourceLocation getRegistryName() {
			return this.registryName;
		}
		
		public boolean isLearnable() {
			return this.category.learnable();
		}
		
		public boolean hasCategory(SkillCategory category) {
			return this.category == category;
		}
	}
	
	public static Builder<Skill> createBuilder() {
		return new Builder<Skill>();
	}
	
	public static Skill.Builder<Skill> createIdentityBuilder() {
		return (new Skill.Builder<Skill>()).setCategory(SkillCategories.IDENTITY).setResource(Resource.NONE);
	}
	
	public static Skill.Builder<Skill> createMoverBuilder() {
		return (new Skill.Builder<Skill>()).setCategory(SkillCategories.MOVER).setResource(Resource.STAMINA);
	}
	
	protected final ResourceLocation registryName;
	protected final SkillCategory category;
	protected final ActivateType activateType;
	protected final Resource resource;
	protected float consumption;
	protected int maxDuration;
	protected int maxStackSize;
	protected int requiredXp;
	
	public Skill(Builder<? extends Skill> builder) {
		if (builder.registryName == null) {
			Exception e = new IllegalArgumentException("No registry name is given for " + this.getClass().getCanonicalName());
			e.printStackTrace();
		}
		
		this.registryName = builder.registryName;
		this.category = builder.category;
		this.activateType = builder.activateType;
		this.resource = builder.resource;
	}
	
	public void setParams(CompoundTag parameters) {
		this.consumption = parameters.getFloat("consumption");
		this.maxDuration = parameters.getInt("max_duration");
		this.maxStackSize = parameters.contains("max_stacks") ? parameters.getInt("max_stacks") : 1;
		this.requiredXp = parameters.getInt("xp_requirement");
	}
	
	public boolean isExecutableState(PlayerPatch<?> executer) {
		return !executer.getOriginal().isSpectator() && !executer.isUnstable() && executer.getEntityState().canUseSkill();
	}
	
	public boolean canExecute(PlayerPatch<?> executer) {
		return this.checkExecuteCondition(executer);
	}
	
	/**
	 * This makes the skill icon white if it returns false
	 */
	public boolean checkExecuteCondition(PlayerPatch<?> executer) {
		return true;
	}
	
	/**
	 * Get a packet to send to the server
	 */
	@OnlyIn(Dist.CLIENT)
	public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
		return new CPExecuteSkill(executer.getSkill(this).getSlotId(), CPExecuteSkill.WorkType.ACTIVATE, args);
	}
	
	@OnlyIn(Dist.CLIENT)
	public FriendlyByteBuf gatherArguments(LocalPlayerPatch executer, ControllEngine controllEngine) {
		return null;
	}

	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		SPSkillExecutionFeedback feedbackPacket = SPSkillExecutionFeedback.executed(executer.getSkill(this).getSlotId());
		SkillContainer container = executer.getSkill(this);
		
		if (executer.isChargingSkill()) {
			if (this instanceof ChargeableSkill chargingSkill) {
				feedbackPacket.getBuffer().writeInt(executer.getAccumulatedChargeAmount());
				chargingSkill.castSkill(executer, container, executer.getAccumulatedChargeAmount(), feedbackPacket, false);
				executer.resetSkillCharging();
				
				EpicFightNetworkManager.sendToPlayer(feedbackPacket, executer.getOriginal());
			}
		} else {
			SkillConsumeEvent event = new SkillConsumeEvent(executer, this, this.resource, true);
			executer.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
			
			if (!event.isCanceled()) {
				event.getResourceType().consumer.consume(this, executer, event.getAmount());
			}
			
			container.activate();
			EpicFightNetworkManager.sendToPlayer(feedbackPacket, executer.getOriginal());
		}
	}
	
	public void cancelOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		SkillCancelEvent skillCancelEvent = new SkillCancelEvent(executer, executer.getSkill(this));
		executer.getEventListener().triggerEvents(EventType.SKILL_CANCEL_EVENT, skillCancelEvent);

		EpicFightNetworkManager.sendToPlayer(SPSkillExecutionFeedback.expired(executer.getSkill(this).getSlotId()), executer.getOriginal());
	}

	public float getDefaultConsumeptionAmount(PlayerPatch<?> executer) {
		switch(this.resource) {
		case STAMINA:
			return executer.getModifiedStaminaConsume(this.consumption);
		case WEAPON_INNATE_ENERGY:
			return executer.getSkill(this).stack;
		case COOLDOWN:
			return executer.getSkill(this).stack;
		default:
			return 0.0F;
		}
	}

	/**
	 * Instant feedback when the skill is executed successfully
	 * @param executer
	 * @param args
	 */
	@OnlyIn(Dist.CLIENT)
	public void executeOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
	}
	
	/**
	 * Called when the duration ends.
	 * @param executer
	 * @param args
	 */
	@OnlyIn(Dist.CLIENT)
	public void cancelOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
		SkillCancelEvent skillCancelEvent = new SkillCancelEvent(executer, executer.getSkill(this));
		executer.getEventListener().triggerEvents(EventType.SKILL_CANCEL_EVENT, skillCancelEvent);
	}
	
	public void onInitiate(SkillContainer container) {
		container.maxDuration = this.maxDuration;
	}
	
	/**
	 * When skill removed from the container
	 * @param container
	 */
	public void onRemoved(SkillContainer container) {
	}
	
	/**
	 * When stacks reach to zero
	 * @param container
	 */
	public void onReset(SkillContainer container) {
	}
	
	public void setConsumption(SkillContainer container, float value) {
		container.resource = Math.min(Math.max(value, 0), container.getMaxResource());
		
		if (value >= container.getMaxResource()) {
			if (container.stack < this.maxStackSize) {
				container.stack++;	
				container.resource = 0;
				container.prevResource = 0;
			} else {
				container.resource = container.getMaxResource();
				container.prevResource = container.getMaxResource();
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
			if (container.stack < this.maxStackSize) {
				container.setResource(container.resource + this.getCooldownRegenPerSecond(executer) * EpicFightOptions.A_TICK);
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
				if (!container.getExecuter().isLogicalClient() && this.activateType != ActivateType.CHARGING) {
					this.cancelOnServer((ServerPlayerPatch)executer, null);
				}
				
				container.deactivate();
			}
		}
		
		if (this.activateType == Skill.ActivateType.CHARGING && container.getExecuter().getChargingSkill() == this) {
			ChargeableSkill chargingSkill = (ChargeableSkill)this;
			chargingSkill.chargingTick(executer);
			
			if (!container.getExecuter().isLogicalClient()) {
				container.getExecuter().resetActionTick();
				
				if (container.getExecuter().getSkillChargingTicks(1.0F) > chargingSkill.getAllowedMaxChargingTicks()) {
					SPSkillExecutionFeedback feedbackPacket = SPSkillExecutionFeedback.executed(executer.getSkill(this).getSlotId());
					feedbackPacket.getBuffer().writeInt(executer.getAccumulatedChargeAmount());
					chargingSkill.castSkill((ServerPlayerPatch)executer, container, container.getExecuter().getAccumulatedChargeAmount(), feedbackPacket, true);
					container.getExecuter().resetSkillCharging();
					
					EpicFightNetworkManager.sendToPlayer(feedbackPacket, (ServerPlayer)container.getExecuter().getOriginal());
				}
			}
		}
	}

	public void setConsumptionSynchronize(ServerPlayerPatch executer, float amount) {
		setConsumptionSynchronize(executer, this, amount);
	}
	
	public void setMaxDurationSynchronize(ServerPlayerPatch executer, int amount) {
		setMaxDurationSynchronize(executer, this, amount);
	}
	
	public void setDurationSynchronize(ServerPlayerPatch executer, int amount) {
		setDurationSynchronize(executer, this, amount);
	}
	
	public void setStackSynchronize(ServerPlayerPatch executer, int amount) {
		setStackSynchronize(executer, this, amount);
	}
	
	public void setMaxResourceSynchronize(ServerPlayerPatch executer, float amount) {
		setMaxResourceSynchronize(executer, this, amount);
	}
	
	public static void setConsumptionSynchronize(ServerPlayerPatch executer, Skill skill, float amount) {
		SkillContainer skillContainer = executer.getSkill(skill);
		skillContainer.setResource(amount);
		
		EpicFightNetworkManager.sendToPlayer(new SPSetSkillValue(Target.COOLDOWN, skillContainer.getSlotId(), amount, false), executer.getOriginal());
	}
	
	public static void setDurationSynchronize(ServerPlayerPatch executer, Skill skill, int amount) {
		SkillContainer skillContainer = executer.getSkill(skill);
		skillContainer.setDuration(amount);
		
		EpicFightNetworkManager.sendToPlayer(new SPSetSkillValue(Target.DURATION, skillContainer.getSlotId(), amount, false), executer.getOriginal());
	}
	
	public static void setMaxDurationSynchronize(ServerPlayerPatch executer, Skill skill, int amount) {
		SkillContainer skillContainer = executer.getSkill(skill);
		skillContainer.setMaxDuration(amount);
		
		EpicFightNetworkManager.sendToPlayer(new SPSetSkillValue(Target.MAX_DURATION, skillContainer.getSlotId(), amount, false), executer.getOriginal());
	}
	
	public static void setStackSynchronize(ServerPlayerPatch executer, Skill skill, int amount) {
		SkillContainer skillContainer = executer.getSkill(skill);
		skillContainer.setStack(amount);
		
		EpicFightNetworkManager.sendToPlayer(new SPSetSkillValue(Target.STACK, skillContainer.getSlotId(), amount, false), executer.getOriginal());
	}
	
	public static void setMaxResourceSynchronize(ServerPlayerPatch executer, Skill skill, float amount) {
		SkillContainer skillContainer = executer.getSkill(skill);
		skillContainer.setMaxResource(amount);
		
		EpicFightNetworkManager.sendToPlayer(new SPSetSkillValue(Target.MAX_RESOURCE, skillContainer.getSlotId(), amount, false), executer.getOriginal());
	}
	
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}
	
	public String getTranslationKey() {
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
	
	public int getMaxDuration() {
		return this.maxDuration;
	}

	public float getConsumption() {
		return this.consumption;
	}
	
	public int getRequiredXp() {
		return this.requiredXp;
	}
	
	public boolean resourcePredicate(PlayerPatch<?> playerpatch) {
		float consumption = this.getDefaultConsumeptionAmount(playerpatch);

		SkillConsumeEvent event = new SkillConsumeEvent(playerpatch, this, this.resource, consumption, false);
		playerpatch.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
		
		if (event.isCanceled()) {
			return false;
		}
		
		return event.getResourceType().predicate.canExecute(this, playerpatch, event.getAmount());
	}
	
	public boolean shouldDeactivateAutomatically(PlayerPatch<?> executer) {
		return !executer.getOriginal().isCreative();
	}
	
	public ActivateType getActivateType() {
		return this.activateType;
	}
	
	public Resource getResourceType() {
		return this.resource;
	}

	public Skill getPriorSkill() {
		return null;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void onScreen(LocalPlayerPatch playerpatch, float resolutionX, float resolutionY) {
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
		return Lists.newArrayList();
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		return list;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
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
		return Component.translatable(String.format("%s.%s.%s", "skill", this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));
	}
	
	public enum ActivateType {
		ONE_SHOT, DURATION, DURATION_INFINITE, TOGGLE, CHARGING
	}
	
	public enum Resource {
		NONE(
			(skill, playerpatch, amount) -> true,
			(skill, playerpatch, amount) -> {}
		),
		
		WEAPON_INNATE_ENERGY(
			(skill, playerpatch, amount) -> amount > 0,
			(skill, playerpatch, amount) -> {
				skill.setStackSynchronize(playerpatch, playerpatch.getSkill(skill).getStack() - 1);
				skill.setDurationSynchronize(playerpatch, skill.maxDuration);
			}
		),
		
		COOLDOWN(
			(skill, playerpatch, amount) -> amount > 0,
			(skill, playerpatch, amount) -> {
				skill.setConsumptionSynchronize(playerpatch, 0);
				skill.setStackSynchronize(playerpatch, playerpatch.getSkill(skill).getStack() - 1);
				skill.setDurationSynchronize(playerpatch, skill.maxDuration);
			}
		),
		
		STAMINA(
			(skill, playerpatch, amount) -> playerpatch.hasStamina(amount),
			(skill, playerpatch, amount) -> {
				playerpatch.consumeStamina(amount);
				skill.setDurationSynchronize(playerpatch, skill.maxDuration);
			}
		),

		HEALTH(
			(skill, playerpatch, amount) -> playerpatch.getOriginal().getHealth() > amount,
			(skill, playerpatch, amount) -> {
				playerpatch.getOriginal().setHealth(playerpatch.getOriginal().getHealth() - amount);
				skill.setDurationSynchronize(playerpatch, skill.maxDuration);
			}
		);
		
		public final ResourcePredicate predicate;
		public final ResourceConsumer consumer;
		
		Resource(ResourcePredicate predicate, ResourceConsumer consumer) {
			this.predicate = predicate;
			this.consumer = consumer;
		}

		@FunctionalInterface
		public interface ResourcePredicate {
			boolean canExecute(Skill skill, PlayerPatch<?> playerpatch, float amount);
		}

		@FunctionalInterface
		public interface ResourceConsumer {
			void consume(Skill skill, ServerPlayerPatch playerpatch, float amount);
		}
	}
}