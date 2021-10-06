package yesman.epicfight.capabilities.entity.mob;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.MobData;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.AttackPatternGoal;
import yesman.epicfight.entity.ai.AttackPatternPercentGoal;
import yesman.epicfight.entity.ai.ChasingGoal;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.AttackCombos;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Model;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;

public class SpiderData<T extends MobEntity> extends MobData<T> {
	public SpiderData() {
		super(Faction.NATURAL);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
        
		Set<PrioritizedGoal> goals = this.orgEntity.goalSelector.goals;
		Iterator<PrioritizedGoal> iterator = goals.iterator();
		Goal toRemove = null;
		
		while (iterator.hasNext()) {
			PrioritizedGoal goal = iterator.next();
			Goal inner = goal.getGoal();

			if (inner instanceof LeapAtTargetGoal) {
				toRemove = inner;
				break;
			}
		}
        
        if(toRemove != null) {
        	this.orgEntity.goalSelector.removeGoal(toRemove);
        }
        
        this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
        this.orgEntity.goalSelector.addGoal(1, new AttackPatternPercentGoal(this, this.orgEntity, 0.0D, 2.0D, 0.5F, true, AttackCombos.SPIDER));
        this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 2.5D, true, AttackCombos.SPIDER_JUMP));
	}
	
	@Override
	public void postInit() {
		super.postInit();
	}

	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.SPIDER_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.SPIDER_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.SPIDER_CRAWL);
	}

	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonCreatureUpdateMotion(considerInaction);
	}

	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return Animations.SPIDER_HIT;
	}

	@Override
	public SoundEvent getSwingSound(Hand hand) {
		return SoundEvents.ENTITY_SPIDER_HURT;
	}

	@Override
	public SoundEvent getWeaponHitSound(Hand hand) {
		return super.getWeaponHitSound(hand);
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.spider;
	}
}