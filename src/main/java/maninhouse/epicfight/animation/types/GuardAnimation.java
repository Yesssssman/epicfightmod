package maninhouse.epicfight.animation.types;

public class GuardAnimation extends MainFrameAnimation {
	public GuardAnimation(int id, float convertTime, String path) {
		super(id, convertTime, path);
	}
	
	@Override
	public EntityState getState(float time) {
		return EntityState.POST_DELAY;
	}
}