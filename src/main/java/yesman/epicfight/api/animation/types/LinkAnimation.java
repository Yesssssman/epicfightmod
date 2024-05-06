package yesman.epicfight.api.animation.types;

import java.util.Map;

import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class LinkAnimation extends DynamicAnimation {
	private final AnimationClip animationClip = new AnimationClip();
	protected DynamicAnimation fromAnimation;
	protected DynamicAnimation toAnimation;
	protected float startsAt;
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		this.toAnimation.linkTick(entitypatch, this);
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		if (!isEnd) {
			this.toAnimation.end(entitypatch, nextAnimation, isEnd);
		} else {
			if (this.startsAt > 0.0F) {
				entitypatch.getAnimator().getPlayerFor(this).setElapsedTime(this.startsAt);
				entitypatch.getAnimator().getPlayerFor(this).markToDoNotReset();
				this.startsAt = 0.0F;
			}
		}
	}
	
	@Override
	public TypeFlexibleHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
		return this.toAnimation.getStatesMap(entitypatch, time);
	}
	
	@Override
	public EntityState getState(LivingEntityPatch<?> entitypatch, float time) {
		return this.toAnimation.getState(entitypatch, 0.0F);
	}
	
	@Override
	public <T> T getState(StateFactor<T> stateFactor, LivingEntityPatch<?> entitypatch, float time) {
		return this.toAnimation.getState(stateFactor, entitypatch, 0.0F);
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		Pose nextStartingPose = this.toAnimation.getPoseByTime(entitypatch, this.startsAt, 1.0F);
		
		/**
		 * Update dest pose
		 */
		for (Map.Entry<String, JointTransform> entry : nextStartingPose.getJointTransformData().entrySet()) {
			if (this.animationClip.hasJointTransform(entry.getKey())) {
				Keyframe[] keyframe = this.animationClip.getJointTransform(entry.getKey()).getKeyframes();
				JointTransform jt = keyframe[keyframe.length - 1].transform();
				JointTransform newJt = nextStartingPose.getJointTransformData().get(entry.getKey());
				newJt.translation().set(jt.translation());
				jt.copyFrom(newJt);
			}
		}
		
		return super.getPoseByTime(entitypatch, time, partialTicks);
	}
	
	@Override
	public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		this.toAnimation.modifyPose(this, pose, entitypatch, time, partialTicks);
	}
	
	@Override
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch) {
		return this.toAnimation.getPlaySpeed(entitypatch);
	}
	
	public void setConnectedAnimations(DynamicAnimation from, DynamicAnimation to) {
		this.fromAnimation = from.getRealAnimation();
		this.toAnimation = to;
	}
	
	public DynamicAnimation getNextAnimation() {
		return this.toAnimation;
	}
	
	@Override
	public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, String joint) {
		return this.animationClip.hasJointTransform(joint);
	}
	
	@Override
	public BindModifier getBindModifier(LivingEntityPatch<?> entitypatch, String joint) {
		return this.toAnimation.getBindModifier(entitypatch, joint);
	}
	
	@Override
	public boolean isMainFrameAnimation() {
		return this.toAnimation.isMainFrameAnimation();
	}
	
	@Override
	public boolean isReboundAnimation() {
		return this.toAnimation.isReboundAnimation();
	}
	
	@Override
	public boolean doesHeadRotFollowEntityHead() {
		return this.fromAnimation.doesHeadRotFollowEntityHead() && this.toAnimation.doesHeadRotFollowEntityHead();
	}
	
	@Override
	public DynamicAnimation getRealAnimation() {
		return this.toAnimation;
	}
	
	public DynamicAnimation getFromAnimation() {
		return this.fromAnimation;
	} 
	
	public void copyTo(LinkAnimation dest) {
		dest.setConnectedAnimations(this.fromAnimation, this.toAnimation);
		dest.setTotalTime(this.getTotalTime());
		
		Map<String, TransformSheet> trnasforms = dest.getTransfroms();
		trnasforms.clear();
		trnasforms.putAll(this.getTransfroms());
	}
	
	public void resetNextStartTime() {
		this.startsAt = 0.0F;
	}
	
	@Override
	public String toString() {
		return "From " + this.fromAnimation + " to " + this.toAnimation;
	}

	@Override
	public AnimationClip getAnimationClip() {
		return this.animationClip;
	}
}