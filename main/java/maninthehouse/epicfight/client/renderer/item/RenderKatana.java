package maninthehouse.epicfight.client.renderer.item;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.item.ModItems;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderKatana extends RenderItemBase {
	private final ItemStack sheathStack = new ItemStack(ModItems.KATANA_SHEATH);
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, EnumHand hand) {
		GlStateManager.pushMatrix();
		VisibleMatrix4f modelMatrix = new VisibleMatrix4f(this.correctionMatrix);
		VisibleMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointByName("Tool_R").getAnimatedTransform(), modelMatrix, modelMatrix);
		GlStateManager.multMatrix(modelMatrix.toFloatBuffer());
		Minecraft.getMinecraft().getItemRenderer().renderItem(itemHolder.getOriginalEntity(), stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		modelMatrix = new VisibleMatrix4f(this.correctionMatrix);
		VisibleMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointByName("Tool_L").getAnimatedTransform(), modelMatrix, modelMatrix);
		GlStateManager.multMatrix(modelMatrix.toFloatBuffer());
		Minecraft.getMinecraft().getItemRenderer().renderItem(itemHolder.getOriginalEntity(), this.sheathStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
		GlStateManager.popMatrix();
    }
}