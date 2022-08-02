package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class WitherSkeletonPatch<T extends PathfinderMob> extends SkeletonPatch<T> {
	public WitherSkeletonPatch() {
		super(Faction.WITHER);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.STUN_ARMOR.get()).setBaseValue(6.0F);
	}
	
	@Override
	protected void setWeaponMotions() {
		super.setWeaponMotions();
		this.weaponLivingMotions.put(WeaponCategories.SWORD, ImmutableMap.of(
			CapabilityItem.Styles.ONE_HAND, Set.of(
				Pair.of(LivingMotions.CHASE, Animations.WITHER_SKELETON_CHASE),
				Pair.of(LivingMotions.WALK, Animations.WITHER_SKELETON_WALK),
				Pair.of(LivingMotions.IDLE, Animations.WITHER_SKELETON_IDLE)
			)
		));
		
		this.weaponAttackMotions.put(WeaponCategories.SWORD, ImmutableMap.of(CapabilityItem.Styles.COMMON, MobCombatBehaviors.SKELETON_SWORD));
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveMobUpdateMotion(considerInaction);
	}
	
	@Override
	public void onHurtSomeone(Entity target, InteractionHand handIn, ExtendedDamageSource source, float amount, boolean succeed) {
		if (succeed && target instanceof LivingEntity && this.original.getRandom().nextInt(10) == 0) {
			((LivingEntity)target).addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
		}
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		OpenMatrix4f mat = super.getModelMatrix(partialTicks);
		return OpenMatrix4f.scale(new Vec3f(1.2F, 1.2F, 1.2F), mat, mat);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.skeleton;
	}
}