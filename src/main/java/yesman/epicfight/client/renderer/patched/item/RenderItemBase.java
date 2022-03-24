package yesman.epicfight.client.renderer.patched.item;

import com.mojang.blaze3d.platform.GlStateManager;
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
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderItemBase {
	protected OpenMatrix4f correctionMatrix;
	protected static final OpenMatrix4f BACK_COORECTION;
	public static RenderEngine renderEngine;
	
	static {
		BACK_COORECTION = new OpenMatrix4f();
		BACK_COORECTION.translate(0.5F, 1, 0.1F).rotateDeg(130.0F, Vec3f.Z_AXIS).rotateDeg(100.0F, Vec3f.Y_AXIS);
	}
	
	public RenderItemBase() {
		this.correctionMatrix = new OpenMatrix4f().rotateDeg(-80.0F, Vec3f.X_AXIS).translate(0F, 0.1F, 0F);
	}
	
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, MultiBufferSource buffer, PoseStack matrixStackIn, int packedLight) {
		OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entitypatch, hand);
		String heldingHand = hand == InteractionHand.MAIN_HAND ? "Tool_R" : "Tool_L";
		OpenMatrix4f jointTransform = entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointByName(heldingHand).getAnimatedTransform();
		modelMatrix.mulFront(jointTransform);
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		MathUtils.rotateStack(matrixStackIn, transpose);
		Minecraft.getInstance().getItemInHandRenderer().renderItem(entitypatch.getOriginal(), stack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, buffer, packedLight);
		GlStateManager._enableDepthTest();
	}
	
	public void renderUnusableItemMount(ItemStack stack, LivingEntityPatch<?> entitypatch, MultiBufferSource buffer, PoseStack viewMatrixStack, int packedLight) {
		OpenMatrix4f modelMatrix = new OpenMatrix4f(BACK_COORECTION);
		modelMatrix.mulFront(entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointById(0).getAnimatedTransform());
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		MathUtils.rotateStack(viewMatrixStack, transpose);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, viewMatrixStack, buffer, 0);
	}
	
	public OpenMatrix4f getCorrectionMatrix(ItemStack stack, LivingEntityPatch<?> itemHolder, InteractionHand hand) {
		return new OpenMatrix4f(this.correctionMatrix);
	}
}