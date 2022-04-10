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
		clientAnimator.addLivingMotion(LivingMotion.SPELLCAST, Animations.EVOKER_CAST_SPELL);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.state.inaction() && considerInaction) {
			currentMotion = LivingMotion.INACTION;
		} else {
			if (this.getOriginal().isCastingSpell()) {
				currentMotion = LivingMotion.SPELLCAST;
			} else {
				super.humanoidEntityUpdateMotion(considerInaction);
			}
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