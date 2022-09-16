package yesman.epicfight.api.collider;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MultiLineCollider extends MultiCollider<LineCollider> {
	public MultiLineCollider(int arrayLength, double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
		super(arrayLength, posX, posY, posZ, LineCollider.getInitialAABB(posX, posY, posZ, vecX, vecY, vecZ));
		this.bigCollider = new LineCollider(this.outerAABB, posX, posY, posZ, vecX, vecY, vecZ);
	}
	
	@Override
	public LineCollider createCollider() {
		return new LineCollider(this.outerAABB, this.modelCenter.x, this.modelCenter.y, this.modelCenter.z, this.bigCollider.modelVec.x, this.bigCollider.modelVec.y, this.bigCollider.modelVec.z);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void draw(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, LivingEntityPatch<?> entitypatch, AttackAnimation animation, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		int numberOf = Math.max(Math.round(this.numberOfColliders * attackSpeed), 1);
		float partialScale = 1.0F / numberOf;
		float interpolation = partialScale;
		Armature armature = entitypatch.getEntityModel(Models.LOGICAL_SERVER).getArmature();
		int pathIndex =  armature.searchPathIndex(animation.getPathIndexByTime(elapsedTime));
		boolean red = entitypatch.getEntityState().attacking();
		List<LineCollider> colliders = Lists.newArrayList();
		
		for (int i = 0; i < numberOf; i++) {
			colliders.add(this.createCollider());
		}
		
		for (LineCollider lineCollider : colliders) {
			matrixStackIn.pushPose();
			OpenMatrix4f mat = null;
			
			float partialTime = MathHelper.lerp(interpolation, prevElapsedTime, elapsedTime);
			
			if (pathIndex == -1) {
				mat = new OpenMatrix4f();
			} else {
				mat = Animator.getBindedJointTransformByIndex(animation.getPoseByTime(entitypatch, partialTime, 0.0F), armature, pathIndex);
			}
			
			lineCollider.drawInternal(matrixStackIn, buffer, mat, red);
			interpolation += partialScale;
			matrixStackIn.popPose();
		}
	}
}