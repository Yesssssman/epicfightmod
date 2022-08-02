package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.goal.AnimatedAttackGoal;
import yesman.epicfight.world.entity.ai.goal.TargetChasingGoal;

public class SpiderPatch<T extends PathfinderMob> extends MobPatch<T> {
	public SpiderPatch() {
		super(Faction.NEUTRAL);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
        this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, MobCombatBehaviors.SPIDER.build(this)));
        this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.original, 1.0D, false));
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		super.selectGoalToRemove(toRemove);
		
		for (WrappedGoal wrappedGoal : this.original.goalSelector.getAvailableGoals()) {
			Goal goal = wrappedGoal.getGoal();
			
			if (goal instanceof LeapAtTargetGoal) {
				toRemove.add(goal);
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.SPIDER_DEATH);
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.SPIDER_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.SPIDER_CRAWL);
		clientAnimator.setCurrentMotionsAsDefault();
	}

	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}

	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return Animations.SPIDER_HIT;
	}

	@Override
	public SoundEvent getSwingSound(InteractionHand hand) {
		return SoundEvents.SPIDER_HURT;
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.spider;
	}
}