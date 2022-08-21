package yesman.epicfight.skill;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class GuardSkill extends Skill {
	protected static final SkillDataKey<Integer> LAST_HIT_TICK = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	protected static final SkillDataKey<Float> PENALTY = SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);
	protected static final UUID EVENT_UUID = UUID.fromString("b422f7a0-f378-11eb-9a03-0242ac130003");
	
	public static class Builder extends Skill.Builder<GuardSkill> {
		protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardMotions = Maps.newHashMap();
		protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> advancedGuardMotions = Maps.newHashMap();
		protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardBreakMotions = Maps.newHashMap();
		
		public Builder(ResourceLocation resourceLocation) {
			super(resourceLocation);
		}
		
		public Builder setCategory(SkillCategory category) {
			this.category = category;
			return this;
		}
		
		public Builder setConsumption(float consumption) {
			this.consumption = consumption;
			return this;
		}
		
		public Builder setMaxDuration(int maxDuration) {
			this.maxDuration = maxDuration;
			return this;
		}
		
		public Builder setMaxStack(int maxStack) {
			this.maxStack = maxStack;
			return this;
		}
		
		public Builder setRequiredXp(int requiredXp) {
			this.requiredXp = requiredXp;
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
		
		public Builder addGuardMotion(WeaponCategory weaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation> function) {
			this.guardMotions.put(weaponCategory, function);
			return this;
		}
		
		public Builder addAdvancedGuardMotion(WeaponCategory weaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?> function) {
			this.advancedGuardMotions.put(weaponCategory, function);
			return this;
		}
		
		public Builder addGuardBreakMotion(WeaponCategory weaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation> function) {
			this.guardBreakMotions.put(weaponCategory, function);
			return this;
		}
	}
	
	public static GuardSkill.Builder createBuilder(ResourceLocation resourceLocation) {
		return (new GuardSkill.Builder(resourceLocation))
				.setCategory(SkillCategories.GUARD)
				.setMaxStack(0)
				.setActivateType(ActivateType.ONE_SHOT)
				.setResource(Resource.STAMINA)
				.addGuardMotion(WeaponCategories.AXE, (item, player) -> Animations.SWORD_GUARD_HIT)
				.addGuardMotion(WeaponCategories.GREATSWORD, (item, player) -> Animations.GREATSWORD_GUARD_HIT)
				.addGuardMotion(WeaponCategories.KATANA, (item, player) -> Animations.KATANA_GUARD_HIT)
				.addGuardMotion(WeaponCategories.LONGSWORD, (item, player) -> Animations.LONGSWORD_GUARD_HIT)
				.addGuardMotion(WeaponCategories.SPEAR, (item, player) -> item.getStyle(player) == Styles.TWO_HAND ? Animations.SPEAR_GUARD_HIT : null)
				.addGuardMotion(WeaponCategories.SWORD, (item, player) -> item.getStyle(player) == Styles.ONE_HAND ? Animations.SWORD_GUARD_HIT : Animations.SWORD_DUAL_GUARD_HIT)
				.addGuardMotion(WeaponCategories.TACHI, (item, player) -> Animations.LONGSWORD_GUARD_HIT)
				.addGuardBreakMotion(WeaponCategories.AXE, (item, player) -> Animations.COMMON_GUARD_BREAK)
				.addGuardBreakMotion(WeaponCategories.GREATSWORD, (item, player) -> Animations.GREATSWORD_GUARD_BREAK)
				.addGuardBreakMotion(WeaponCategories.KATANA, (item, player) -> Animations.COMMON_GUARD_BREAK)
				.addGuardBreakMotion(WeaponCategories.LONGSWORD, (item, player) -> Animations.COMMON_GUARD_BREAK)
				.addGuardBreakMotion(WeaponCategories.SPEAR, (item, player) -> Animations.COMMON_GUARD_BREAK)
				.addGuardBreakMotion(WeaponCategories.SWORD, (item, player) -> Animations.COMMON_GUARD_BREAK)
				.addGuardBreakMotion(WeaponCategories.TACHI, (item, player) -> Animations.COMMON_GUARD_BREAK);
	}
	
	protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardMotions;
	protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> advancedGuardMotions;
	protected final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardBreakMotions;
	
	public GuardSkill(GuardSkill.Builder builder) {
		super(builder);
		this.guardMotions = builder.guardMotions;
		this.advancedGuardMotions = builder.advancedGuardMotions;
		this.guardBreakMotions = builder.guardBreakMotions;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(LAST_HIT_TICK);
		container.getDataManager().registerData(PENALTY);
		
		container.getExecuter().getEventListener().addEventListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
			
			if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && this.isExecutableState(event.getPlayerPatch())) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
			
			if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && this.isExecutableState(event.getPlayerPatch())) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_STOP_EVENT, EVENT_UUID, (event) -> {
			ServerPlayer serverplayer = event.getPlayerPatch().getOriginal();
			container.getDataManager().setDataSync(LAST_HIT_TICK, serverplayer.tickCount, serverplayer);
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(PENALTY, 0.0F, event.getPlayerPatch().getOriginal());
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(event.getPlayerPatch().getOriginal().getUsedItemHand());
			
			if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && event.getPlayerPatch().getOriginal().isUsingItem() && this.isExecutableState(event.getPlayerPatch())) {
				DamageSource damageSource = event.getDamageSource();
				boolean isFront = false;
				Vec3 sourceLocation = damageSource.getSourcePosition();
				
				if (sourceLocation != null) {
					Vec3 viewVector = event.getPlayerPatch().getOriginal().getViewVector(1.0F);
					Vec3 toSourceLocation = sourceLocation.subtract(event.getPlayerPatch().getOriginal().position()).normalize();
					
					if (toSourceLocation.dot(viewVector) > 0.0D) {
						isFront = true;
					}
				}
				
				if (isFront) {
					float impact = 0.5F;
					float knockback = 0.25F;
					
					if (event.getDamageSource() instanceof ExtendedDamageSource) {
						impact = ((ExtendedDamageSource)event.getDamageSource()).getImpact();
						knockback += Math.min(impact * 0.1F, 1.0F);
					}
					
					this.guard(container, itemCapability, event, knockback, impact, false);
				}
			}
		}, 1);
	}
	
	public void guard(SkillContainer container, CapabilityItem itemCapability, HurtEvent.Pre event, float knockback, float impact, boolean advanced) {
		DamageSource damageSource = event.getDamageSource();
		
		if (this.isBlockableSource(damageSource, advanced)) {
			event.getPlayerPatch().playSound(EpicFightSounds.CLASH, -0.05F, 0.1F);
			ServerPlayer serveerPlayer = event.getPlayerPatch().getOriginal();
			EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(((ServerLevel)serveerPlayer.level), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serveerPlayer, damageSource.getDirectEntity());
			
			if (damageSource.getDirectEntity() instanceof LivingEntity) {
				knockback += EnchantmentHelper.getKnockbackBonus((LivingEntity)damageSource.getDirectEntity()) * 0.1F;
			}
			
			float penalty = container.getDataManager().getDataValue(PENALTY) + this.getPenaltyMultiplier(itemCapability);
			event.getPlayerPatch().knockBackEntity(damageSource.getDirectEntity().position(), knockback);
			
			float stamina = event.getPlayerPatch().getStamina() - penalty * impact;
			event.getPlayerPatch().setStamina(stamina);
			
			container.getDataManager().setDataSync(PENALTY, penalty, event.getPlayerPatch().getOriginal());
			
			BlockType blockType = (stamina >= 0.0F) ? BlockType.GUARD : BlockType.GUARD_BREAK;
			StaticAnimation animation = this.getGuardMotion(event.getPlayerPatch(), itemCapability, blockType);
			
			if (animation != null) {
				event.getPlayerPatch().playAnimationSynchronized(animation, 0.0F);
			}
			
			if (blockType == BlockType.GUARD_BREAK) {
				event.getPlayerPatch().playSound(EpicFightSounds.NEUTRALIZE_MOBS, 3.0F, 0.0F, 0.1F);
			}
			
			this.dealEvent(event.getPlayerPatch(), event);
		}
	}
	
	public void dealEvent(PlayerPatch<?> playerpatch, HurtEvent.Pre event) {
		event.setCanceled(true);
		event.setResult(AttackResult.ResultType.BLOCKED);
		
		Entity directEntity = event.getDamageSource().getDirectEntity();
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)directEntity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		
		if (entitypatch != null) {
			entitypatch.onAttackBlocked(event, playerpatch);
		}
	}
	
	protected float getPenaltyMultiplier(CapabilityItem itemCapapbility) {
		return 0.6F;
	}
	
	protected Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> getGuradMotionMap(BlockType blockType) {
		switch (blockType) {
		case GUARD_BREAK:
			return this.guardBreakMotions;
		case GUARD:
			return this.guardMotions;
		case ADVANCED_GUARD:
			return this.advancedGuardMotions;
		default:
			throw new IllegalArgumentException("unsupported block type " + blockType);
		}
	}
	
	protected boolean isHoldingWeaponAvailable(PlayerPatch<?> playerpatch, CapabilityItem itemCapability, BlockType blockType) {
		Map<WeaponCategory, BiFunction<CapabilityItem, PlayerPatch<?>, ?>> guardMotions = this.getGuradMotionMap(blockType);
		
		if (!guardMotions.containsKey(itemCapability.getWeaponCategory())) {
			return false;
		}
		
		Object motion = guardMotions.get(itemCapability.getWeaponCategory()).apply(itemCapability, playerpatch);
		return motion != null;
	}
	
	/**
	 * Not safe from null pointer exception
	 * Must call isAvailableState first to check if it's safe
	 * 
	 * @param metadata 0: guard breaks, 1: normal guards, 2: reinforced guards
	 * @return StaticAnimation
	 */
	@Nullable
	protected StaticAnimation getGuardMotion(PlayerPatch<?> playerpatch, CapabilityItem itemCapability, BlockType blockType) {
		return (StaticAnimation)this.getGuradMotionMap(blockType).getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null).apply(itemCapability, playerpatch);
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		
		if (!container.getExecuter().isLogicalClient() && !container.getExecuter().getOriginal().isUsingItem()) {
			float penalty = container.getDataManager().getDataValue(PENALTY);
			
			if (penalty > 0) {
				int hitTick = container.getDataManager().getDataValue(LAST_HIT_TICK);
				
				if (container.getExecuter().getOriginal().tickCount - hitTick > 40) {
					container.getDataManager().setDataSync(PENALTY, 0.0F, (ServerPlayer)container.getExecuter().getOriginal());
				}
			}
		} else {
			container.getExecuter().resetActionTick();
		}
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID, 1);
		container.getExecuter().getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_STOP_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		return !(executer.isUnstable() || playerState.hurt()) && executer.isBattleMode();
	}
	
	protected boolean isBlockableSource(DamageSource damageSource, boolean advanced) {
		return !damageSource.isBypassInvul() && !damageSource.isBypassArmor() && !damageSource.isProjectile() && !damageSource.isExplosion() && !damageSource.isMagic() && !damageSource.isFire();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgs() {
		List<Object> list = Lists.newArrayList();
		list.add(String.format("%s, %s, %s, %s, %s, %s, %s", this.guardMotions.keySet().toArray(new Object[0])).toLowerCase());
		return list;
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean shouldDraw(SkillContainer container) {
		return container.getDataManager().getDataValue(PENALTY) > 0.0F;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack matStackIn, float x, float y, float scale, int width, int height) {
		matStackIn.pushPose();
		matStackIn.scale(scale, scale, 1.0F);
		matStackIn.translate(0, (float)gui.getSlidingProgression() * 1.0F / scale, 0);
		RenderSystem.setShaderTexture(0, Skills.GUARD.getSkillTexture());
		float scaleMultiply = 1.0F / scale;
		gui.drawTexturedModalRectFixCoord(matStackIn.last().pose(), (width - x) * scaleMultiply, (height - y) * scaleMultiply, 0, 0, 255, 255);
		matStackIn.scale(scaleMultiply, scaleMultiply, 1.0F);
		gui.font.drawShadow(matStackIn, String.format("x%.1f", container.getDataManager().getDataValue(PENALTY)), ((float)width - x), ((float)height - y+6), 16777215);
	}
	
	protected boolean isAdvancedGuard() {
		return false;
	}
	
	protected static enum BlockType {
		GUARD_BREAK, GUARD, ADVANCED_GUARD
	}
}