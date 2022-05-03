package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.Faction;

public class PillagerPatch extends AbstractIllagerPatch<Pillager> {
	public PillagerPatch() {
		super(Faction.ILLAGER);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_WALK);
		clientAnimator.addLivingAnimation(LivingMotion.CHASE, Animations.BIPED_WALK);
		clientAnimator.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		clientAnimator.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		clientAnimator.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveRangedMobUpdateMotion(considerInaction);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		if (!holdingRanedWeapon) {
			super.setAIAsInfantry(holdingRanedWeapon);
		}
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {

	}
}