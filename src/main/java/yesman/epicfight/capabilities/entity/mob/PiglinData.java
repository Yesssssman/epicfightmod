package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.tags.ItemTags;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.entity.ai.brain.BrainRemodeler;
import yesman.epicfight.entity.ai.brain.task.AttackPatternTask;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.AttackCombos;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.Model;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSReqSpawnInfo;

public class PiglinData extends BipedMobData<PiglinEntity> {
	public PiglinData() {
		super(Faction.PIGLIN_ARMY);
	}
	
	@Override
	public void onEntityJoinWorld(PiglinEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		BrainRemodeler.replaceTask(this.orgEntity.getBrain(), Activity.FIGHT, 13, AttackTargetTask.class,
				new AttackPatternTask(this, AttackCombos.BIPED_ARMED, 0.0D, 2.0D));
		BrainRemodeler.removeTask(this.orgEntity.getBrain(), Activity.CELEBRATE, 15, FirstShuffledTask.class);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(1.0F);
	}

	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.PIGLIN_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.CELEBRATE, EpicFightMod.getInstance().animationManager.findAnimation(
				EpicFightMod.MODID.hashCode(), Animations.PIGLIN_CELEBRATE1.getId() + this.orgEntity.getRNG().nextInt(3)));
		animatorClient.addLivingAnimation(LivingMotion.ADMIRE, Animations.PIGLIN_ADMIRE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.PIGLIN_WALK);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.PIGLIN_DEATH);
		animatorClient.addOverwritingLivingMotion(LivingMotion.RELOAD, Animations.BIPED_CROSSBOW_RELOAD);
		animatorClient.addOverwritingLivingMotion(LivingMotion.AIM, Animations.BIPED_CROSSBOW_AIM);
		animatorClient.addOverwritingLivingMotion(LivingMotion.SHOT, Animations.BIPED_CROSSBOW_SHOT);
	}
	
	@Override
	public void postInit() {
		super.postInit();

		if (this.isRemote()) {
			AnimatorClient animator = this.getClientAnimator();
			if(orgEntity.isChild()) {
				animator.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_RUN);
			}
			ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getEntityId()));
		}
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.getOriginalEntity().getHeldItemOffhand().getItem().isIn(ItemTags.PIGLIN_LOVED))
			this.currentMotion = LivingMotion.ADMIRE;
		else if (this.orgEntity.func_234425_eN_())
			this.currentMotion = LivingMotion.CELEBRATE;
		else
			super.commonRangedAttackCreatureUpdateMotion(considerInaction);
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
}