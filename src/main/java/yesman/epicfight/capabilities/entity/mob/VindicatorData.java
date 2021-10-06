package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.AttackPatternGoal;
import yesman.epicfight.entity.ai.ChasingGoal;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.AttackCombos;

public class VindicatorData<T extends AbstractIllagerEntity> extends AbstractIllagerData<T> {
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
		this.orgEntity.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(1.0F);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.state.isInaction() && considerInaction) {
			currentMotion = LivingMotion.INACTION;
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
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 3.0D, true, AttackCombos.VINDICATOR_AXE));
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
	}

	@Override
	public void setAIAsUnarmed() {

	}

	@Override
	public void setAIAsMounted(Entity ridingEntity) {

	}
}