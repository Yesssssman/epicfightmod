package yesman.epicfight.api.animation;

import yesman.epicfight.api.animation.property.AnimationProperty.PlaySpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AnimationPlayer {
	private float elapsedTime;
	private float prevElapsedTime;
	private boolean isEnd;
	private boolean doNotResetNext;
	private boolean reversed;
	private DynamicAnimation play;
	
	public AnimationPlayer() {
		this.setPlayAnimation(Animations.DUMMY_ANIMATION);
	}
	
	public void tick(LivingEntityPatch<?> entitypatch) {
		this.prevElapsedTime = this.elapsedTime;
		
		float playbackSpeed = this.getAnimation().getPlaySpeed(entitypatch);
		PlaySpeedModifier playSpeedModifier = this.getAnimation().getRealAnimation().getProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER).orElse(null);
		
		if (playSpeedModifier != null) {
			playbackSpeed = playSpeedModifier.modify(this.getAnimation(), entitypatch, playbackSpeed, this.elapsedTime);
		}
		
		this.elapsedTime += ConfigurationIngame.A_TICK * playbackSpeed * (this.isReversed() && this.getAnimation().canBePlayedReverse() ? -1.0F : 1.0F); 
		
		if (this.elapsedTime >= this.play.getTotalTime()) {
			if (this.play.isRepeat()) {
				this.prevElapsedTime = 0;
				this.elapsedTime %= this.play.getTotalTime();
			} else {
				this.elapsedTime = this.play.getTotalTime();
				this.isEnd = true;
			}
		} else if (this.elapsedTime < 0) {
			if (this.play.isRepeat()) {
				this.prevElapsedTime = this.play.getTotalTime();
				this.elapsedTime = this.play.getTotalTime() + this.elapsedTime;
			} else {
				this.elapsedTime = 0.0F;
				this.isEnd = true;
			}
		}
	}
	
	public void reset() {
		this.elapsedTime = 0;
		this.prevElapsedTime = 0;
		this.isEnd = false;
	}
	
	public void setPlayAnimation(DynamicAnimation animation) {
		if (this.doNotResetNext) {
			this.doNotResetNext = false;
		} else {
			this.reset();
		}
		
		this.play = animation;
	}
	
	public Pose getCurrentPose(LivingEntityPatch<?> entitypatch, float partialTicks) {
		return this.play.getPoseByTime(entitypatch, this.prevElapsedTime + (this.elapsedTime - this.prevElapsedTime) * partialTicks, partialTicks);
	}
	
	public float getElapsedTime() {
		return this.elapsedTime;
	}
	
	public float getPrevElapsedTime() {
		return this.prevElapsedTime;
	}
	
	public void setElapsedTimeCurrent(float elapsedTime) {
		this.elapsedTime = elapsedTime;
		this.isEnd = false;
	}
	
	public void setElapsedTime(float elapsedTime) {
		this.elapsedTime = elapsedTime;
		this.prevElapsedTime = elapsedTime;
		this.isEnd = false;
	}
	
	public void setElapsedTime(float prevElapsedTime, float elapsedTime) {
		this.elapsedTime = elapsedTime;
		this.prevElapsedTime = prevElapsedTime;
		this.isEnd = false;
	}
	
	public DynamicAnimation getAnimation() {
		return this.play;
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
		this.reversed = reversed;
	}
	
	public boolean isEmpty() {
		return this.play == Animations.DUMMY_ANIMATION ? true : false;
	}
}