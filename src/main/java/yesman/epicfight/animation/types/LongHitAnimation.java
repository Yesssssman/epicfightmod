package yesman.epicfight.animation.types;

import yesman.epicfight.model.Model;

public class LongHitAnimation extends ActionAnimation {
	public LongHitAnimation(float convertTime, String path, Model model) {
		super(convertTime, false, false, path, model);
	}
	
	@Override
	public EntityState getState(float time) {
		return EntityState.HIT;
	}
}