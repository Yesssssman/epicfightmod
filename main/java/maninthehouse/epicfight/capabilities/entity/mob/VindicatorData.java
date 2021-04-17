package maninthehouse.epicfight.capabilities.entity.mob;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.EntityAIAttackPattern;
import maninthehouse.epicfight.entity.ai.EntityAIChase;
import maninthehouse.epicfight.gamedata.Animations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityVindicator;

public class VindicatorData extends AbstractIllagerData<EntityVindicator> {
	public VindicatorData() {
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.ANGRY, Animations.VINDICATOR_IDLE_AGGRESSIVE);
		animatorClient.addLivingAnimation(LivingMotion.CHASING, Animations.VINDICATOR_CHASE);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion() {
		boolean isAngry = orgEntity.isAggressive();

		if (orgEntity.limbSwingAmount > 0.01F) {
			currentMotion = isAngry ? LivingMotion.CHASING : LivingMotion.WALKING;
		} else {
			currentMotion = isAngry ? LivingMotion.ANGRY : LivingMotion.IDLE;
		}
	}
	
	@Override
	public void setAIAsArmed() {
		orgEntity.tasks.addTask(0, new EntityAIAttackPattern(this, this.orgEntity, 0.0D, 3.0D, true, MobAttackPatterns.VINDICATOR_PATTERN));
		orgEntity.tasks.addTask(1, new EntityAIChase(this, this.orgEntity, 1.0D, false));
	}

	@Override
	public void setAIAsUnarmed() {

	}

	@Override
	public void setAIAsMounted(Entity ridingEntity) {

	}

	@Override
	public void setAIAsRange() {

	}
}