package yesman.epicfight.client.renderer.patched.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderShootableWeapon extends RenderItemBase {
	public RenderShootableWeapon(OpenMatrix4f correctionMatrix) {
		super(correctionMatrix, correctionMatrix);
	}
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, Hand hand, IRenderTypeBuffer buffer, MatrixStack poseStack, int packedLight) {
		OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entitypatch, hand);
		OpenMatrix4f jointTransform = entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointByName("Tool_L").getAnimatedTransform();
		modelMatrix.mulFront(jointTransform);
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
		Minecraft.getInstance().getItemInHandRenderer().renderItem(entitypatch.getOriginal(), stack, TransformType.THIRD_PERSON_RIGHT_HAND, false, poseStack, buffer, packedLight);
		poseStack.popPose();
		
		GlStateManager._enableDepthTest();
	}
}