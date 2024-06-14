package yesman.epicfight.api.animation.types;

import java.util.Map;
import java.util.Optional;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class LinkAnimation extends DynamicAnimation {
	private final AnimationClip animationClip = new AnimationClip();
	protected DynamicAnimation fromAnimation;
	protected StaticAnimation toAnimation;
	protected float nextStartTime;
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		this.toAnimation.linkTick(entitypatch, this);
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		if (!isEnd) {
			this.toAnimation.end(entitypatch, nextAnimation, isEnd);
		} else {
			if (this.nextStartTime > 0.0F) {
				entitypatch.getAnimator().getPlayerFor(this).setElapsedTime(this.nextStartTime);
				entitypatch.getAnimator().getPlayerFor(this).markToDoNotReset();
			}
		}
	}
	
	@Override
	public TypeFlexibleHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
		return this.toAnimation.getStatesMap(entitypatch, this, 0.0F);
	}
	
	@Override
	public EntityState getState(LivingEntityPatch<?> entitypatch, float time) {
		return this.toAnimation.getState(entitypatch, this, 0.0F);
	}
	
	@Override
	public <T> T getState(StateFactor<T> stateFactor, LivingEntityPatch<?> entitypatch, float time) {
		return this.toAnimation.getState(stateFactor, entitypatch, this, 0.0F);
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		Pose nextStartingPose = this.toAnimation.getPoseByTime(entitypatch, this.nextStartTime, 1.0F);
		
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
		// Bad implementation: Add root joint as coord in loading animation
		if (this.toAnimation instanceof ActionAnimation) {
			JointTransform jt = pose.getOrDefaultTransform("Root");
			Vec3f jointPosition = jt.translation();
			OpenMatrix4f toRootTransformApplied = entitypatch.getArmature().searchJointByName("Root").getLocalTrasnform().removeTranslation();
			OpenMatrix4f toOrigin = OpenMatrix4f.invert(toRootTransformApplied, null);
			Vec3f worldPosition = OpenMatrix4f.transform3v(toRootTransformApplied, jointPosition, null);
			worldPosition.x = 0.0F;
			worldPosition.y = (this.toAnimation.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false) && worldPosition.y > 0.0F) ? 0.0F : worldPosition.y;
			worldPosition.z = 0.0F;
			OpenMatrix4f.transform3v(toOrigin, worldPosition, worldPosition);
			jointPosition.x = worldPosition.x;
			jointPosition.y = worldPosition.y;
			jointPosition.z = worldPosition.z;
		}
	}
	
	@Override
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		return this.toAnimation.getPlaySpeed(entitypatch, animation);
	}
	
	public void setConnectedAnimations(DynamicAnimation from, StaticAnimation to) {
		this.fromAnimation = from.getRealAnimation();
		this.toAnimation = to;
	}
	
	public DynamicAnimation getNextAnimation() {
		return this.toAnimation;
	}
	
	@OnlyIn(Dist.CLIENT)
	public Optional<JointMaskEntry> getJointMaskEntry(LivingEntityPatch<?> entitypatch, boolean useCurrentMotion) {
		return useCurrentMotion ? this.toAnimation.getJointMaskEntry(entitypatch, true) : this.fromAnimation.getJointMaskEntry(entitypatch, false);
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
	
	public void setNextStartTime(float nextStartTime) {
		this.nextStartTime = nextStartTime;
	}
	
	public void resetNextStartTime() {
		this.nextStartTime = 0.0F;
	}
	
	@Override
	public boolean isLinkAnimation() {
		return true;
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