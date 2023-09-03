package yesman.epicfight.api.collider;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MultiLineCollider extends MultiCollider<LineCollider> {
	public MultiLineCollider(int arrayLength, double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
		super(arrayLength, posX, posY, posZ, LineCollider.getInitialAABB(posX, posY, posZ, vecX, vecY, vecZ));
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void draw(PoseStack matrixStackIn, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, AttackAnimation animation, Joint joint, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		int numberOf = Math.max(Math.round(this.numberOfColliders * attackSpeed), 1);
		float partialScale = 1.0F / numberOf;
		float interpolation = partialScale;
		Armature armature = entitypatch.getArmature();
		int pathIndex =  armature.searchPathIndex(joint.getName());
		boolean red = entitypatch.getEntityState().attacking();
		List<LineCollider> colliders = Lists.newArrayList();
		
		for (int i = 0; i < numberOf; i++) {
			colliders.add(this.colliders.get(i).deepCopy());
		}
		
		for (LineCollider lineCollider : colliders) {
			matrixStackIn.pushPose();
			OpenMatrix4f mat = null;
			
			float partialTime = Mth.lerp(interpolation, prevElapsedTime, elapsedTime);
			
			if (pathIndex == -1) {
				mat = new OpenMatrix4f();
			} else {
				mat = armature.getBindedTransformByJointIndex(animation.getPoseByTime(entitypatch, partialTime, 0.0F), pathIndex);
			}
			
			lineCollider.drawInternal(matrixStackIn, buffer, mat, red);
			interpolation += partialScale;
			matrixStackIn.popPose();
		}
	}
}