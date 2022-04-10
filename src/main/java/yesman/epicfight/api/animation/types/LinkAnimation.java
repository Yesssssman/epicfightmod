package yesman.epicfight.api.animation.types;

import java.util.Map;

import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class LinkAnimation extends DynamicAnimation {
	protected DynamicAnimation nextAnimation;
	protected float startsAt;
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		this.nextAnimation.linkTick(entitypatch, this);
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, boolean isEnd) {
		if (!isEnd) {
			this.nextAnimation.end(entitypatch, isEnd);
		} else {
			if (this.startsAt > 0.0F) {
				entitypatch.getAnimator().getPlayerFor(this).setElapsedTime(this.startsAt);
				entitypatch.getAnimator().getPlayerFor(this).markToDoNotReset();
				this.startsAt = 0.0F;
			}
		}
	}
	
	@Override
	public EntityState getState(float time) {
		return this.nextAnimation.getState(0.0F);
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		Pose nextStartingPose = this.nextAnimation.getPoseByTime(entitypatch, this.startsAt, 1.0F);
		
		for (Map.Entry<String, JointTransform> entry : nextStartingPose.getJointTransformData().entrySet()) {
			if (this.jointTransforms.containsKey(entry.getKey())) {
				Keyframe[] keyframe = this.jointTransforms.get(entry.getKey()).getKeyframes();
				JointTransform jt = keyframe[keyframe.length - 1].transform();
				JointTransform newJt = nextStartingPose.getJointTransformData().get(entry.getKey());
				newJt.translation().set(jt.translation());
				jt.copyFrom(newJt);
			}
		}
		
		return super.getPoseByTime(entitypatch, time, partialTicks);
	}
	
	@Override
	protected void modifyPose(Pose pose, LivingEntityPatch<?> entitypatch, float time) {
		this.nextAnimation.modifyPose(pose, entitypatch, time);
	}
	
	@Override
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch) {
		return this.nextAnimation.getPlaySpeed(entitypatch);
	}
	
	public void setNextAnimation(DynamicAnimation animation) {
		this.nextAnimation = animation;
	}

	public DynamicAnimation getNextAnimation() {
		return this.nextAnimation;
	}
	
	@Override
	public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, String joint) {
		return this.nextAnimation.isJointEnabled(entitypatch, joint);
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