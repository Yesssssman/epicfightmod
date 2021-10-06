package yesman.epicfight.capabilities.entity.mob;

import java.util.Optional;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.SupplementedTask;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.MobData;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.entity.ai.brain.BrainRemodeler;
import yesman.epicfight.entity.ai.brain.task.AttackPatternTask;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.AttackCombos;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.gamedata.Sounds;
import yesman.epicfight.model.Model;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;

public class HoglinData extends MobData<HoglinEntity> {
	
	@Override
	public void onEntityJoinWorld(HoglinEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		BrainRemodeler.replaceTask(this.orgEntity.getBrain(), Activity.FIGHT, 13, SupplementedTask.class, new AttackPatternTask(this, AttackCombos.HOGLIN_HEADBUTT, 0.0D, 1.5D));
		BrainRemodeler.removeTask(this.orgEntity.getBrain(), Activity.FIGHT, 14, SupplementedTask.class);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.HOGLIN_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.HOGLIN_WALK);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.HOGLIN_DEATH);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(4.0F);
		this.orgEntity.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(5.0F);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonCreatureUpdateMotion(considerInaction);
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
		return this.orgEntity.isChild() ? Sounds.BLUNT_HIT : Sounds.BLUNT_HIT_HARD;
	}
	
	@Override
	public SoundEvent getSwingSound(Hand hand) {
		return this.orgEntity.isChild() ? Sounds.WHOOSH : Sounds.WHOOSH_BIG;
	}
	
	@Override
	public LivingEntity getAttackTarget() {
		Optional<LivingEntity> opt = this.orgEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
		return opt.orElse(null);
	}
}