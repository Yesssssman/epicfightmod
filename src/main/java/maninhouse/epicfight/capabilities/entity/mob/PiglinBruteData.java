package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.entity.ai.brain.BrainRemodeler;
import maninhouse.epicfight.entity.ai.brain.task.AttackPatternTask;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;

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
				new AttackPatternTask(this, MobAttackPatterns.BIPED_ARMED, 0.0D, 2.0D));
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(ModAttributes.STUN_ARMOR.get()).setBaseValue(8.0F);
		this.orgEntity.getAttribute(ModAttributes.IMPACT.get()).setBaseValue(3.0F);
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
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_PIGLIN;
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