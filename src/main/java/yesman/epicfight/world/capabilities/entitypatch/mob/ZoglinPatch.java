package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Optional;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.brain.BrainRecomposer;
import yesman.epicfight.world.entity.ai.brain.task.AnimatedCombatBehavior;
import yesman.epicfight.world.entity.ai.brain.task.MoveToTargetSinkStopInaction;

public class ZoglinPatch extends MobPatch<Zoglin> {
	@Override
	public void initAI() {
		super.initAI();
		BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.CORE, 1, MoveToTargetSink.class, new MoveToTargetSinkStopInaction());
		BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.FIGHT, 11, RunIf.class, new AnimatedCombatBehavior<>(this, MobCombatBehaviors.HOGLIN.build(this)));
		BrainRecomposer.removeBehavior(this.original.getBrain(), Activity.FIGHT, 12, RunIf.class);
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
	public LivingEntity getTarget() {
		Optional<LivingEntity> opt = this.original.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
		return opt.orElse(null);
	}
}