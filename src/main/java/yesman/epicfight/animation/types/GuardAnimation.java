package yesman.epicfight.animation.types;

import yesman.epicfight.model.Model;

public class GuardAnimation extends MainFrameAnimation {
	public GuardAnimation(float convertTime, String path, Model model) {
		super(convertTime, path, model);
	}
	
	@Override
	public EntityState getState(float time) {
		return EntityState.POST_DELAY;
	}
}