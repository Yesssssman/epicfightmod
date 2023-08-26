package yesman.epicfight.client.renderer.patched.item;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderShootableWeapon extends RenderItemBase {
	public RenderShootableWeapon(OpenMatrix4f correctionMatrix) {
		super(correctionMatrix, correctionMatrix);
	}
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, HumanoidArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
		OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entitypatch, hand);
		OpenMatrix4f jointTransform = poses[armature.toolL.getId()];
		modelMatrix.mulFront(jointTransform);
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
		Minecraft.getInstance().getItemInHandRenderer().renderItem(entitypatch.getOriginal(), stack, TransformType.THIRD_PERSON_RIGHT_HAND, false, poseStack, buffer, packedLight);
		poseStack.popPose();
		
		GlStateManager._enableDepthTest();
	}
}