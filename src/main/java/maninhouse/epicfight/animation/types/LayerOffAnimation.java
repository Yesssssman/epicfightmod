package maninhouse.epicfight.animation.types;

import java.util.Optional;

import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.property.Property;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.animation.BindingOperation;
import maninhouse.epicfight.client.animation.Layer.Priority;
import net.minecraft.client.Minecraft;

public class LayerOffAnimation extends DynamicAnimation {
	private DynamicAnimation lastAnimation;
	private Pose lastPose;
	private Priority layerPriority;
	
	public LayerOffAnimation(Priority layerPriority) {
		super();
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
	public Pose getPoseByTime(DynamicAnimation animation, LivingData<?> entitydata, float time) {
		Pose lowerLayerPose = entitydata.getClientAnimator().getComposedLowerLayerPose(this.layerPriority, Minecraft.getInstance().getRenderPartialTicks());
		return Pose.interpolatePose(this.lastPose, lowerLayerPose, time / this.totalTime);
	}
	
	@Override
	public boolean isEnabledJoint(String joint) {
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
	public BindingOperation getBindingOperation(String joint) {
		return this.lastAnimation.getBindingOperation(joint);
	}
	
	@Override
	public DynamicAnimation getRealAnimation() {
		return this.lastAnimation;
	}
}