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
import yesman.epicfight.item.EpicFightItems;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public class RenderKatana extends RenderItemBase {
	private final ItemStack sheathStack = new ItemStack(EpicFightItems.KATANA_SHEATH.get());
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, Hand hand, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight) {
		viewMatrixStack.push();
		OpenMatrix4f modelMatrix = new OpenMatrix4f(correctionMatrix);
		OpenMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointByName("Tool_R").getAnimatedTransform(), modelMatrix, modelMatrix);
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		MathUtils.rotateStack(viewMatrixStack, transpose);
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, viewMatrixStack, buffer);
        viewMatrixStack.pop();
		modelMatrix = new OpenMatrix4f(correctionMatrix);
		OpenMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointByName("Tool_L").getAnimatedTransform(), modelMatrix, modelMatrix);
		transpose = OpenMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		MathUtils.rotateStack(viewMatrixStack, transpose);
        Minecraft.getInstance().getItemRenderer().renderItem(sheathStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, viewMatrixStack, buffer);
    }
}