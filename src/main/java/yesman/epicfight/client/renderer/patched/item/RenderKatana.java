package yesman.epicfight.client.renderer.patched.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.item.EpicFightItems;

@OnlyIn(Dist.CLIENT)
public class RenderKatana extends RenderItemBase {
	private final ItemStack sheathStack = new ItemStack(EpicFightItems.KATANA_SHEATH.get());
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, MultiBufferSource buffer, PoseStack matrixStackIn, int packedLight) {
		matrixStackIn.pushPose();
		OpenMatrix4f modelMatrix = new OpenMatrix4f(this.correctionMatrix);
		modelMatrix.mulFront(entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointByName("Tool_R").getAnimatedTransform());
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		MathUtils.rotateStack(matrixStackIn, transpose);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, matrixStackIn, buffer, 0);
        matrixStackIn.popPose();
		modelMatrix = new OpenMatrix4f(this.correctionMatrix);
		modelMatrix.mulFront(entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointByName("Tool_L").getAnimatedTransform());
		transpose = OpenMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		MathUtils.rotateStack(matrixStackIn, transpose);
        Minecraft.getInstance().getItemRenderer().renderStatic(this.sheathStack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, matrixStackIn, buffer, 0);
    }
}