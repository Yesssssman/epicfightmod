package yesman.epicfight.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public abstract class RenderItemMirror extends RenderItemBase {
	protected OpenMatrix4f leftHandCorrectionMatrix;
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, Hand hand, IRenderTypeBuffer buffer, MatrixStack matrixStackIn, int packedLight) {
		OpenMatrix4f modelMatrix = new OpenMatrix4f(hand == Hand.OFF_HAND ? this.leftHandCorrectionMatrix : this.correctionMatrix);
		String heldingHand = hand == Hand.MAIN_HAND ? "Tool_R" : "Tool_L";
		OpenMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointByName(heldingHand).getAnimatedTransform(), modelMatrix, modelMatrix);
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		
		matrixStackIn.push();
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		MathUtils.rotateStack(matrixStackIn, transpose);
		
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, matrixStackIn, buffer);
        matrixStackIn.pop();
	}
}