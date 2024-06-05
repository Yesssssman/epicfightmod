package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.schedule.Activity;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.brain.BrainRecomposer;
import yesman.epicfight.world.entity.ai.brain.task.AnimatedCombatBehavior;
import yesman.epicfight.world.entity.ai.brain.task.MoveToTargetSinkStopInaction;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class PiglinBrutePatch extends HumanoidMobPatch<PiglinBrute> {
	public PiglinBrutePatch() {
		super(Faction.PIGLINS);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.STUN_ARMOR.get()).setBaseValue(8.0F);
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(3.0F);
	}
	
	@Override
	public void initAnimator(Animator animator) {
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.PIGLIN_IDLE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.PIGLIN_WALK);
		animator.addLivingAnimation(LivingMotions.CHASE, Animations.PIGLIN_WALK);
		animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
		animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
		animator.addLivingAnimation(LivingMotions.DEATH, Animations.PIGLIN_DEATH);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		BrainRecomposer.removeBehavior(this.original.getBrain(), Activity.FIGHT, 12, MeleeAttack.class);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
		
		if (builder != null) {
			BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.FIGHT, 12, AnimatedCombatBehavior.class, new AnimatedCombatBehavior<>(this, builder.build(this)));
		}
		
		BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.CORE, 1, MoveToTargetSink.class, new MoveToTargetSinkStopInaction());
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {
		
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		return super.getModelMatrix(partialTicks).scale(1.1F, 1.1F, 1.1F);
	}
}