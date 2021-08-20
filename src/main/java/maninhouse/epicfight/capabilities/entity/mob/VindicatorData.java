package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;

public class VindicatorData extends AbstractIllagerData<MobEntity> {
	public VindicatorData() {
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.ANGRY, Animations.VINDICATOR_IDLE_AGGRESSIVE);
		animatorClient.addLivingAnimation(LivingMotion.CHASE, Animations.VINDICATOR_CHASE);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(ModAttributes.IMPACT.get()).setBaseValue(1.0F);
	}
	
	@Override
	public void updateMotion() {
		if (this.state.isInaction()) {
			currentMotion = LivingMotion.IDLE;
		} else {
			boolean isAngry = orgEntity.isAggressive();
			
			if(this.orgEntity.getHealth() <= 0.0F) {
				currentMotion = LivingMotion.DEATH;
			} else if (orgEntity.limbSwingAmount > 0.01F) {
				currentMotion = isAngry ? LivingMotion.CHASE : LivingMotion.WALK;
			} else {
				currentMotion = isAngry ? LivingMotion.ANGRY : LivingMotion.IDLE;
			}
		}
	}
	
	@Override
	public void setAIAsArmed() {
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 3.0D, true, MobAttackPatterns.VINDICATOR_AXE));
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
	}

	@Override
	public void setAIAsUnarmed() {

	}

	@Override
	public void setAIAsMounted(Entity ridingEntity) {

	}
}