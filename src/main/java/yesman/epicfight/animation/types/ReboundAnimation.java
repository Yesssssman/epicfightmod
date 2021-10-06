package yesman.epicfight.animation.types;

import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.model.Model;

public class ReboundAnimation extends AimAnimation {
	public ReboundAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String path3, String path4, Model model) {
		super(convertTime, repeatPlay, path1, path2, path3, path4, model);
	}
	
	public ReboundAnimation(boolean repeatPlay, String path1, String path2, String path3, String path4, Model model) {
		this(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, path4, model);
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