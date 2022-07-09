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
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.item.EpicFightItems;

@OnlyIn(Dist.CLIENT)
public class RenderKatana extends RenderItemBase {
	private final ItemStack sheathStack = new ItemStack(EpicFightItems.KATANA_SHEATH.get());
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
		OpenMatrix4f modelMatrix = new OpenMatrix4f(this.mainhandcorrectionMatrix);
		modelMatrix.mulFront(entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointByName("Tool_R").getAnimatedTransform());
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);
        poseStack.popPose();
        
		modelMatrix = new OpenMatrix4f(this.mainhandcorrectionMatrix);
		modelMatrix.mulFront(entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointByName("Tool_L").getAnimatedTransform());
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
        Minecraft.getInstance().getItemRenderer().renderStatic(this.sheathStack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);
        poseStack.popPose();
    }
}