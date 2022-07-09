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
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.game.AttackResult;
import yesman.epicfight.api.utils.game.ExtendedDamageSource;
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
import yesman.epicfight.world.entity.eventlistener.HurtEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class GuardSkill extends Skill {
	protected static final SkillDataKey<Integer> LAST_HIT_TICK = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	protected static final SkillDataKey<Float> PENALTY = SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);
	public static final UUID EVENT_UUID = UUID.fromString("b422f7a0-f378-11eb-9a03-0242ac130003");
	protected static final Map<WeaponCategories, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation>> AVAILABLE_WEAPON_TYPES = Maps.<WeaponCategories, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation>>newLinkedHashMap();
	
	static {
		AVAILABLE_WEAPON_TYPES.put(WeaponCategories.AXE, (item, player) -> Animations.SWORD_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategories.GREATSWORD, (item, player) -> Animations.GREATSWORD_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategories.KATANA, (item, player) -> Animations.KATANA_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategories.LONGSWORD, (item, player) -> Animations.LONGSWORD_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategories.SPEAR, (item, player) -> item.getStyle(player) == Styles.TWO_HAND ? Animations.SPEAR_GUARD_HIT : null);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategories.SWORD, (item, player) -> item.getStyle(player) == Styles.ONE_HAND ? Animations.SWORD_GUARD_HIT : Animations.SWORD_DUAL_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategories.TACHI, (item, player) -> Animations.LONGSWORD_GUARD_HIT);
	}
	
	public static Skill.Builder<GuardSkill> createBuilder(ResourceLocation resourceLocation) {
		return (new Skill.Builder<GuardSkill>(resourceLocation)).setCategory(SkillCategories.GUARD).setMaxStack(0).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA);
	}
	
	public GuardSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(LAST_HIT_TICK);
		container.getDataManager().registerData(PENALTY);
		
		container.getExecuter().getEventListener().addEventListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
			
			if (AVAILABLE_WEAPON_TYPES.getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null).apply(itemCapability, event.getPlayerPatch()) != null && this.isExecutableState(event.getPlayerPatch())) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
			
			if (AVAILABLE_WEAPON_TYPES.getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null).apply(itemCapability, event.getPlayerPatch()) != null && this.isExecutableState(event.getPlayerPatch())) {
				event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_STOP_EVENT, EVENT_UUID, (event) -> {
			ServerPlayer serverplayerentity = event.getPlayerPatch().getOriginal();
			container.getDataManager().setDataSync(LAST_HIT_TICK, serverplayerentity.tickCount, serverplayerentity);
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(PENALTY, 0.0F, event.getPlayerPatch().getOriginal());
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(event.getPlayerPatch().getOriginal().getUsedItemHand());
			
			if (this.getHitMotion(event.getPlayerPatch(), itemCapability, 0) != null && event.getPlayerPatch().getOriginal().isUsingItem() && this.isExecutableState(event.getPlayerPatch())) {
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
	
	public void guard(SkillContainer container, CapabilityItem itemCapability, HurtEvent.Pre event, float knockback, float impact, boolean reinforced) {
		DamageSource damageSource = event.getDamageSource();
		
		if (this.isBlockableSource(damageSource, reinforced)) {
			event.getPlayerPatch().playSound(EpicFightSounds.CLASH, -0.05F, 0.1F);
			Entity playerentity = event.getPlayerPatch().getOriginal();
			EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(((ServerLevel)playerentity.level), HitParticleType.POSITION_FRONT_OF_EYE_POSITION, HitParticleType.ARGUMENT_ZERO, playerentity, damageSource.getDirectEntity());
			
			if (damageSource.getDirectEntity() instanceof LivingEntity) {
				knockback += EnchantmentHelper.getKnockbackBonus((LivingEntity)damageSource.getDirectEntity()) * 0.1F;
			}
			
			float penalty = container.getDataManager().getDataValue(PENALTY) + this.getPenaltyStamina(itemCapability);
			event.getPlayerPatch().knockBackEntity(damageSource.getDirectEntity().position(), knockback);
			float stamina = event.getPlayerPatch().getStamina() - penalty * impact;
			event.getPlayerPatch().setStamina(stamina);
			container.getDataManager().setDataSync(PENALTY, penalty, event.getPlayerPatch().getOriginal());
			
			StaticAnimation animation = this.getHitMotion(event.getPlayerPatch(), itemCapability, stamina >= 0 ? 1 : 0);
			
			if (animation != null) {
				event.getPlayerPatch().playAnimationSynchronized(animation, 0.0F);
			}
			
			if (stamina >= 0.0F) {
				this.dealEvent(event.getPlayerPatch(), event);
			}
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
	
	protected float getPenaltyStamina(CapabilityItem itemCapapbility) {
		return 0.6F;
	}
	
	protected Map<WeaponCategories, BiFunction<CapabilityItem, PlayerPatch<?>, StaticAnimation>> getAvailableWeaponTypes(int metadata) {
		return AVAILABLE_WEAPON_TYPES;
	}
	
	@Nullable
	protected StaticAnimation getHitMotion(PlayerPatch<?> playerpatch, CapabilityItem itemCapability, int meta) {
		return this.getAvailableWeaponTypes(meta).getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null).apply(itemCapability, playerpatch);
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
		return !(executer.getOriginal().isFallFlying() || executer.currentLivingMotion == LivingMotions.FALL || playerState.hurt()) && executer.isBattleMode();
	}
	
	protected boolean isBlockableSource(DamageSource damageSource, boolean specialSourceBlockCondition) {
		return !damageSource.isBypassInvul() && !damageSource.isBypassArmor() && !damageSource.isProjectile() && !damageSource.isExplosion() && !damageSource.isMagic() && !damageSource.isFire();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgs() {
		List<Object> list = Lists.<Object>newArrayList();
		list.add(String.format("%s, %s, %s, %s, %s, %s, %s", AVAILABLE_WEAPON_TYPES.keySet().toArray(new Object[0])).toLowerCase());
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
}