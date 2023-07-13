package yesman.epicfight.client.renderer.patched.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderItemBase {
	protected final OpenMatrix4f mainhandcorrectionMatrix;
	protected final OpenMatrix4f offhandCorrectionMatrix;
	protected static final OpenMatrix4f BACK_COORECTION = new OpenMatrix4f().translate(0.5F, 1F, 0.1F).rotateDeg(130.0F, Vec3f.Z_AXIS).rotateDeg(100.0F, Vec3f.Y_AXIS);
	public static RenderEngine renderEngine;
	
	public RenderItemBase() {
		this(new OpenMatrix4f().translate(0F, 0F, -0.13F).rotateDeg(-90.0F, Vec3f.X_AXIS), new OpenMatrix4f().translate(0F, 0F, -0.13F).rotateDeg(-90.0F, Vec3f.X_AXIS));
	}
	
	public RenderItemBase(OpenMatrix4f mainhandcorrectionMatrix, OpenMatrix4f offhandCorrectionMatrix) {
		this.mainhandcorrectionMatrix = mainhandcorrectionMatrix;
		this.offhandCorrectionMatrix = offhandCorrectionMatrix;
	}
	
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
		OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entitypatch, hand);
		boolean isInMainhand = (hand == InteractionHand.MAIN_HAND);
		String holdingHand = isInMainhand ? "Tool_R" : "Tool_L";
		OpenMatrix4f jointTransform = entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointByName(holdingHand).getAnimatedTransform();
		modelMatrix.mulFront(jointTransform);
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
		TransformType transformType = isInMainhand ? TransformType.THIRD_PERSON_RIGHT_HAND : TransformType.THIRD_PERSON_LEFT_HAND;
		Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(entitypatch.getOriginal(), stack, transformType, !isInMainhand, poseStack, buffer, packedLight);
		poseStack.popPose();
	}
	
	public void renderUnusableItemMount(ItemStack stack, LivingEntityPatch<?> entitypatch, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
		OpenMatrix4f modelMatrix = new OpenMatrix4f(BACK_COORECTION);
		modelMatrix.mulFront(entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointById(0).getAnimatedTransform());
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, modelMatrix);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);
        poseStack.popPose();
	}
	
	protected void mulPoseStack(PoseStack poseStack, OpenMatrix4f pose) {
		OpenMatrix4f transposed = pose.transpose(null);
		MathUtils.translateStack(poseStack, pose);
		MathUtils.rotateStack(poseStack, transposed);
		MathUtils.scaleStack(poseStack, transposed);
	}
	
	public OpenMatrix4f getCorrectionMatrix(ItemStack stack, LivingEntityPatch<?> itemHolder, InteractionHand hand) {
		return new OpenMatrix4f(hand == InteractionHand.MAIN_HAND ? this.mainhandcorrectionMatrix : this.offhandCorrectionMatrix);
	}
}