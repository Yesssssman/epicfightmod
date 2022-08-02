package yesman.epicfight.api.animation.types;

import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

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
	public void tick(LivingEntityPatch<?> entitypatch) {
		super.tick(entitypatch);
		ClientAnimator animator = entitypatch.getClientAnimator();
		Layer layer = animator.getCompositeLayer(this.getPriority());
		AnimationPlayer player = layer.animationPlayer;
		
		if (player.getElapsedTime() >= this.totalTime - 0.06F) {
			layer.pause();
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		if (!entitypatch.isFirstPerson()) {
			if (entitypatch.getOriginal().isVisuallySwimming() || entitypatch.getOriginal().isFallFlying() || entitypatch.getOriginal().isAutoSpinAttack()) {
				Pose pose = this.lying.getPoseByTime(entitypatch, time, partialTicks);
				this.modifyPose(pose, entitypatch, time);
				return pose;
			} else {
				float pitch = entitypatch.getOriginal().getViewXRot(Minecraft.getInstance().getFrameTime());
				StaticAnimation interpolateAnimation;
				interpolateAnimation = (pitch > 0) ? this.lookDown : this.lookUp;
				Pose pose1 = super.getPoseByTime(entitypatch, time, partialTicks);	
				Pose pose2 = interpolateAnimation.getPoseByTime(entitypatch, time, partialTicks);
				this.modifyPose(pose2, entitypatch, time);
				Pose interpolatedPose = Pose.interpolatePose(pose1, pose2, (Math.abs(pitch) / 90.0F));
				return interpolatedPose;
			}
		}
		
		return super.getPoseByTime(entitypatch, time, partialTicks);
	}
	
	@Override
	protected void modifyPose(Pose pose, LivingEntityPatch<?> entitypatch, float time) {
		if (!entitypatch.isFirstPerson()) {
			JointTransform chest = pose.getOrDefaultTransform("Chest");
			JointTransform head = pose.getOrDefaultTransform("Head");
			float f = 90.0F;
			float ratio = (f - Math.abs(entitypatch.getOriginal().getXRot())) / f;
			float yawOffset = entitypatch.getOriginal().getVehicle() != null ? entitypatch.getOriginal().getYRot() : entitypatch.getOriginal().yBodyRot;
			MathUtils.mulQuaternion(Vector3f.YP.rotationDegrees((yawOffset - entitypatch.getOriginal().getYRot()) * ratio), head.rotation(), head.rotation());
			chest.frontResult(JointTransform.getRotation(Vector3f.YP.rotationDegrees((entitypatch.getOriginal().getYRot() - yawOffset) * ratio)), OpenMatrix4f::mulAsOriginFront);
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
	public void loadAnimation(ResourceManager resourceManager) {
		load(resourceManager, this);
		load(resourceManager, this.lookUp);
		load(resourceManager, this.lookDown);
		load(resourceManager, this.lying);
	}
}