package yesman.epicfight.animation;

import yesman.epicfight.animation.types.DynamicAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.gamedata.Animations;

public class AnimationPlayer {
	private float elapsedTime;
	private float prevElapsedTime;
	private float exceedTime;
	private boolean isEnd;
	private boolean doNotResetNext;
	private boolean reversed;
	private DynamicAnimation play;
	
	public AnimationPlayer() {
		this.setPlayAnimation(Animations.DUMMY_ANIMATION);
	}
	
	public void update(LivingData<?> entitydata) {
		this.prevElapsedTime = this.elapsedTime;
		this.elapsedTime += ConfigurationIngame.A_TICK * this.getPlay().getPlaySpeed(entitydata);
		
		if (this.elapsedTime >= this.play.getTotalTime()) {
			if (this.play.isRepeat()) {
				this.prevElapsedTime = 0;
				this.elapsedTime %= this.play.getTotalTime();
			} else {
				this.exceedTime = this.elapsedTime - this.play.getTotalTime();
				this.elapsedTime = this.play.getTotalTime();
				this.isEnd = true;
			}
		} else if (this.elapsedTime < 0) {
			if (this.play.isRepeat()) {
				this.prevElapsedTime = this.play.getTotalTime();
				this.elapsedTime = this.play.getTotalTime() + this.elapsedTime;
			} else {
				this.elapsedTime = 0;
				this.isEnd = true;
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
		if (this.doNotResetNext) {
			this.doNotResetNext = false;
		} else {
			this.resetPlayer();
		}
		
		this.play = animation;
	}
	
	public void setEmpty() {
		this.resetPlayer();
		this.play = Animations.DUMMY_ANIMATION;
	}
	
	public Pose getCurrentPose(LivingData<?> entitydata, float partialTicks) {
		return this.play.getPoseByTime(entitydata, this.prevElapsedTime + (this.elapsedTime - this.prevElapsedTime) * partialTicks);
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
		return this.play;
	}
	
	public float getExceedTime() {
		return this.exceedTime;
	}

	public void markToDoNotReset() {
		this.doNotResetNext = true;
	}

	public boolean isEnd() {
		return this.isEnd;
	}
	
	public boolean isReversed() {
		return this.reversed;
	}
	
	public void setReversed(boolean reversed) {
		if (reversed != this.reversed) {
			this.setElapsedTime(this.getPlay().getTotalTime() - this.getElapsedTime());
			this.reversed = reversed;
		}
	}
	
	public boolean isEmpty() {
		return this.play == Animations.DUMMY_ANIMATION ? true : false;
	}
}