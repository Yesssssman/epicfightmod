package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.goal.AttackPatternGoal;
import yesman.epicfight.world.entity.ai.goal.AttackPatternPercentGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;

public class SpiderPatch<T extends Mob> extends MobPatch<T> {
	public SpiderPatch() {
		super(Faction.NATURAL);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
        
		Set<WrappedGoal> goals = this.original.goalSelector.getAvailableGoals();
		Iterator<WrappedGoal> iterator = goals.iterator();
		Goal toRemove = null;
		
		while (iterator.hasNext()) {
			WrappedGoal goal = iterator.next();
			Goal inner = goal.getGoal();

			if (inner instanceof LeapAtTargetGoal) {
				toRemove = inner;
				break;
			}
		}
        
        if(toRemove != null) {
        	this.original.goalSelector.removeGoal(toRemove);
        }
        
        this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, false));
        this.original.goalSelector.addGoal(1, new AttackPatternPercentGoal(this, this.original, 0.0D, 2.0D, 0.5F, true, MobCombatBehaviors.SPIDER));
        this.original.goalSelector.addGoal(0, new AttackPatternGoal(this, this.original, 0.0D, 2.5D, true, MobCombatBehaviors.SPIDER_JUMP));
	}
	
	@Override
	public void postInit() {
		super.postInit();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotion.DEATH, Animations.SPIDER_DEATH);
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.SPIDER_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.WALK, Animations.SPIDER_CRAWL);
	}

	@Override
	public void updateMotion(boolean considerInaction) {
		super.humanoidEntityUpdateMotion(considerInaction);
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