package yesman.epicfight.api.animation.types;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.Layer.Priority;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class LayerOffAnimation extends DynamicAnimation {
	private DynamicAnimation lastAnimation;
	private Pose lastPose;
	private Priority layerPriority;
	
	public LayerOffAnimation(Priority layerPriority) {
		this.layerPriority = layerPriority;
	}
	
	public void setLastPose(Pose pose) {
		this.lastPose = pose;
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		if (entitypatch.isLogicalClient()) {
			entitypatch.getClientAnimator().baseLayer.disableLayer(this.layerPriority);
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		Pose lowerLayerPose = entitypatch.getClientAnimator().getComposedLayerPoseBelow(this.layerPriority, Minecraft.getInstance().getFrameTime());
		return Pose.interpolatePose(this.lastPose, lowerLayerPose, time / this.totalTime);
	}
	
	@Override
	public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		return this.lastPose.getJointTransformData().containsKey(joint);
	}
	
	@Override
	public <V> Optional<V> getProperty(AnimationProperty<V> propertyType) {
		return this.lastAnimation.getProperty(propertyType);
	}
	
	public void setLastAnimation(DynamicAnimation animation) {
		this.lastAnimation = animation;
	}
	
	@Override
	public BindModifier getBindModifier(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		return this.lastAnimation.getBindModifier(entitypatch, layer, joint);
	}
	
	@Override
	public DynamicAnimation getRealAnimation() {
		return Animations.DUMMY_ANIMATION;
	}
}