package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.world.entity.monster.WitherSkeleton;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.mob.WitherSkeletonPatch;

public class PWitherSkeletonRenderer extends PHumanoidRenderer<WitherSkeleton, WitherSkeletonPatch, SkeletonModel<WitherSkeleton>> {
	@Override
	protected void setJointTransforms(WitherSkeletonPatch entitypatch, Armature armature, float partialTicks) {
		Pose pose = entitypatch.getClientAnimator().getPose(partialTicks);
		Vec3f rootScale = pose.getTransformByName("Root").scale();
		Vec3f headScale = pose.getTransformByName("Head").scale();
		Vec3f shoulderLScale = pose.getTransformByName("Shoulder_L").scale();
		Vec3f shoulderRScale = pose.getTransformByName("Shoulder_R").scale();
		this.setJointTransform(9, armature, OpenMatrix4f.createScale(headScale.x / rootScale.x, headScale.y / rootScale.y, headScale.z / rootScale.z));
		this.setJointTransform(10, armature, OpenMatrix4f.createScale(shoulderRScale.x / rootScale.x, shoulderRScale.y / rootScale.y, shoulderRScale.z / rootScale.z));
		this.setJointTransform(15, armature, OpenMatrix4f.createScale(shoulderLScale.x / rootScale.x, shoulderLScale.y / rootScale.y, shoulderLScale.z / rootScale.z));
	}
}