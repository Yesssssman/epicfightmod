package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.entity.ai.brain.BrainRemodeler;
import yesman.epicfight.entity.ai.brain.task.AttackPatternTask;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.AttackCombos;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Model;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class PiglinBruteData extends BipedMobData<PiglinBruteEntity> {
	public PiglinBruteData() {
		super(Faction.PIGLIN_ARMY);
	}
	
	@Override
	public void onEntityConstructed(PiglinBruteEntity entityIn) {
		super.onEntityConstructed(entityIn);
	}
	
	@Override
	public void onEntityJoinWorld(PiglinBruteEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		BrainRemodeler.replaceTask(this.orgEntity.getBrain(), Activity.FIGHT, 12, AttackTargetTask.class,
				new AttackPatternTask(this, AttackCombos.BIPED_ARMED, 0.0D, 2.0D));
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(EpicFightAttributes.STUN_ARMOR.get()).setBaseValue(8.0F);
		this.orgEntity.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(3.0F);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.PIGLIN_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.PIGLIN_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.PIGLIN_DEATH);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonCreatureUpdateMotion(considerInaction);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.piglin;
	}
	
	public void setAIAsUnarmed() {
		
	}
	
	public void setAIAsArmed() {
		
	}
	
	public void setAIAsMounted(Entity ridingEntity) {
		
	}
	
	public void setAIAsRanged() {
		
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		OpenMatrix4f mat = super.getModelMatrix(partialTicks);
		return OpenMatrix4f.scale(new Vec3f(1.1F, 1.1F, 1.1F), mat, mat);
	}
}