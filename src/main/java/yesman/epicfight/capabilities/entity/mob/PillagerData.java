package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.PillagerEntity;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.gamedata.Animations;

public class PillagerData extends AbstractIllagerData<PillagerEntity> {
	public PillagerData() {
		super(Faction.ILLAGER);
	}

	@Override
	protected void initAI() {
		super.initAI();
	}

	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_WALK);
		animatorClient.addOverwritingLivingMotion(LivingMotion.IDLE, Animations.BIPED_IDLE_CROSSBOW);
		animatorClient.addOverwritingLivingMotion(LivingMotion.WALK, Animations.BIPED_IDLE_CROSSBOW);
		animatorClient.addOverwritingLivingMotion(LivingMotion.RELOAD, Animations.BIPED_CROSSBOW_RELOAD);
		animatorClient.addOverwritingLivingMotion(LivingMotion.AIM, Animations.BIPED_CROSSBOW_AIM);
		animatorClient.addOverwritingLivingMotion(LivingMotion.SHOT, Animations.BIPED_CROSSBOW_SHOT);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonRangedAttackCreatureUpdateMotion(considerInaction);
	}

	@Override
	public void postInit() {
		super.postInit();
	}

	@Override
	public void setAIAsUnarmed() {

	}

	@Override
	public void setAIAsArmed() {

	}

	@Override
	public void setAIAsMounted(Entity ridingEntity) {

	}
}