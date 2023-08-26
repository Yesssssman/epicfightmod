package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

@OnlyIn(Dist.CLIENT)
public class PWitherSkeletonMinionRenderer extends PHumanoidRenderer<PathfinderMob, HumanoidMobPatch<PathfinderMob>, HumanoidModel<PathfinderMob>, HumanoidMesh> {
	public PWitherSkeletonMinionRenderer() {
		super(Meshes.SKELETON);
	}

	@Override
	protected void setJointTransforms(HumanoidMobPatch<PathfinderMob> entitypatch, Armature armature, float partialTicks) {
		Pose pose = entitypatch.getArmature().getPose(partialTicks);
		Vec3f rootScale = pose.getOrDefaultTransform("Root").scale();
		Vec3f headScale = pose.getOrDefaultTransform("Head").scale();
		Vec3f shoulderLScale = pose.getOrDefaultTransform("Shoulder_L").scale();
		Vec3f shoulderRScale = pose.getOrDefaultTransform("Shoulder_R").scale();
		this.setJointTransform("Head", armature, OpenMatrix4f.createScale(headScale.x / rootScale.x, headScale.y / rootScale.y, headScale.z / rootScale.z));
		this.setJointTransform("Shoulder_R", armature, OpenMatrix4f.createScale(shoulderRScale.x / rootScale.x, shoulderRScale.y / rootScale.y, shoulderRScale.z / rootScale.z));
		this.setJointTransform("Shoulder_L", armature, OpenMatrix4f.createScale(shoulderLScale.x / rootScale.x, shoulderLScale.y / rootScale.y, shoulderLScale.z / rootScale.z));
	}
	
	@Override
	public HumanoidMesh getMesh(HumanoidMobPatch<PathfinderMob> entitypatch) {
		return Meshes.SKELETON;
	}
}