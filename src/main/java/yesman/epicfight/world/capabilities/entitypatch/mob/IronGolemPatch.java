package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.AnimatedAttackGoal;
import yesman.epicfight.world.entity.ai.goal.TargetChasingGoal;

public class IronGolemPatch extends MobPatch<IronGolemEntity> {
	private int deathTimerExt;
	
	public IronGolemPatch() {
		super(Faction.VILLAGER);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, MobCombatBehaviors.IRON_GOLEM.build(this)));
		this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.original, 1.0D, false));
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		super.selectGoalToRemove(toRemove);
		
		for (PrioritizedGoal wrappedGoal : this.original.goalSelector.availableGoals) {
			Goal goal = wrappedGoal.getGoal();
			
			if (goal instanceof MoveTowardsTargetGoal) {
				toRemove.add(goal);
			}
		}
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(4.0D);
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(6.0D);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.GOLEM_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.GOLEM_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.GOLEM_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}

	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}

	@Override
	public void tick(LivingUpdateEvent event) {
		if (this.original.getHealth() <= 0.0F) {
			this.original.xRot = 0;
			if (this.original.deathTime > 1 && this.deathTimerExt < 20) {
				this.deathTimerExt++;
				this.original.deathTime--;
			}
		}
		
		super.tick(event);
	}
	
	@Override
	public SoundEvent getWeaponHitSound(Hand hand) {
		return EpicFightSounds.BLUNT_HIT_HARD;
	}
	
	@Override
	public SoundEvent getSwingSound(Hand hand) {
		return EpicFightSounds.WHOOSH_BIG;
	}
	
	@Override
	public float getDamageTo(Entity targetEntity, ExtendedDamageSource source, Hand hand) {
		float damage = super.getDamageTo(targetEntity, source, hand);
		return (int)damage > 0 ? damage / 2.0F + (float)this.original.getRandom().nextInt((int)damage) : damage;
	}

	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return null;
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ironGolem;
	}
}