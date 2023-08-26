package yesman.epicfight.api.animation.types;

import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ConcurrentLinkAnimation extends DynamicAnimation {
	protected DynamicAnimation nextAnimation;
	protected DynamicAnimation currentAnimation;
	protected float startsAt;
	
	public void acceptFrom(DynamicAnimation currentAnimation, DynamicAnimation nextAnimation, float time) {
		this.currentAnimation = currentAnimation;
		this.nextAnimation = nextAnimation;
		this.startsAt = time;
		this.setTotalTime(nextAnimation.getConvertTime());
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		this.nextAnimation.linkTick(entitypatch, this);
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		if (!isEnd) {
			this.nextAnimation.end(entitypatch, nextAnimation, isEnd);
		} else {
			if (this.startsAt > 0.0F) {
				entitypatch.getAnimator().getPlayerFor(this).setElapsedTime(this.startsAt);
				entitypatch.getAnimator().getPlayerFor(this).markToDoNotReset();
				this.startsAt = 0.0F;
			}
		}
	}
	
	@Override
	public EntityState getState(LivingEntityPatch<?> entitypatch, float time) {
		return this.nextAnimation.getState(entitypatch, 0.0F);
	}
	
	@Override
	public <T> T getState(StateFactor<T> stateFactor, LivingEntityPatch<?> entitypatch, float time) {
		return this.nextAnimation.getState(stateFactor, entitypatch, 0.0F);
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		float elapsed = time + this.startsAt;
		float currentElapsed = elapsed % this.currentAnimation.getTotalTime();
		float nextElapsed = elapsed % this.nextAnimation.getTotalTime();
		Pose currentAnimPose = this.currentAnimation.getPoseByTime(entitypatch, currentElapsed, 1.0F);
		Pose nextAnimPose = this.nextAnimation.getPoseByTime(entitypatch, nextElapsed, 1.0F);
		float interpolate = time / this.getTotalTime();
		
		return Pose.interpolatePose(currentAnimPose, nextAnimPose, interpolate);
	}
	
	@Override
	public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		this.nextAnimation.modifyPose(this, pose, entitypatch, time, partialTicks);
	}
	
	@Override
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch) {
		return this.nextAnimation.getPlaySpeed(entitypatch);
	}
	
	public void setNextAnimation(DynamicAnimation animation) {
		this.nextAnimation = animation;
	}
	
	@Override
	public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		return this.nextAnimation.isJointEnabled(entitypatch, layer, joint);
	}
	
	@Override
	public boolean isMainFrameAnimation() {
		return this.nextAnimation.isMainFrameAnimation();
	}
	
	@Override
	public boolean isReboundAnimation() {
		return this.nextAnimation.isReboundAnimation();
	}
	
	@Override
	public DynamicAnimation getRealAnimation() {
		return this.nextAnimation;
	}
	
	@Override
	public String toString() {
		return "ConcurrentLinkAnimation: Mix " + this.currentAnimation + " and " + this.nextAnimation;
	}
}