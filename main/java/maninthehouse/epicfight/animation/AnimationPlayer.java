package maninthehouse.epicfight.animation;

import maninthehouse.epicfight.animation.types.DynamicAnimation;
import maninthehouse.epicfight.capabilities.entity.LivingData;

public class AnimationPlayer {
	private float elapsedTime;
	private float prevElapsedTime;
	private float exceedTime;
	private boolean isEnd;
	private boolean doNotResetNext;

	private DynamicAnimation play;

	public AnimationPlayer() {
		resetPlayer();
	}

	public AnimationPlayer(DynamicAnimation animation) {
		setPlayAnimation(animation);
	}

	public void update(float updateTime) {
		prevElapsedTime = elapsedTime;
		elapsedTime += updateTime;

		if (elapsedTime >= play.getTotalTime()) {
			if (play.isRepeat()) {
				prevElapsedTime = 0;
				elapsedTime %= play.getTotalTime();
			} else {
				exceedTime = elapsedTime % play.getTotalTime();
				elapsedTime = play.getTotalTime();
				isEnd = true;
			}
		} else if (elapsedTime < 0) {
			if (play.isRepeat()) {
				prevElapsedTime = play.getTotalTime();
				elapsedTime = play.getTotalTime() + elapsedTime;
			} else {
				elapsedTime = 0;
				isEnd = true;
			}
		}
	}

	public void synchronize(AnimationPlayer animationPlayer) {
		this.play = animationPlayer.play;
		this.elapsedTime = animationPlayer.elapsedTime;
		this.prevElapsedTime = animationPlayer.prevElapsedTime;
		this.exceedTime = animationPlayer.exceedTime;
		this.isEnd = animationPlayer.isEnd;
	}

	public void resetPlayer() {
		this.elapsedTime = 0;
		this.prevElapsedTime = 0;
		this.exceedTime = 0;
		this.isEnd = false;
	}

	public void setPlayAnimation(DynamicAnimation animation) {
		if (doNotResetNext)
			doNotResetNext = false;
		else
			resetPlayer();

		this.play = animation;
	}

	public void setEmpty() {
		resetPlayer();
		play = null;
	}
	
	public Pose getCurrentPose(LivingData<?> entitydata, float partialTicks) {
		return play.getPoseByTime(entitydata, prevElapsedTime + (elapsedTime - prevElapsedTime) * partialTicks);
	}
	
	public float getElapsedTime() {
		return this.elapsedTime;
	}

	public float getPrevElapsedTime() {
		return this.prevElapsedTime;
	}

	public void setElapsedTime(float elapsedTime) {
		this.elapsedTime = elapsedTime;
		this.prevElapsedTime = elapsedTime;
		this.exceedTime = 0;
		this.isEnd = false;
	}

	public DynamicAnimation getPlay() {
		return play;
	}

	public float getExceedTime() {
		return exceedTime;
	}

	public void checkNoResetMark() {
		this.doNotResetNext = true;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public boolean isEmpty() {
		return play == null ? true : false;
	}
}