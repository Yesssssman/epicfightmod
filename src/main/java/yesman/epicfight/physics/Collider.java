package yesman.epicfight.physics;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Armature;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;
import yesman.epicfight.utils.math.Vec4f;

public abstract class Collider {
	protected final Vec3f modelCenter;
	protected final AxisAlignedBB outerAABB;
	protected Vec3f worldCenter;

	public Collider(Vec3f center, @Nullable AxisAlignedBB outerAABB) {
		this.modelCenter = center;
		this.outerAABB = outerAABB;
		this.worldCenter = new Vec3f();
	}
	
	protected void transform(OpenMatrix4f mat) {
		Vec4f temp = new Vec4f(this.modelCenter.x, this.modelCenter.y, this.modelCenter.z, 1);
		OpenMatrix4f.transform(mat, temp, temp);
		this.worldCenter.x = temp.x;
		this.worldCenter.y = temp.y;
		this.worldCenter.z = temp.z;
	}
	
	public List<Entity> updateAndFilterCollideEntity(LivingData<?> entitydata, AttackAnimation attackAnimation, float prevElapsedTime, float elapsedTime,
			int jointIndexer, float attackSpeed) {
		OpenMatrix4f transformMatrix;
		
		if(jointIndexer == -1) {
			transformMatrix = new OpenMatrix4f();
		} else {
			float partialTime = MathHelper.lerp(0.5F, prevElapsedTime, elapsedTime);
			transformMatrix = entitydata.getServerAnimator().getJointTransformByIndex(attackAnimation.getPoseByTime(entitydata, partialTime),
					entitydata.getEntityModel(Models.LOGICAL_SERVER).getArmature().getJointHierarcy(), new OpenMatrix4f(), jointIndexer);
		}
		
		OpenMatrix4f mvMatrix = OpenMatrix4f.translate(new Vec3f(-(float)entitydata.getOriginalEntity().getPosX(),
				(float)entitydata.getOriginalEntity().getPosY(), -(float)entitydata.getOriginalEntity().getPosZ()), new OpenMatrix4f(), null);
		OpenMatrix4f.mul(mvMatrix, entitydata.getModelMatrix(1.0F), mvMatrix);
		OpenMatrix4f.mul(mvMatrix, transformMatrix, transformMatrix);
		
		this.transform(transformMatrix);
		
		List<Entity> list = entitydata.getOriginalEntity().world.getEntitiesWithinAABBExcludingEntity(entitydata.getOriginalEntity(), this.getHitboxAABB());
		this.filterHitEntities(list);
		
		return list;
	}
	
	/** Display on debug mode **/
	@OnlyIn(Dist.CLIENT)
	protected abstract void drawInternal(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, OpenMatrix4f pose, boolean red);
	
	
	/** Display on debug mode **/
	@OnlyIn(Dist.CLIENT)
	public void draw(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, LivingData<?> entitydata, AttackAnimation animation, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		int index = animation.getIndexer(elapsedTime);
		boolean flag3 = entitydata.getEntityState().shouldDetectCollision();
		OpenMatrix4f mat = null;
		
		if (index == -1) {
			mat = new OpenMatrix4f();
		} else {
			Armature armature = entitydata.getEntityModel(Models.LOGICAL_SERVER).getArmature();
			armature.initializeTransform();
			mat = entitydata.getClientAnimator().getJointTransformByIndex(animation.getPoseByTime(entitydata, elapsedTime),
					armature.getJointHierarcy(), new OpenMatrix4f(), index);
		}
		
		this.drawInternal(matrixStackIn, buffer, mat, flag3);
	}
	
	protected abstract boolean isCollideWith(Entity opponent);
	
	protected void filterHitEntities(List<Entity> entities) {
		entities.removeIf((entity) -> !this.isCollideWith(entity));
		/**
		Iterator<Entity> iterator = entities.iterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (!this.isCollideWith(entity)) {
				iterator.remove();
			}
		}**/
	}
	
	protected AxisAlignedBB getHitboxAABB() {
		return this.outerAABB.offset(-this.worldCenter.x, this.worldCenter.y, -this.worldCenter.z);
	}
}