package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

public abstract class AbstractIllagerPatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {
	public AbstractIllagerPatch(Faction faction) {
		super(faction);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.ILLAGER_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.ILLAGER_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}
}