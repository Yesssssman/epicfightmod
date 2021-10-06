package yesman.epicfight.animation.types;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import yesman.epicfight.animation.AnimationPlayer;
import yesman.epicfight.animation.JointTransform;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.property.Property.StaticAnimationProperty;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.client.animation.Layer;
import yesman.epicfight.collada.AnimationDataExtractor;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.model.Model;
import yesman.epicfight.utils.math.OpenMatrix4f;

public class AimAnimation extends StaticAnimation {
	public StaticAnimation lookUp;
	public StaticAnimation lookDown;
	public StaticAnimation lying;
	
	public AimAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String path3, String path4, Model model) {
		super(convertTime, repeatPlay, path1, model);
		this.lookUp = new StaticAnimation(convertTime, repeatPlay, path2, model, true);
		this.lookDown = new StaticAnimation(convertTime, repeatPlay, path3, model, true);
		this.lying = new StaticAnimation(convertTime, repeatPlay, path4, model, true);
	}
	
	public AimAnimation(boolean repeatPlay, String path1, String path2, String path3, String path4, Model model) {
		this(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, path4, model);
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		super.onUpdate(entitydata);
		AnimatorClient animator = entitydata.getClientAnimator();
		Layer layer = animator.getLayer(this.getPriority());
		AnimationPlayer player = layer.animationPlayer;
		if (player.getElapsedTime() >= this.totalTime - 0.06F) {
			layer.pause();
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		if (!entitydata.isFirstPerson()) {
			if (entitydata.getOriginalEntity().isActualySwimming() || entitydata.getOriginalEntity().isElytraFlying() || entitydata.getOriginalEntity().isSpinAttacking()) {
				Pose pose = this.lying.getPoseByTime(entitydata, time);
				this.modifyPose(pose, entitydata, time);
				return pose;
			} else {
				if (entitydata instanceof PlayerData) {
					float pitch = entitydata.getOriginalEntity().getPitch(Minecraft.getInstance().getRenderPartialTicks());
					StaticAnimation interpolateAnimation;
					interpolateAnimation = (pitch > 0) ? this.lookDown : this.lookUp;
					Pose pose1 = super.getPoseByTime(entitydata, time);
					Pose pose2 = interpolateAnimation.getPoseByTime(entitydata, time);
					this.modifyPose(pose2, entitydata, time);
					Pose interpolatedPose = Pose.interpolatePose(pose1, pose2, (Math.abs(pitch) / 90.0F));
					return interpolatedPose;
				}
			}
		}
		return super.getPoseByTime(entitydata, time);
	}
	
	@Override
	protected void modifyPose(Pose pose, LivingData<?> entitydata, float time) {
		if (!entitydata.isFirstPerson()) {
			JointTransform chest = pose.getTransformByName("Chest");
			JointTransform head = pose.getTransformByName("Head");
			float f = 90.0F;
			float ratio = (f - Math.abs(entitydata.getOriginalEntity().rotationPitch)) / f;
			float yawOffset = entitydata.getOriginalEntity().getRidingEntity() != null ? entitydata.getOriginalEntity().rotationYaw : entitydata.getOriginalEntity().renderYawOffset;
			Quaternion q = new Quaternion(new Vector3f(0, 1, 0), (yawOffset - entitydata.getOriginalEntity().rotationYaw) * ratio, true);
			q.multiply(head.getRotation());
			head.getRotation().set(q.getX(), q.getY(), q.getZ(), q.getW());
			chest.push(JointTransform.DYNAMIC_TRANSFORM, OpenMatrix4f::mulOnOrigin, JointTransform.of(new Quaternion(new Vector3f(0, 1, 0), (entitydata.getOriginalEntity().rotationYaw - yawOffset) * ratio, true)));
		}
	}
	
	@Override
	public <V> StaticAnimation addProperty(StaticAnimationProperty<V> propertyType, V value) {
		super.addProperty(propertyType, value);
		this.lookDown.addProperty(propertyType, value);
		this.lookUp.addProperty(propertyType, value);
		this.lying.addProperty(propertyType, value);
		return this;
	}
	
	@Override
	public void loadAnimation(IResourceManager resourceManager) {
		AnimationDataExtractor.loadStaticAnimation(resourceManager, this);
		AnimationDataExtractor.loadStaticAnimation(resourceManager, this.lookUp);
		AnimationDataExtractor.loadStaticAnimation(resourceManager, this.lookDown);
		AnimationDataExtractor.loadStaticAnimation(resourceManager, this.lying);
	}
}