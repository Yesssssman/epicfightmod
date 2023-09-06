package yesman.epicfight.skill.identity;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class RevelationSkill extends Skill {
	private static final UUID EVENT_UUID = UUID.fromString("31a396ea-0361-11ee-be56-0242ac120002");
	
	public static RevelationSkill.Builder createRevelationSkillBuilder() {
		return (new Builder())
				.setCategory(SkillCategories.IDENTITY)
				.setActivateType(ActivateType.DURATION)
				.setResource(Resource.NONE)
				.addMotion(WeaponCategories.LONGSWORD, (item, player) -> Animations.REVELATION_TWOHAND)
				.addMotion(WeaponCategories.GREATSWORD, (item, player) -> Animations.REVELATION_TWOHAND)
				.addMotion(WeaponCategories.TACHI, (item, player) -> Animations.REVELATION_TWOHAND)
				;
	}
	
	public static class Builder extends Skill.Builder<RevelationSkill> {
		protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation>> motions = Maps.newHashMap();
		
		public Builder setCategory(SkillCategory category) {
			this.category = category;
			return this;
		}
		
		public Builder setActivateType(ActivateType activateType) {
			this.activateType = activateType;
			return this;
		}
		
		public Builder setResource(Resource resource) {
			this.resource = resource;
			return this;
		}
		
		public Builder setCreativeTab(CreativeModeTab tab) {
			this.tab = tab;
			return this;
		}
		
		public Builder addMotion(WeaponCategory weaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation> function) {
			this.motions.put(weaponCategory, function);
			return this;
		}
	}
	
	protected static final SkillDataKey<Integer> STACKS = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation>> motions;
	protected final Map<EntityType<?>, Integer> maxRevelationStacks = Maps.newHashMap();
	protected int blockStack;
	protected int parryStack;
	protected int dodgeStack;
	protected int defaultRevelationStacks;
	
	public RevelationSkill(Builder builder) {
		super(builder);
		
		this.motions = builder.motions;
	}
	
	@Override
	public void setParams(CompoundTag parameters) {
		super.setParams(parameters);
		
		this.maxRevelationStacks.clear();
		this.blockStack = parameters.getInt("block_stacks");
		this.parryStack = parameters.getInt("parry_stacks");
		this.dodgeStack = parameters.getInt("dodge_stacks");
		this.defaultRevelationStacks = parameters.getInt("default_revelation_stacks");
		
		CompoundTag maxStacks = parameters.getCompound("max_revelations");
		
		for (String registryName : maxStacks.getAllKeys()) {
			EntityType<?> entityType = EntityType.byString(registryName).orElse(null);
			
			if (entityType != null) {
				this.maxRevelationStacks.put(entityType, maxStacks.getInt(registryName));
			} else {
				EpicFightMod.LOGGER.warn("Revelation registry error: no entity type named : " + registryName);
				
			}
		}
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(STACKS);
		
		PlayerEventListener listener = container.getExecuter().getEventListener();
		
		listener.addEventListener(EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, (event) -> {
			if (container.getExecuter().isLogicalClient()) {
				Skill skill = event.getSkillContainer().getSkill();
				
				if (skill.getCategory() != SkillCategories.WEAPON_INNATE) {
					return;
				}
				
				if (container.getExecuter().getTarget() != null) {
					LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(container.getExecuter().getTarget(), LivingEntityPatch.class);
					
					if (entitypatch != null && container.isActivated()) {
						if (container.sendExecuteRequest((LocalPlayerPatch)container.getExecuter(), ClientEngine.getInstance().controllEngine).isExecutable()) {
							container.setDuration(0);
							event.setCanceled(true);
						}
					}
				}
			}
		});
		
		listener.addEventListener(EventType.SET_TARGET_EVENT, EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(STACKS, 0, event.getPlayerPatch().getOriginal());
		});
		
		listener.addEventListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event) -> {
			LivingEntity target = container.getExecuter().getTarget();
			
			if (target != null && target.is(event.getDamageSource().getDirectEntity())) {
				this.checkStackAndActivate(container, event.getPlayerPatch(), target, container.getDataManager().getDataValue(STACKS), this.dodgeStack);
			}
			
		}, -1);
		
		listener.addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			if (event.getResult() == ResultType.BLOCKED) {
				LivingEntity target = container.getExecuter().getTarget();
				
				if (target != null && target.is(event.getDamageSource().getDirectEntity())) {
					int stacks = event.isParried() ? this.parryStack : this.blockStack;
					
					this.checkStackAndActivate(container, event.getPlayerPatch(), target, container.getDataManager().getDataValue(STACKS), stacks);
				}
			}
		}, -1);
		
		listener.addEventListener(EventType.TARGET_INDICATOR_ALERT_CHECK_EVENT, EVENT_UUID, (event) -> {
			if (container.isActivated()) {
				event.setCanceled(false);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SET_TARGET_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.TARGET_INDICATOR_ALERT_CHECK_EVENT, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnServer(executer, args);
		
		CapabilityItem holdingItem = executer.getHoldingItemCapability(InteractionHand.MAIN_HAND);
		StaticAnimation animation = this.motions.containsKey(holdingItem.getWeaponCategory()) ? 
				this.motions.get(holdingItem.getWeaponCategory()).apply(holdingItem, executer) : Animations.REVELATION_ONEHAND;
		
		executer.playAnimationSynchronized(animation, 0.0F);
	}
	
	public void checkStackAndActivate(SkillContainer container, ServerPlayerPatch playerpatch, LivingEntity target, int stacks, int addStacks) {
		int maxStackSize = this.maxRevelationStacks.getOrDefault(target.getType(), this.defaultRevelationStacks);
		int plusStack = stacks + addStacks;
		
		if (plusStack < maxStackSize) {
			container.getDataManager().setDataSync(STACKS, plusStack, playerpatch.getOriginal());
		} else {
			if (!container.isActivated()) {
				this.setDurationSynchronize(playerpatch, this.maxDuration);
			}
			
			container.getDataManager().setDataSync(STACKS, 0, playerpatch.getOriginal());
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getExecuter().getTarget() != null;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
		poseStack.pushPose();
		poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
		RenderSystem.setShaderTexture(0, this.getSkillTexture());
		GuiComponent.blit(poseStack, (int)x, (int)y, 24, 24, 0, 0, 1, 1, 1, 1);
		int stacks = container.getRemainDuration() > 0 ? 0 : this.maxRevelationStacks.getOrDefault(container.getExecuter().getTarget().getType(), this.defaultRevelationStacks)
																- container.getDataManager().getDataValue(STACKS);
		gui.font.drawShadow(poseStack, String.format("%d", stacks), x + 18, y + 14, 16777215);
		poseStack.popPose();
	}
}