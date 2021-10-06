package yesman.epicfight.animation.types;

import java.util.Map;

import yesman.epicfight.animation.JointTransform;
import yesman.epicfight.animation.Keyframe;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.capabilities.entity.LivingData;

public class LinkAnimation extends DynamicAnimation {
	protected DynamicAnimation nextAnimation;
	protected float startsAt;
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		this.nextAnimation.updateOnLinkAnimation(entitydata, this);
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {
		if (!isEnd) {
			this.nextAnimation.onFinish(entitydata, isEnd);
		} else {
			if (this.startsAt > 0.0F) {
				entitydata.getAnimator().getPlayerFor(this).setElapsedTime(this.startsAt);
				entitydata.getAnimator().getPlayerFor(this).markToDoNotReset();
				this.startsAt = 0.0F;
			}
		}
	}
	
	@Override
	public EntityState getState(float time) {
		return this.nextAnimation.getState(0.0F);
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		Pose nextStartingPose = entitydata.getAnimator().getNextStartingPose(this.startsAt);
		for (Map.Entry<String, JointTransform> entry : nextStartingPose.getJointTransformData().entrySet()) {
			if (this.jointTransforms.containsKey(entry.getKey())) {
				Keyframe[] keyframe = this.jointTransforms.get(entry.getKey()).getKeyframes();
				JointTransform jt = keyframe[keyframe.length - 1].getTransform();
				JointTransform newJt = nextStartingPose.getJointTransformData().get(entry.getKey());
				jt.set(newJt);
			}
		}
		return super.getPoseByTime(entitydata, time);
	}
	
	@Override
	protected void modifyPose(Pose pose, LivingData<?> entitydata, float time) {
		this.nextAnimation.modifyPose(pose, entitydata, time);
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata) {
		return this.nextAnimation.getPlaySpeed(entitydata);
	}
	
	public void setNextAnimation(DynamicAnimation animation) {
		this.nextAnimation = animation;
	}

	public DynamicAnimation getNextAnimation() {
		return this.nextAnimation;
	}
	
	@Override
	public boolean isEnabledJoint(LivingData<?> entitydata, String joint) {
		return this.nextAnimation.isEnabledJoint(entitydata, joint);
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
		return "LinkAnimation " + this.nextAnimation;
	}
}