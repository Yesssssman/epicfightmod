package maninthehouse.epicfight.client.renderer.item;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.events.engine.RenderEngine;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItemBase {
	protected VisibleMatrix4f correctionMatrix;
	
	protected static final VisibleMatrix4f BACK_COORECTION;
	public static RenderEngine renderEngine;
	
	static {
		BACK_COORECTION = new VisibleMatrix4f();
		VisibleMatrix4f.translate(new Vec3f(0.5F, 1, 0.1F), BACK_COORECTION, BACK_COORECTION);
		VisibleMatrix4f.rotate((float)Math.toRadians(130), new Vec3f(0, 0, 1), BACK_COORECTION, BACK_COORECTION);
		VisibleMatrix4f.rotate((float)Math.toRadians(100), new Vec3f(0, 1, 0), BACK_COORECTION, BACK_COORECTION);
	}
	
	public RenderItemBase() {
		correctionMatrix = new VisibleMatrix4f();
		VisibleMatrix4f.rotate((float)Math.toRadians(-80), new Vec3f(1,0,0), correctionMatrix, correctionMatrix);
		VisibleMatrix4f.translate(new Vec3f(0,0.1F,0), correctionMatrix, correctionMatrix);
	}
	
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, EnumHand hand) {
		VisibleMatrix4f modelMatrix = this.getCorrectionMatrix(stack, itemHolder, hand);
		String heldingHand = hand == EnumHand.MAIN_HAND ? "Tool_R" : "Tool_L";
		VisibleMatrix4f jointTransform = itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointByName(heldingHand).getAnimatedTransform();
		VisibleMatrix4f.mul(jointTransform, modelMatrix, modelMatrix);
		GlStateManager.multMatrix(modelMatrix.toFloatBuffer());
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
	}
	
	public void renderItemBack(ItemStack stack, LivingData<?> itemHolder) {
		VisibleMatrix4f modelMatrix = new VisibleMatrix4f(BACK_COORECTION);
		VisibleMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(0).getAnimatedTransform(), modelMatrix, modelMatrix);
		GlStateManager.multMatrix(modelMatrix.toFloatBuffer());
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
	}
	
	public void renderItemOnHead(ItemStack stack, LivingData<?> itemHolder, float partialTicks) {
		VisibleMatrix4f modelMatrix = new VisibleMatrix4f();
		VisibleMatrix4f.translate(new Vec3f(0F, 0.2F, 0F), modelMatrix, modelMatrix);
		VisibleMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
		VisibleMatrix4f.scale(new Vec3f(0.6F, 0.6F, 0.6F), modelMatrix, modelMatrix);
		GlStateManager.multMatrix(modelMatrix.toFloatBuffer());
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
	}
	
	public VisibleMatrix4f getCorrectionMatrix(ItemStack stack, LivingData<?> itemHolder, EnumHand hand) {
		return new VisibleMatrix4f(correctionMatrix);
	}
}