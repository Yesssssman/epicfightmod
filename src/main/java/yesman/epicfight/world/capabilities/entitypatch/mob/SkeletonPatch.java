package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.entity.ai.goal.AttackPatternGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;
import yesman.epicfight.world.entity.ai.goal.AttackBehaviorGoal;

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
		super.commonBipedCreatureAnimatorInit(clientAnimator);
		clientAnimator.addCompositeAnimation(LivingMotion.AIM, Animations.BIPED_BOW_AIM);
		clientAnimator.addCompositeAnimation(LivingMotion.SHOT, Animations.BIPED_BOW_SHOT);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.humanoidRangedEntityUpdateMotion(considerInaction);
	}
	
	@Override
	public void setAIAsArmed() {
		this.original.goalSelector.addGoal(0, new AttackBehaviorGoal<>(this, MobCombatBehaviors.SKELETON_ARMED_BEHAVIORS.build(this)));
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.2D, true, Animations.WITHER_SKELETON_CHASE, Animations.WITHER_SKELETON_WALK));
	}
	
	@Override
	public void setAIAsUnarmed() {
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, false, null, null, false));
		this.original.goalSelector.addGoal(0, new AttackPatternGoal(this, this.original, 0.0D, 1.8D, true, MobCombatBehaviors.BIPED_UNARMED));
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.skeleton;
	}
}