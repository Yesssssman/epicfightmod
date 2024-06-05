package yesman.epicfight.api.animation.types;

import java.util.Optional;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class ConcurrentLinkAnimation extends DynamicAnimation {
	private final AnimationClip animationClip = new AnimationClip();
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
		
		Pose interpolatedPose = Pose.interpolatePose(currentAnimPose, nextAnimPose, interpolate);
		JointMaskEntry maskEntry = this.nextAnimation.getJointMaskEntry(entitypatch, true).orElse(null);
		
		if (maskEntry != null && entitypatch.isLogicalClient()) {
			interpolatedPose.getJointTransformData().entrySet().removeIf((entry) -> maskEntry.isMasked(this.nextAnimation.getProperty(ClientAnimationProperties.LAYER_TYPE).orElse(Layer.LayerType.BASE_LAYER) == Layer.LayerType.BASE_LAYER ?
					entitypatch.getClientAnimator().currentMotion() : entitypatch.getClientAnimator().currentCompositeMotion(), entry.getKey()));
		}
		
		return interpolatedPose;
	}
	
	@Override
	public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		this.nextAnimation.modifyPose(this, pose, entitypatch, time, partialTicks);
	}
	
	@Override
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		return this.nextAnimation.getPlaySpeed(entitypatch, animation);
	}
	
	public void setNextAnimation(DynamicAnimation animation) {
		this.nextAnimation = animation;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public Optional<JointMaskEntry> getJointMaskEntry(LivingEntityPatch<?> entitypatch, boolean useCurrentMotion) {
		return this.nextAnimation.getJointMaskEntry(entitypatch, useCurrentMotion);
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
	
	@Override
	public AnimationClip getAnimationClip() {
		return this.animationClip;
	}
	
	@Override
	public boolean hasTransformFor(String joint) {
		return this.nextAnimation.hasTransformFor(joint);
	}
	
	@Override
	public boolean isLinkAnimation() {
		return true;
	}
}