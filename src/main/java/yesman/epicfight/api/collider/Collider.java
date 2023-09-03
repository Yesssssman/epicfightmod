package yesman.epicfight.api.collider;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class Collider {
	protected final Vec3 modelCenter;
	protected final AABB outerAABB;
	protected Vec3 worldCenter;
	
	public Collider(Vec3 center, @Nullable AABB outerAABB) {
		this.modelCenter = center;
		this.outerAABB = outerAABB;
		this.worldCenter = new Vec3(0.0D, 0.0D, 0.0D);
	}
	
	protected void transform(OpenMatrix4f mat) {
		this.worldCenter = OpenMatrix4f.transform(mat, this.modelCenter);
	}
	
	public List<Entity> updateAndSelectCollideEntity(LivingEntityPatch<?> entitypatch, AttackAnimation attackAnimation, float prevElapsedTime, float elapsedTime, Joint joint, float attackSpeed) {
		OpenMatrix4f transformMatrix;
		Armature armature = entitypatch.getArmature();
		int pathIndex = armature.searchPathIndex(joint.getName());
		
		if (pathIndex == -1) {
			Pose rootPose = new Pose();
			rootPose.putJointData("Root", JointTransform.empty());
			attackAnimation.modifyPose(attackAnimation, rootPose, entitypatch, elapsedTime, 1.0F);
			transformMatrix = rootPose.getOrDefaultTransform("Root").getAnimationBindedMatrix(entitypatch.getArmature().rootJoint, new OpenMatrix4f()).removeTranslation();
		} else {
			transformMatrix = armature.getBindedTransformByJointIndex(attackAnimation.getPoseByTime(entitypatch, elapsedTime, 1.0F), pathIndex);
		}
		
		OpenMatrix4f toWorldCoord = OpenMatrix4f.createTranslation(-(float)entitypatch.getOriginal().getX(), (float)entitypatch.getOriginal().getY(), -(float)entitypatch.getOriginal().getZ());
		transformMatrix.mulFront(toWorldCoord.mulBack(entitypatch.getModelMatrix(1.0F)));		
		this.transform(transformMatrix);
		
		return this.getCollideEntities(entitypatch.getOriginal());
	}
	
	public List<Entity> getCollideEntities(Entity entity) {
		List<Entity> list = entity.getLevel().getEntities(entity, this.getHitboxAABB(), (e) -> {
			if (e instanceof PartEntity<?> partEntity) {
				if (partEntity.getParent().is(entity)) {
					return false;
				}
			}
			
			return this.isCollide(e);
		});
		
		return list;
	}
	
	/** Display on debug mode **/
	@OnlyIn(Dist.CLIENT)
	public abstract void drawInternal(PoseStack matrixStackIn, MultiBufferSource buffer, OpenMatrix4f pose, boolean red);
	
	/** Display on debug mode **/
	@OnlyIn(Dist.CLIENT)
	public void draw(PoseStack matrixStackIn, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, AttackAnimation animation, Joint joint, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		Armature armature = entitypatch.getArmature();
		int pathIndex =  armature.searchPathIndex(joint.getName());
		EntityState state = animation.getState(entitypatch, elapsedTime);
		EntityState prevState = animation.getState(entitypatch, prevElapsedTime);
		boolean flag3 = prevState.attacking() || state.attacking() || (prevState.getLevel() < 2 && state.getLevel() > 2);
		OpenMatrix4f mat = null;
		
		if (pathIndex == -1) {
			Pose rootPose = new Pose();
			rootPose.putJointData("Root", JointTransform.empty());
			animation.modifyPose(animation, rootPose, entitypatch, elapsedTime, 1.0F);
			mat = rootPose.getOrDefaultTransform("Root").getAnimationBindedMatrix(entitypatch.getArmature().rootJoint, new OpenMatrix4f()).removeTranslation();
		} else {
			mat = armature.getBindedTransformByJointIndex(animation.getPoseByTime(entitypatch, elapsedTime, 0.0F), pathIndex);
		}
		
		this.drawInternal(matrixStackIn, buffer, mat, flag3);
	}
	
	public abstract boolean isCollide(Entity opponent);
	
	protected AABB getHitboxAABB() {
		return this.outerAABB.move(-this.worldCenter.x, this.worldCenter.y, -this.worldCenter.z);
	}
	
	public abstract Collider deepCopy();
	
	@Override
	public String toString() {
		return "[ColliderInfo] type: " + this.getClass() + " center: " + this.modelCenter;
	}
}