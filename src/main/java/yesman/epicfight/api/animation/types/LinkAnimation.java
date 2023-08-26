package yesman.epicfight.api.animation.types;

import java.util.Map;

import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class LinkAnimation extends DynamicAnimation {
	protected DynamicAnimation nextAnimation;
	protected float startsAt;
	
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
	public TypeFlexibleHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
		return this.nextAnimation.getStatesMap(entitypatch, time);
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

	public DynamicAnimation getNextAnimation() {
		return this.nextAnimation;
	}
	
	@Override
	public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		return this.nextAnimation.isJointEnabled(entitypatch, layer, joint);
	}
	
	@Override
	public BindModifier getBindModifier(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		return this.nextAnimation.getBindModifier(entitypatch, layer, joint);
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
	
	public void copyTo(LinkAnimation linkAnimation) {
		linkAnimation.setNextAnimation(this.nextAnimation);
		linkAnimation.totalTime = this.totalTime;
		
		Map<String, TransformSheet> trnasforms = linkAnimation.getTransfroms();
		trnasforms.clear();
		trnasforms.putAll(this.getTransfroms());
	}
	
	@Override
	public String toString() {
		return "LinkAnimation " + this.nextAnimation;
	}
}