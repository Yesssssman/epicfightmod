package yesman.epicfight.animation.types;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.property.Property;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.animation.PoseModifyingFunction;
import yesman.epicfight.client.animation.Layer.Priority;
import yesman.epicfight.gamedata.Animations;

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
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {
		if (entitydata.isRemote() && isEnd) {
			entitydata.getClientAnimator().getLayer(this.layerPriority).animationPlayer.setEmpty();
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		Pose lowerLayerPose = entitydata.getClientAnimator().getComposedLayerPoseLimit(this.layerPriority.lower(), Minecraft.getInstance().getRenderPartialTicks());
		return Pose.interpolatePose(this.lastPose, lowerLayerPose, time / this.totalTime);
	}
	
	@Override
	public boolean isEnabledJoint(LivingData<?> entitydata, String joint) {
		return this.lastPose.getJointTransformData().containsKey(joint);
	}
	
	@Override
	public <V> Optional<V> getProperty(Property<V> propertyType) {
		return this.lastAnimation.getProperty(propertyType);
	}
	
	public void setLastAnimation(DynamicAnimation animation) {
		this.lastAnimation = animation;
	}
	
	@Override
	public PoseModifyingFunction getPoseModifyingFunction(LivingData<?> entitydata, String joint) {
		return this.lastAnimation.getPoseModifyingFunction(entitydata, joint);
	}
	
	@Override
	public DynamicAnimation getRealAnimation() {
		return Animations.DUMMY_ANIMATION;
	}
}