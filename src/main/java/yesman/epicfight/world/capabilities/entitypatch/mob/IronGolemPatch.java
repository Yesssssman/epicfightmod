package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.ExtendedDamageSource;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviorGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;

public class IronGolemPatch extends HumanoidMobPatch<IronGolem> {
	private int deathTimerExt;
	
	public IronGolemPatch() {
		super(Faction.VILLAGER);
	}
	
	@Override
	public void onJoinWorld(IronGolem entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		Set<WrappedGoal> goals = this.original.goalSelector.getAvailableGoals();
		Iterator<WrappedGoal> iterator = goals.iterator();
		Goal toRemove = null;
		
		while (iterator.hasNext()) {
			WrappedGoal goal = iterator.next();
			Goal inner = goal.getGoal();

			if (inner instanceof MoveTowardsTargetGoal) {
				toRemove = inner;
				break;
			}
		}

		if (toRemove != null) {
			this.original.goalSelector.removeGoal(toRemove);
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
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.GOLEM_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.WALK, Animations.GOLEM_WALK);
		clientAnimator.addLivingAnimation(LivingMotion.DEATH, Animations.GOLEM_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}

	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}

	@Override
	public void tick(LivingUpdateEvent event) {
		if (this.original.getHealth() <= 0.0F) {
			this.original.setXRot(0);
			if (this.original.deathTime > 1 && this.deathTimerExt < 20) {
				this.deathTimerExt++;
				this.original.deathTime--;
			}
		}
		
		super.tick(event);
	}

	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		this.original.goalSelector.addGoal(0, new CombatBehaviorGoal<>(this, MobCombatBehaviors.IRON_GOLEM.build(this)));
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, false));
	}

	@Override
	public SoundEvent getWeaponHitSound(InteractionHand hand) {
		return EpicFightSounds.BLUNT_HIT_HARD;
	}
	
	@Override
	public SoundEvent getSwingSound(InteractionHand hand) {
		return EpicFightSounds.WHOOSH_BIG;
	}
	
	@Override
	public float calculateDamageTo(Entity targetEntity, ExtendedDamageSource source, InteractionHand hand) {
		float damage = super.calculateDamageTo(targetEntity, source, hand);
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