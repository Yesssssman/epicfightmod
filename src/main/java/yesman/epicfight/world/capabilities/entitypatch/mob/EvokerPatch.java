package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;

public class EvokerPatch extends AbstractIllagerPatch<Evoker> {
	public EvokerPatch() {
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAI() {
		
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		super.initAnimator(clientAnimator);
		clientAnimator.addLivingAnimation(LivingMotion.SPELLCAST, Animations.EVOKER_CAST_SPELL);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.state.inaction() && considerInaction) {
			currentLivingMotion = LivingMotion.INACTION;
		} else {
			if (this.getOriginal().isCastingSpell()) {
				currentLivingMotion = LivingMotion.SPELLCAST;
			} else {
				super.commonMobUpdateMotion(considerInaction);
			}
		}
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {

	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {

	}
}