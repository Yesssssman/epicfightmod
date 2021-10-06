package yesman.epicfight.physics;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import yesman.epicfight.animation.property.Property.AttackAnimationProperty;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Armature;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public abstract class MultiCollider<T extends Collider> extends Collider {
	protected T bigCollider;
	protected int numberOfColliders;
	
	public MultiCollider(int arrayLength, float posX, float posY, float posZ, AxisAlignedBB outerAABB) {
		super(new Vec3f(posX, posY, posZ), outerAABB);
		this.numberOfColliders = arrayLength;
	}
	
	public abstract T createCollider();
	
	@Override
	public List<Entity> updateAndFilterCollideEntity(LivingData<?> entitydata, AttackAnimation attackAnimation, float prevElapsedTime, float elapsedTime, int jointIndexer, float attackSpeed) {
		int numberOf = Math.max(Math.round((this.numberOfColliders + attackAnimation.getProperty(AttackAnimationProperty.COLLIDER_ADDER).orElse(0)) * attackSpeed), 1);
		float partialScale = 1.0F / (numberOf - 1);
		float interpolation = 0.0F;
		List<T> colliders = Lists.newArrayList();
		
		for (int i = 0; i < numberOf; i++) {
			colliders.add(this.createCollider());
		}
		
		AxisAlignedBB outerBox = null;
		
		for (T collider : colliders) {
			OpenMatrix4f transformMatrix;
			if (jointIndexer == -1) {
				transformMatrix = new OpenMatrix4f();
			} else {
				float partialTime = MathHelper.lerp(interpolation, prevElapsedTime, elapsedTime);
				Armature armature = entitydata.getEntityModel(Models.LOGICAL_SERVER).getArmature();
				armature.initializeTransform();
				transformMatrix = entitydata.getServerAnimator().getJointTransformByIndex(attackAnimation.getPoseByTime(entitydata, partialTime),
						armature.getJointHierarcy(), new OpenMatrix4f(), jointIndexer);
				
			}
			
			OpenMatrix4f mvMatrix = OpenMatrix4f.translate(new Vec3f(-(float)entitydata.getOriginalEntity().getPosX(),
					(float)entitydata.getOriginalEntity().getPosY(), -(float)entitydata.getOriginalEntity().getPosZ()), new OpenMatrix4f(), null);
			OpenMatrix4f.mul(mvMatrix, entitydata.getModelMatrix(1.0F), mvMatrix);
			OpenMatrix4f.mul(mvMatrix, transformMatrix, transformMatrix);
			collider.transform(transformMatrix);
			
			interpolation += partialScale;
			
			if (interpolation >= 1.0F) {
				this.transform(transformMatrix);
			}
			
			if (outerBox == null) {
				outerBox = collider.getHitboxAABB();
			} else {
				outerBox.union(collider.getHitboxAABB());
			}
		}
		
		List<Entity> entities = entitydata.getOriginalEntity().world.getEntitiesWithinAABBExcludingEntity(entitydata.getOriginalEntity(), outerBox);
		
		entities.removeIf((entity) -> {
			boolean remove = true;
			for (T collider : colliders) {
				if (collider.isCollideWith(entity)) {
					remove = false;
				}
			}
			return remove;
		});
		
		return entities;
	}
	
	@Override
	protected void drawInternal(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, OpenMatrix4f pose, boolean red) {
		;
	}
	
	protected void filterHitEntities(List<Entity> entities) {
		entities.removeIf((entity) -> !this.isCollideWith(entity));
	}
	
	@Override
	protected AxisAlignedBB getHitboxAABB() {
		return null;
	}
	
	@Override
	protected boolean isCollideWith(Entity opponent) {
		return false;
	}
}