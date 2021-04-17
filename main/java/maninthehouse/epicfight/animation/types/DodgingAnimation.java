package maninthehouse.epicfight.animation.types;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.utils.game.Formulars;

public class DodgingAnimation extends ActionAnimation {
	private final float width;
	private final float height;

	public DodgingAnimation(int id, float convertTime, boolean affectVelocity, String path, float width, float height) {
		this(id, convertTime, -1.0F, affectVelocity, path, width, height);
	}

	public DodgingAnimation(int id, float convertTime, float postDelay, boolean affectVelocity, String path, float width, float height) {
		super(id, convertTime, postDelay, affectVelocity, false, path);

		this.width = width;
		this.height = height;
	}

	@Override
	public void onUpdate(LivingData<?> entitydata) {
		super.onUpdate(entitydata);
		if (this.width > 0.0F || this.height > 0.0F) {
			entitydata.notifyToReset(width, height);
		}
	}

	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {
		super.onFinish(entitydata, isEnd);
		entitydata.notifyToReset(entitydata.getOriginalEntity().width, entitydata.getOriginalEntity().height);
	}

	@Override
	public float getPlaySpeed(LivingData<?> entitydata) {
		return Formulars.getRollAnimationSpeedPenalty((float)entitydata.getWeight(), entitydata);
	}
	
	@Override
	public LivingData.EntityState getState(float time) {
		if(time < this.delayTime) {
			return LivingData.EntityState.PRE_DELAY;
		} else {
			return LivingData.EntityState.DODGE;
		}
	}
}