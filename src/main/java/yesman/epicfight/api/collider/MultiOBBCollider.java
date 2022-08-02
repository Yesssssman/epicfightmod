package yesman.epicfight.api.collider;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MultiOBBCollider extends MultiCollider<OBBCollider> {
	public MultiOBBCollider(int arrayLength, double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
		super(arrayLength, vecX, vecY, vecZ, OBBCollider.getInitialAABB(posX, posY, posZ, vecX, vecY, vecZ));
		this.bigCollider = new OBBCollider(this.outerAABB, posX, posY, posZ, vecX, vecY, vecZ);
	}
	
	@Override
	public OBBCollider createCollider() {
		return new OBBCollider(this.outerAABB, this.bigCollider.modelVertex[1].x, this.bigCollider.modelVertex[1].y, this.bigCollider.modelVertex[1].z, this.modelCenter.x, this.modelCenter.y, this.modelCenter.z);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void draw(PoseStack matrixStackIn, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, AttackAnimation animation, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		int numberOf = Math.max(Math.round((this.numberOfColliders + animation.getProperty(AttackAnimationProperty.COLLIDER_ADDER).orElse(0)) * attackSpeed), 1);
		float partialScale = 1.0F / (numberOf - 1);
		float interpolation = 0.0F;
		Armature armature = entitypatch.getEntityModel(Models.LOGICAL_SERVER).getArmature();
		int pathIndex =  armature.searchPathIndex(animation.getPathIndexByTime(elapsedTime));
		boolean red = entitypatch.getEntityState().attacking();
		List<OBBCollider> colliders = Lists.newArrayList();
		
		for (int i = 0; i < numberOf; i++) {
			colliders.add(this.createCollider());
		}
		
		for (OBBCollider obbCollider : colliders) {
			matrixStackIn.pushPose();
			OpenMatrix4f mat = null;
			armature.initializeTransform();
			
			if (pathIndex == -1) {
				mat = new OpenMatrix4f();
			} else {
				mat = Animator.getBindedJointTransformByIndex(entitypatch.getAnimator().getPose(interpolation), armature, pathIndex);
			}
			
			obbCollider.drawInternal(matrixStackIn, buffer, mat, red);
			obbCollider.transform(mat);
			
			interpolation += partialScale;
			matrixStackIn.popPose();
		}
	}
}