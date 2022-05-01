package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviorGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class SkeletonPatch<T extends AbstractSkeleton> extends HumanoidMobPatch<T> {
	public SkeletonPatch() {
		super(Faction.UNDEAD);
	}
	
	public SkeletonPatch(Faction faction) {
		super(faction);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		super.commonAggresiveMobAnimatorInit(clientAnimator);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveRangedMobUpdateMotion(considerInaction);
	}
	
	@Override
	protected void setWeaponMotions() {
		super.setWeaponMotions();
		this.weaponLivingMotions.put(WeaponCategory.SWORD, ImmutableMap.of(
			CapabilityItem.Style.COMMON, Set.of(
				Pair.of(LivingMotion.CHASE, Animations.WITHER_SKELETON_CHASE),
				Pair.of(LivingMotion.IDLE, Animations.WITHER_SKELETON_IDLE)
			)
		));
		
		this.weaponAttackMotions.put(WeaponCategory.SWORD, ImmutableMap.of(CapabilityItem.Style.COMMON, MobCombatBehaviors.SKELETON_SWORD));
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		if (!holdingRanedWeapon) {
			CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
			
			if (builder != null) {
				this.original.goalSelector.addGoal(0, new CombatBehaviorGoal<>(this, builder.build(this)));
			}
			
			this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.2D, true));
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.skeleton;
	}
}