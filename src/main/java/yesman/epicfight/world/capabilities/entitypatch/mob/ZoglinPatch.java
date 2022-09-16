package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Optional;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.SupplementedTask;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.brain.BrainRecomposer;
import yesman.epicfight.world.entity.ai.brain.task.AnimatedCombatBehavior;
import yesman.epicfight.world.entity.ai.brain.task.MoveToTargetSinkStopInaction;

public class ZoglinPatch extends MobPatch<ZoglinEntity> {
	@Override
	public void initAI() {
		super.initAI();
		BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.CORE, 1, WalkToTargetTask.class, new MoveToTargetSinkStopInaction());
		BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.FIGHT, 11, SupplementedTask.class, new AnimatedCombatBehavior<>(this, MobCombatBehaviors.HOGLIN.build(this)));
		BrainRecomposer.removeBehavior(this.original.getBrain(), Activity.FIGHT, 12, SupplementedTask.class);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.HOGLIN_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.HOGLIN_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.HOGLIN_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(4.0F);
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(5.0F);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.hoglin;
	}

	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return null;
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
	public LivingEntity getTarget() {
		Optional<LivingEntity> opt = this.original.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
		return opt.orElse(null);
	}
}