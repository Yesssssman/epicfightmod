package yesman.epicfight.client.renderer.patched.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderShield extends RenderItemBase {
	public RenderShield() {
		super(new OpenMatrix4f().rotateDeg(-80.0F, Vec3f.X_AXIS).translate(0F, 0.2F, 0F), new OpenMatrix4f().translate(0.0F, 1.5F, -0.15F).rotateDeg(180F, Vec3f.Y_AXIS).rotateDeg(90F, Vec3f.X_AXIS));
	}
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, Hand hand, IRenderTypeBuffer buffer, MatrixStack poseStack, int packedLight) {
		OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entitypatch, hand);
		String holdingHand = (hand == Hand.MAIN_HAND) ? "Tool_R" : "Tool_L";
		OpenMatrix4f jointTransform = entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointByName(holdingHand).getAnimatedTransform();
		modelMatrix.mulFront(jointTransform);
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
		TransformType transformType = (hand == Hand.MAIN_HAND) ? TransformType.THIRD_PERSON_RIGHT_HAND : TransformType.THIRD_PERSON_LEFT_HAND;
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, transformType, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer);
		poseStack.popPose();
		
		GlStateManager._enableDepthTest();
	}
}