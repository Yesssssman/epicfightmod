package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.gamedata.Animations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EvokerEntity;

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
	public void updateMotion() {
		if (this.getOriginalEntity().isSpellcasting()) {
			currentMotion = LivingMotion.SPELLCAST;
		} else {
			super.commonCreatureUpdateMotion();
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