package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.config.ConfigurationIngame;

public class MovementAnimation extends StaticAnimation {
	public MovementAnimation(int id, float convertTime, boolean isRepeat, String path) {
		super(id, convertTime, isRepeat, path);
	}
	
	public MovementAnimation(String path) {
		super(path);
	}
	
	public MovementAnimation(float convertTime, boolean repeatPlay, String path) {
		super(convertTime, repeatPlay, path);
	}
	
	public MovementAnimation(int id, boolean repeatPlay, String path) {
		this(id, ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path);
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