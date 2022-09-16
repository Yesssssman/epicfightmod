package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.CreatureEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

@OnlyIn(Dist.CLIENT)
public class PWitherSkeletonMinionRenderer extends PHumanoidRenderer<CreatureEntity, HumanoidMobPatch<CreatureEntity>, BipedModel<CreatureEntity>> {
	@Override
	protected void setJointTransforms(HumanoidMobPatch<CreatureEntity> entitypatch, Armature armature, float partialTicks) {
		Pose pose = entitypatch.getClientAnimator().getPose(partialTicks);
		Vec3f rootScale = pose.getOrDefaultTransform("Root").scale();
		Vec3f headScale = pose.getOrDefaultTransform("Head").scale();
		Vec3f shoulderLScale = pose.getOrDefaultTransform("Shoulder_L").scale();
		Vec3f shoulderRScale = pose.getOrDefaultTransform("Shoulder_R").scale();
		this.setJointTransform(9, armature, OpenMatrix4f.createScale(headScale.x / rootScale.x, headScale.y / rootScale.y, headScale.z / rootScale.z));
		this.setJointTransform(10, armature, OpenMatrix4f.createScale(shoulderRScale.x / rootScale.x, shoulderRScale.y / rootScale.y, shoulderRScale.z / rootScale.z));
		this.setJointTransform(15, armature, OpenMatrix4f.createScale(shoulderLScale.x / rootScale.x, shoulderLScale.y / rootScale.y, shoulderLScale.z / rootScale.z));
	}
}