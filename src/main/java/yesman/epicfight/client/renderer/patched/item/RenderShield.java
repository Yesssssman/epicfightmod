package yesman.epicfight.client.renderer.patched.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderShield extends RenderItemBase {
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, HumanoidArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entitypatch, hand);
		boolean isInMainhand = (hand == InteractionHand.MAIN_HAND);
		Joint holdingHand = isInMainhand ? armature.toolR : armature.toolL;
		modelMatrix.mulFront(poses[holdingHand.getId()]);
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
		ItemDisplayContext transformType = isInMainhand ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
		Minecraft mc = Minecraft.getInstance();
		BakedModel model = mc.getItemRenderer().getItemModelShaper().getItemModel(stack);
		mc.getItemRenderer().render(stack, transformType, !isInMainhand, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, model);
		
		poseStack.popPose();
	}
}
