package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EvokerEntity;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.gamedata.Animations;

public class EvokerData extends AbstractIllagerData<EvokerEntity> {
	public EvokerData() {
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAI() {

	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.SPELLCAST, Animations.EVOKER_CAST_SPELL);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.getOriginalEntity().isSpellcasting()) {
			currentMotion = LivingMotion.SPELLCAST;
		} else {
			super.commonCreatureUpdateMotion(considerInaction);
		}
	}
	
	@Override
	public void postInit() {

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