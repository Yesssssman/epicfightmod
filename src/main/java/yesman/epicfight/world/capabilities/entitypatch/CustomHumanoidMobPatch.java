package yesman.epicfight.world.capabilities.entitypatch;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.AnimatedAttackGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.TargetChasingGoal;

public class CustomHumanoidMobPatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {
	private final MobPatchReloadListener.CustomHumanoidMobPatchProvider provider;

	public CustomHumanoidMobPatch(Faction faction, MobPatchReloadListener.CustomHumanoidMobPatchProvider provider) {
		super(faction);
		this.provider = provider;
		this.weaponLivingMotions = this.provider.getHumanoidWeaponMotions();
		this.weaponAttackMotions = this.provider.getHumanoidCombatBehaviors();
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		if (!holdingRanedWeapon) {
			CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
			
			if (builder != null) {
				this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, builder.build(this)));
				this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.getOriginal(), this.provider.getChasingSpeed(), true));
			}
		}
	}
	
	@Override
	protected void setWeaponMotions() {
		
	}
	
	@Override
	protected void initAttributes() {
		this.original.getAttribute(EpicFightAttributes.WEIGHT.get()).setBaseValue(this.original.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 2.0D);
		this.original.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(this.provider.getAttributeValues().get(EpicFightAttributes.MAX_STRIKES.get()));
		this.original.getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).setBaseValue(this.provider.getAttributeValues().get(EpicFightAttributes.ARMOR_NEGATION.get()));
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(this.provider.getAttributeValues().get(EpicFightAttributes.IMPACT.get()));
		
		if (this.provider.getAttributeValues().containsKey(Attributes.ATTACK_DAMAGE)) {
			this.original.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.provider.getAttributeValues().get(Attributes.ATTACK_DAMAGE));
		}
	}
	
	@Override
	public void tick(LivingUpdateEvent event) {
		super.tick(event);
	}
	
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		for (Pair<LivingMotion, StaticAnimation> pair : this.provider.getDefaultAnimations()) {
			clientAnimator.addLivingAnimation(pair.getFirst(), pair.getSecond());
		}
		
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveMobUpdateMotion(considerInaction);
		
		if (this.original.isUsingItem()) {
			CapabilityItem activeItem = this.getHoldingItemCapability(this.original.getUsedItemHand());
			UseAnim useAnim = this.original.getItemInHand(this.original.getUsedItemHand()).getUseAnimation();
			UseAnim secondUseAnim = activeItem.getUseAnimation(this);
			
			if (useAnim == UseAnim.BLOCK || secondUseAnim == UseAnim.BLOCK)
				if (activeItem.getWeaponCategory() == WeaponCategory.SHIELD)
					currentCompositeMotion = LivingMotions.BLOCK_SHIELD;
				else
					currentCompositeMotion = LivingMotions.BLOCK;
			else if (useAnim == UseAnim.BOW || useAnim == UseAnim.SPEAR)
				currentCompositeMotion = LivingMotions.AIM;
			else if (useAnim == UseAnim.CROSSBOW)
				currentCompositeMotion = LivingMotions.RELOAD;
			else
				currentCompositeMotion = currentLivingMotion;
		} else {
			if (CrossbowItem.isCharged(this.original.getMainHandItem()))
				currentCompositeMotion = LivingMotions.AIM;
			else if (this.getClientAnimator().getCompositeLayer(Layer.Priority.MIDDLE).animationPlayer.getPlay().isReboundAnimation())
				currentCompositeMotion = LivingMotions.NONE;
			else if (this.original.swinging && this.original.getSleepingPos().isEmpty())
				currentCompositeMotion = LivingMotions.DIGGING;
			else
				currentCompositeMotion = currentLivingMotion;
			
			if (this.getClientAnimator().isAiming() && currentCompositeMotion != LivingMotions.AIM) {
				this.playReboundAnimation();
			}
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.get(this.provider.getModelLocation());
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return this.provider.getStunAnimations().get(stunType);
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		float scale = this.provider.getScale();
		return super.getModelMatrix(partialTicks).scale(scale, scale, scale);
	}
}