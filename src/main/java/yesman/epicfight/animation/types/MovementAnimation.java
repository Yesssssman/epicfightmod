package yesman.epicfight.animation.types;

import yesman.epicfight.animation.Pose;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.model.Model;

public class MovementAnimation extends StaticAnimation {
	public MovementAnimation(boolean isRepeat, String path, Model model) {
		super(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, isRepeat, path, model);
	}
	
	public MovementAnimation(float convertTime, boolean isRepeat, String path, Model model) {
		super(convertTime, isRepeat, path, model);
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		if (entitydata.getAnimator().getPlayerFor(this).isReversed()) {
			time = this.getTotalTime() - time;
		}
		return super.getPoseByTime(entitydata, time);
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata) {
		float movementSpeed = 1.0F;
		if (Math.abs(entitydata.getOriginalEntity().limbSwingAmount - entitydata.getOriginalEntity().prevLimbSwingAmount) < 0.007F) {
			movementSpeed *= (entitydata.getOriginalEntity().limbSwingAmount * 1.16F);
		}
		return movementSpeed;
	}
}