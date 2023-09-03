package yesman.epicfight.api.collider;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MultiOBBCollider extends MultiCollider<OBBCollider> {
	public MultiOBBCollider(int arrayLength, double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
		super(arrayLength, vecX, vecY, vecZ, null);
		
		AABB aabb = OBBCollider.getInitialAABB(posX, posY, posZ, vecX, vecY, vecZ);
		OBBCollider colliderForAll = new OBBCollider(aabb, posX, posY, posZ, vecX, vecY, vecZ);
		
		for (int i = 0; i < arrayLength; i++) {
			this.colliders.add(colliderForAll);
		}
	}
	
	public MultiOBBCollider(OBBCollider... colliders) {
		super(colliders);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void draw(PoseStack matrixStackIn, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, AttackAnimation animation, Joint joint, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		int numberOf = Math.max(Math.round((this.numberOfColliders + animation.getProperty(AttackAnimationProperty.EXTRA_COLLIDERS).orElse(0)) * attackSpeed), this.numberOfColliders);
		float partialScale = 1.0F / (numberOf - 1);
		float interpolation = 0.0F;
		Armature armature = entitypatch.getArmature();
		int pathIndex =  armature.searchPathIndex(joint.getName());
		EntityState state = animation.getState(entitypatch, elapsedTime);
		EntityState prevState = animation.getState(entitypatch, prevElapsedTime);
		boolean red = prevState.attacking() || state.attacking() || (prevState.getLevel() < 2 && state.getLevel() > 2);
		List<OBBCollider> colliders = Lists.newArrayList();
		float index = 0.0F;
		float interIndex = Math.min((float)(this.numberOfColliders - 1) / (numberOf - 1), 1.0F);
		
		for (int i = 0; i < numberOf; i++) {
			colliders.add(this.colliders.get((int)index).deepCopy());
			index += interIndex;
		}
		
		for (OBBCollider obbCollider : colliders) {
			OpenMatrix4f mat = null;
			armature.initializeTransform();
			
			float pt1 = prevElapsedTime + (elapsedTime - prevElapsedTime) * partialTicks;
			float pt2 = prevElapsedTime + (elapsedTime - prevElapsedTime) * interpolation;
			TransformSheet coordTransform = animation.getCoord();
			Vec3f p1 = coordTransform.getInterpolatedTranslation(pt1);
			Vec3f p2 = coordTransform.getInterpolatedTranslation(pt2);
			Vector3f gap = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
			
			matrixStackIn.pushPose();
			matrixStackIn.translate(gap.x(), gap.y(), gap.z());
			
			if (pathIndex == -1) {
				Pose rootPose = new Pose();
				rootPose.putJointData("Root", JointTransform.empty());
				animation.modifyPose(animation, rootPose, entitypatch, elapsedTime, 1.0F);
				mat = rootPose.getOrDefaultTransform("Root").getAnimationBindedMatrix(entitypatch.getArmature().rootJoint, new OpenMatrix4f()).removeTranslation();
			} else {
				mat = entitypatch.getArmature().getBindedTransformByJointIndex(entitypatch.getArmature().getPose(interpolation), pathIndex);
			}
			
			obbCollider.drawInternal(matrixStackIn, buffer, mat, red);
			matrixStackIn.popPose();
			
			interpolation += partialScale;
		}
	}
}