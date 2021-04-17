package maninthehouse.epicfight.capabilities.entity.mob;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.gamedata.Animations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEvoker;

public class EvokerData extends AbstractIllagerData<EntityEvoker> {
	public EvokerData() {
		super(Faction.ILLAGER);
	}

	@Override
	protected void initAI() {

	}

	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.SPELLCASTING, Animations.EVOKER_CAST_SPELL);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion() {
		if (this.getOriginalEntity().isSpellcasting()) {
			currentMotion = LivingMotion.SPELLCASTING;
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