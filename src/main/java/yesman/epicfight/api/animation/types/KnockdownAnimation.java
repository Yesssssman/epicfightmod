package yesman.epicfight.api.animation.types;

import yesman.epicfight.api.model.Model;

public class KnockdownAnimation extends LongHitAnimation {
	public KnockdownAnimation(float convertTime, float delayTime, String path, Model model) {
		super(convertTime, path, model);
		this.delayTime = delayTime;
	}
	
	@Override
	public EntityState getState(float time) {
		if (time > this.delayTime) {
			return EntityState.PRE_DELAY;
		} else {
			return EntityState.KNOCKDOWN;
		}
	}
}