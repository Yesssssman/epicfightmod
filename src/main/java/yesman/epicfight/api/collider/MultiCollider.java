package yesman.epicfight.api.collider;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.property.Property.AttackAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class MultiCollider<T extends Collider> extends Collider {
	protected T bigCollider;
	protected int numberOfColliders;
	
	public MultiCollider(int arrayLength, double posX, double posY, double posZ, AABB outerAABB) {
		super(new Vec3(posX, posY, posZ), outerAABB);
		this.numberOfColliders = arrayLength;
	}
	
	public abstract T createCollider();
	
	@Override
	public List<Entity> updateAndFilterCollideEntity(LivingEntityPatch<?> entitypatch, AttackAnimation attackAnimation, float prevElapsedTime, float elapsedTime, String jointName, float attackSpeed) {
		int numberOf = Math.max(Math.round((this.numberOfColliders + attackAnimation.getProperty(AttackAnimationProperty.COLLIDER_ADDER).orElse(0)) * attackSpeed), 1);
		float partialScale = 1.0F / (numberOf - 1);
		float interpolation = 0.0F;
		List<T> colliders = Lists.newArrayList();
		Entity original = entitypatch.getOriginal();
		
		for (int i = 0; i < numberOf; i++) {
			colliders.add(this.createCollider());
		}
		
		AABB outerBox = null;
		
		for (T collider : colliders) {
			OpenMatrix4f transformMatrix;
			Armature armature = entitypatch.getEntityModel(Models.LOGICAL_SERVER).getArmature();
			int pathIndex = armature.searchPathIndex(jointName);
			
			if (pathIndex == -1) {
				transformMatrix = new OpenMatrix4f();
			} else {
				transformMatrix = Animator.getBindedJointTransformByIndex(entitypatch.getAnimator().getPose(interpolation), armature, pathIndex);
			}
			
			double x = original.xo + (original.getX() - original.xo) * interpolation;
			double y = original.yo + (original.getY() - original.yo) * interpolation;
			double z = original.zo + (original.getZ() - original.zo) * interpolation;
			
			OpenMatrix4f mvMatrix = OpenMatrix4f.createTranslation(-(float)x, (float)y, -(float)z);
			transformMatrix.mulFront(mvMatrix.mulBack(entitypatch.getModelMatrix(interpolation)));
			collider.transform(transformMatrix);
			
			interpolation += partialScale;
			
			if (interpolation >= 1.0F) {
				this.transform(transformMatrix);
			}
			
			if (outerBox == null) {
				outerBox = collider.getHitboxAABB();
			} else {
				outerBox.minmax(collider.getHitboxAABB());
			}
		}
		
		List<Entity> entities = entitypatch.getOriginal().level.getEntities(entitypatch.getOriginal(), outerBox);
		
		entities.removeIf((entity) -> {
			boolean remove = true;
			
			for (T collider : colliders) {
				if (collider.collide(entity)) {
					remove = false;
				}
			}
			
			return remove;
		});
		
		return entities;
	}
	
	@Override
	public void drawInternal(PoseStack matrixStackIn, MultiBufferSource buffer, OpenMatrix4f pose, boolean red) {
		;
	}
	
	protected void filterHitEntities(List<Entity> entities) {
		entities.removeIf((entity) -> !this.collide(entity));
	}
	
	@Override
	protected AABB getHitboxAABB() {
		return null;
	}
	
	@Override
	protected boolean collide(Entity opponent) {
		return false;
	}
}