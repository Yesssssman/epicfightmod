package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.gamedata.Animations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.PillagerEntity;

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
		animatorClient.addOverridenLivingMotion(LivingMotion.IDLE, Animations.BIPED_IDLE_CROSSBOW);
		animatorClient.addOverridenLivingMotion(LivingMotion.WALK, Animations.BIPED_IDLE_CROSSBOW);
		animatorClient.addOverridenLivingMotion(LivingMotion.RELOAD, Animations.BIPED_CROSSBOW_RELOAD);
		animatorClient.addOverridenLivingMotion(LivingMotion.AIM, Animations.BIPED_CROSSBOW_AIM);
		animatorClient.addOverridenLivingMotion(LivingMotion.SHOT, Animations.BIPED_CROSSBOW_SHOT);
	}
	
	@Override
	public void updateMotion() {
		super.commonRangedAttackCreatureUpdateMotion();
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