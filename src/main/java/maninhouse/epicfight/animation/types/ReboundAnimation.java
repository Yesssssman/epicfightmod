package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.config.ConfigurationIngame;

public class ReboundAnimation extends AimAnimation {
	public ReboundAnimation(int id, float convertTime, boolean repeatPlay, String path1, String path2, String path3, String path4) {
		super(id, convertTime, repeatPlay, path1, path2, path3, path4);
	}
	
	public ReboundAnimation(int id, boolean repeatPlay, String path1, String path2, String path3, String path4) {
		this(id, ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, path4);
	}
	
	@Override
	public void onActivate(LivingData<?> entity) {
		
	}
	
	@Override
	public void onUpdate(LivingData<?> entity) {
		;
	}

	@Override
	public EntityState getState(float time) {
		return EntityState.POST_DELAY;
	}
	
	@Override
	public boolean isReboundAnimation() {
		return true;
	}
}