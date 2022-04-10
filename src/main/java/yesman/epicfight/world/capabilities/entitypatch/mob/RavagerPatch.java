package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.AttackResult;
import yesman.epicfight.api.utils.game.ExtendedDamageSource;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.AttackPatternGoal;
import yesman.epicfight.world.entity.ai.goal.AttackPatternPercentGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;

public class RavagerPatch extends MobPatch<Ravager> {
	public RavagerPatch() {
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(8.0D);
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(6.0D);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingMotion(LivingMotion.IDLE, Animations.RAVAGER_IDLE);
		clientAnimator.addLivingMotion(LivingMotion.WALK, Animations.RAVAGER_WALK);
		clientAnimator.addLivingMotion(LivingMotion.DEATH, Animations.RAVAGER_DEATH);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.humanoidEntityUpdateMotion(considerInaction);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, false));
		this.original.goalSelector.addGoal(0, new AttackPatternPercentGoal(this, this.original, 0.0D, 2.25D, 0.1F, true, MobCombatBehaviors.RAVAGER_SMASHING_GROUND));
		this.original.goalSelector.addGoal(1, new AttackPatternGoal(this, this.original, 1.0D, 2.4D, true, MobCombatBehaviors.RAVAGER_HEADBUTT));
	}
	
	@Override
	public AttackResult harmEntity(Entity target, ExtendedDamageSource damagesource, float amount) {
		AttackResult result = super.harmEntity(target, damagesource, amount);
		
		if (result.resultType == AttackResult.ResultType.BLOCKED) {
			if (this.original.getStunnedTick() > 0) {
				this.playAnimationSynchronized(Animations.RAVAGER_STUN, 0.0F);
			}
		}
		
		return result;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return null;
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
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ravager;
	}
}