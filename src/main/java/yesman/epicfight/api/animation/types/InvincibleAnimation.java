package yesman.epicfight.api.animation.types;

import yesman.epicfight.api.model.Model;

public class InvincibleAnimation extends ActionAnimation {
	public InvincibleAnimation(float convertTime, String path, Model model) {
		super(convertTime, path, model);
	}
	
	@Override
	public EntityState getState(float time) {
		return EntityState.INVINCIBLE;
	}
}