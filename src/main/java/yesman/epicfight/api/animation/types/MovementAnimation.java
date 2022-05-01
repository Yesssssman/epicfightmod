package yesman.epicfight.api.animation.types;

import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MovementAnimation extends StaticAnimation {
	public MovementAnimation(boolean isRepeat, String path, Model model) {
		super(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, isRepeat, path, model);
	}
	
	public MovementAnimation(float convertTime, boolean isRepeat, String path, Model model) {
		super(convertTime, isRepeat, path, model);
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		if (entitypatch.getAnimator().getPlayerFor(this).isReversed()) {
			time = this.getTotalTime() - time;
		}
		return super.getPoseByTime(entitypatch, time, partialTicks);
	}
	
	@Override
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch) {
		float movementSpeed = 1.0F;
		
		if (Math.abs(entitypatch.getOriginal().animationSpeed - entitypatch.getOriginal().animationSpeedOld) < 0.007F) {
			movementSpeed *= (entitypatch.getOriginal().animationSpeed * 1.16F);
		}
		
		return movementSpeed;
	}
}