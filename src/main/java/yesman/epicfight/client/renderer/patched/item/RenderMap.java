package yesman.epicfight.client.renderer.patched.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderMap extends RenderItemBase {
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, HumanoidArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
		OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entitypatch, hand);
		boolean isInMainhand = (hand == InteractionHand.MAIN_HAND);
		Joint holdingHand = isInMainhand ? armature.toolR : armature.toolL;
		OpenMatrix4f jointTransform = poses[holdingHand.getId()];
		modelMatrix.mulFront(jointTransform);
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
		TransformType transformType = isInMainhand ? TransformType.THIRD_PERSON_RIGHT_HAND : TransformType.THIRD_PERSON_LEFT_HAND;
		Minecraft.getInstance().getItemInHandRenderer().renderItem(entitypatch.getOriginal(), stack, transformType, !isInMainhand, poseStack, buffer, packedLight);
		
		poseStack.popPose();
    }
}