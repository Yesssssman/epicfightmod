package yesman.epicfight.api.collider;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class Collider {
	protected final Vector3d modelCenter;
	protected final AxisAlignedBB outerAABB;
	protected Vector3d worldCenter;

	public Collider(Vector3d center, @Nullable AxisAlignedBB outerAABB) {
		this.modelCenter = center;
		this.outerAABB = outerAABB;
		this.worldCenter = new Vector3d(0, 0, 0);
	}
	
	protected void transform(OpenMatrix4f mat) {
		this.worldCenter = OpenMatrix4f.transform(mat, this.modelCenter);
	}
	
	public List<Entity> updateAndSelectCollideEntity(LivingEntityPatch<?> entitypatch, AttackAnimation attackAnimation, float prevElapsedTime, float elapsedTime, String jointName, float attackSpeed) {
		OpenMatrix4f transformMatrix;
		Armature armature = entitypatch.getEntityModel(Models.LOGICAL_SERVER).getArmature();
		int pathIndex = armature.searchPathIndex(jointName);
		
		if (pathIndex == -1) {
			transformMatrix = new OpenMatrix4f();
		} else {
			transformMatrix = Animator.getBindedJointTransformByIndex(attackAnimation.getPoseByTime(entitypatch, elapsedTime, 1.0F), armature, pathIndex);
		}
		
		OpenMatrix4f toWorldCoord = OpenMatrix4f.createTranslation(-(float)entitypatch.getOriginal().getX(), (float)entitypatch.getOriginal().getY(), -(float)entitypatch.getOriginal().getZ());
		transformMatrix.mulFront(toWorldCoord.mulBack(entitypatch.getModelMatrix(1.0F)));		
		this.transform(transformMatrix);
		
		return this.getCollideEntities(entitypatch.getOriginal());
	}
	
	public List<Entity> getCollideEntities(Entity entity) {
		List<Entity> list = entity.level.getEntities(entity, this.getHitboxAABB(), (e) -> {
			if (e instanceof PartEntity) {
				if (((PartEntity<?>)e).getParent().is(entity)) {
					return false;
				}
			}
			
			return this.isCollide(e);
		});
		
		return list;
	}
	
	/** Display on debug mode **/
	@OnlyIn(Dist.CLIENT)
	public abstract void drawInternal(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, OpenMatrix4f pose, boolean red);
	
	/** Display on debug mode **/
	@OnlyIn(Dist.CLIENT)
	public void draw(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, LivingEntityPatch<?> entitypatch, AttackAnimation animation, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		Armature armature = entitypatch.getEntityModel(Models.LOGICAL_SERVER).getArmature();
		int pathIndex =  armature.searchPathIndex(animation.getPathIndexByTime(elapsedTime));
		boolean flag3 = entitypatch.getEntityState().attacking();
		OpenMatrix4f mat = null;
		
		if (pathIndex == -1) {
			mat = new OpenMatrix4f();
		} else {
			mat = Animator.getBindedJointTransformByIndex(animation.getPoseByTime(entitypatch, elapsedTime, 0.0F), armature, pathIndex);
		}
		
		this.drawInternal(matrixStackIn, buffer, mat, flag3);
	}
	
	protected abstract boolean isCollide(Entity opponent);
	
	protected AxisAlignedBB getHitboxAABB() {
		return this.outerAABB.move(-this.worldCenter.x, this.worldCenter.y, -this.worldCenter.z);
	}
	
	@Override
	public String toString() {
		return "[ColliderInfo] type: " + this.getClass() + " center: " + this.modelCenter;
	}
}