package maninhouse.epicfight.animation.types;

public class LongHitAnimation extends ActionAnimation {
	public LongHitAnimation(int id, float convertTime, String path) {
		super(id, convertTime, false, false, path);
	}
	
	@Override
	public EntityState getState(float time) {
		return EntityState.HIT;
	}
}