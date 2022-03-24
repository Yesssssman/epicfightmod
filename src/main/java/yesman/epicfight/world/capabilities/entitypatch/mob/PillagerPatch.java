package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;

public class PillagerPatch extends AbstractIllagerPatch<Pillager> {
	public PillagerPatch() {
		super(Faction.ILLAGER);
	}

	@Override
	protected void initAI() {
		super.initAI();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		super.initAnimator(clientAnimator);
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_WALK);
		clientAnimator.addCompositeAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE_CROSSBOW);
		clientAnimator.addCompositeAnimation(LivingMotion.WALK, Animations.BIPED_IDLE_CROSSBOW);
		clientAnimator.addCompositeAnimation(LivingMotion.RELOAD, Animations.BIPED_CROSSBOW_RELOAD);
		clientAnimator.addCompositeAnimation(LivingMotion.AIM, Animations.BIPED_CROSSBOW_AIM);
		clientAnimator.addCompositeAnimation(LivingMotion.SHOT, Animations.BIPED_CROSSBOW_SHOT);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.humanoidRangedEntityUpdateMotion(considerInaction);
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