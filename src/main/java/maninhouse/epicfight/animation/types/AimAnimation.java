package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.animation.JointTransform;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.Quaternion;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.client.animation.Layer;
import maninhouse.epicfight.collada.AnimationDataExtractor;
import maninhouse.epicfight.config.ConfigurationIngame;
import maninhouse.epicfight.model.Armature;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;

public class AimAnimation extends StaticAnimation {
	public StaticAnimation lookUp;
	public StaticAnimation lookDown;
	public StaticAnimation lying;
	
	public AimAnimation(int id, float convertTime, boolean repeatPlay, String path1, String path2, String path3, String path4) {
		super(id, convertTime, repeatPlay, path1);
		this.lookUp = new StaticAnimation(path2);
		this.lookDown = new StaticAnimation(path3);
		this.lying = new StaticAnimation(path4);
	}
	
	public AimAnimation(int id, boolean repeatPlay, String path1, String path2, String path3, String path4) {
		this(id, ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, path4);
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
	public Pose getPoseByTime(DynamicAnimation animation, LivingData<?> entitydata, float time) {
		if (entitydata.isFirstPerson() || (animation instanceof LinkAnimation)) {
			return super.getPoseByTime(animation, entitydata, time);
		} else if (entitydata.getOriginalEntity().isActualySwimming() || entitydata.getOriginalEntity().isElytraFlying() || entitydata.getOriginalEntity().isSpinAttacking()) {
			Pose pose = super.getPoseByTime(this.lying, entitydata, time);
			JointTransform chest = pose.getTransformByName("Chest");
			JointTransform head = pose.getTransformByName("Head");
			float f = 90.0F;
			float ratio = (f - Math.abs(entitydata.getOriginalEntity().rotationPitch)) / f;
			float yawOffset = entitydata.getOriginalEntity().getRidingEntity() != null ? entitydata.getOriginalEntity().rotationYaw : entitydata.getOriginalEntity().renderYawOffset;
			head.setRotation(Quaternion.rotate((float)-Math.toRadians((yawOffset - entitydata.getOriginalEntity().rotationYaw) * ratio), new Vec3f(0,1,0), head.getRotation()));
			chest.setCustomRotation(Quaternion.rotate((float)-Math.toRadians((entitydata.getOriginalEntity().rotationYaw - yawOffset) * ratio), new Vec3f(0,1,0), null));
			return pose;
		} else {
			float pitch = entitydata.getOriginalEntity().getPitch(Minecraft.getInstance().getRenderPartialTicks());
			StaticAnimation interpolateAnimation;
			interpolateAnimation = (pitch > 0) ? this.lookDown : this.lookUp;
			Pose pose1 = super.getPoseByTime(this, entitydata, time);
			Pose pose2 = interpolateAnimation.getPoseByTime(interpolateAnimation, entitydata, time);
			Pose interpolatedPose = Pose.interpolatePose(pose1, pose2, (Math.abs(pitch) / 90.0F));
			JointTransform chest = interpolatedPose.getTransformByName("Chest");
			JointTransform head = interpolatedPose.getTransformByName("Head");
			float f = 90.0F;
			float ratio = (f - Math.abs(entitydata.getOriginalEntity().rotationPitch)) / f;
			float yawOffset = entitydata.getOriginalEntity().getRidingEntity() != null ? entitydata.getOriginalEntity().rotationYaw : entitydata.getOriginalEntity().renderYawOffset;
			head.setRotation(Quaternion.rotate((float)-Math.toRadians((yawOffset - entitydata.getOriginalEntity().rotationYaw) * ratio), new Vec3f(0,1,0), head.getRotation()));
			chest.setCustomRotation(Quaternion.rotate((float)-Math.toRadians((entitydata.getOriginalEntity().rotationYaw - yawOffset) * ratio), new Vec3f(0,1,0), null));
			return interpolatedPose;
		}
	}
	
	@Override
	public StaticAnimation loadAnimation(Armature armature, Dist dist) {
		AnimationDataExtractor.extractStaticAnimation(this.animationLocation, this, armature, dist);
		AnimationDataExtractor.extractStaticAnimation(this.lookUp.animationLocation, this.lookUp, armature, dist);
		AnimationDataExtractor.extractStaticAnimation(this.lookDown.animationLocation, this.lookDown, armature, dist);
		AnimationDataExtractor.extractStaticAnimation(this.lying.animationLocation, this.lying, armature, dist);
		return this;
	}
}