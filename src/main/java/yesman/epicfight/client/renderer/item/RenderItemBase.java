package yesman.epicfight.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class RenderItemBase {
	protected OpenMatrix4f correctionMatrix;
	protected static final OpenMatrix4f BACK_COORECTION;
	public static RenderEngine renderEngine;
	
	static {
		BACK_COORECTION = new OpenMatrix4f();
		OpenMatrix4f.translate(new Vec3f(0.5F, 1, 0.1F), BACK_COORECTION, BACK_COORECTION);
		OpenMatrix4f.rotate((float)Math.toRadians(130), new Vec3f(0, 0, 1), BACK_COORECTION, BACK_COORECTION);
		OpenMatrix4f.rotate((float)Math.toRadians(100), new Vec3f(0, 1, 0), BACK_COORECTION, BACK_COORECTION);
	}
	
	public RenderItemBase() {
		this.correctionMatrix = new OpenMatrix4f();
		OpenMatrix4f.rotate((float)Math.toRadians(-80), new Vec3f(1,0,0), this.correctionMatrix, this.correctionMatrix);
		OpenMatrix4f.translate(new Vec3f(0,0.1F,0), this.correctionMatrix, this.correctionMatrix);
	}
	
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, Hand hand, IRenderTypeBuffer buffer, MatrixStack matrixStackIn, int packedLight) {
		OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, itemHolder, hand);
		String heldingHand = hand == Hand.MAIN_HAND ? "Tool_R" : "Tool_L";
		OpenMatrix4f jointTransform = itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointByName(heldingHand).getAnimatedTransform();
		OpenMatrix4f.mul(jointTransform, modelMatrix, modelMatrix);
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		MathUtils.rotateStack(matrixStackIn, transpose);
		Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(itemHolder.getOriginalEntity(), stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, buffer, packedLight);
		GlStateManager.enableDepthTest();
	}
	
	public void renderUnusableItemMount(ItemStack stack, LivingData<?> itemHolder, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight) {
		OpenMatrix4f modelMatrix = new OpenMatrix4f(BACK_COORECTION);
		OpenMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(0).getAnimatedTransform(), modelMatrix, modelMatrix);
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		MathUtils.rotateStack(viewMatrixStack, transpose);
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, viewMatrixStack, buffer);
	}
	
	public OpenMatrix4f getCorrectionMatrix(ItemStack stack, LivingData<?> itemHolder, Hand hand) {
		return new OpenMatrix4f(this.correctionMatrix);
	}
}