package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.RAVAGER_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.RAVAGER_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.RAVAGER_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, MobCombatBehaviors.RAVAGER.build(this)));
		this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.original, 1.0D, false));
	}
	
	@Override
	public void onHurtSomeone(Entity target, InteractionHand handIn, ExtendedDamageSource damagesource, float amount, boolean succeed) {
		super.onHurtSomeone(target, handIn, damagesource, amount, succeed);
		
		if (!succeed && this.original.getStunnedTick() > 0) {
			this.playAnimationSynchronized(Animations.RAVAGER_STUN, 0.0F);
		}
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