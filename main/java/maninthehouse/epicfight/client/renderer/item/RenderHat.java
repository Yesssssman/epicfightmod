package maninthehouse.epicfight.client.renderer.item;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHat extends RenderItemBase {
	@Override
	public void renderItemOnHead(ItemStack stack, LivingData<?> itemHolder, float partialTicks) {
		Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(itemHolder.getOriginalEntity());
		if (render instanceof RenderLivingBase && ((RenderLivingBase)render).getMainModel() instanceof ModelBiped) {
			ModelRenderer model = ((ModelBiped)((RenderLivingBase)render).getMainModel()).bipedHead;
			LayerCustomHead layer = new LayerCustomHead(model);
			EntityLivingBase entity = itemHolder.getOriginalEntity();
			
			VisibleMatrix4f modelMatrix = new VisibleMatrix4f();
			VisibleMatrix4f.scale(new Vec3f(-0.94F, -0.94F, 0.94F), modelMatrix, modelMatrix);
			if(itemHolder.getOriginalEntity().isChild()) {
				VisibleMatrix4f.translate(new Vec3f(0.0F, -0.65F, 0.0F), modelMatrix, modelMatrix);
			}
			VisibleMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
			model.rotateAngleX = 0;
			model.rotateAngleY = 0;
			model.rotateAngleZ = 0;
			model.rotationPointX = 0;
			model.rotationPointY = 0;
			model.rotationPointZ = 0;
			GlStateManager.pushMatrix();//viewMatrixStack.push();
			GlStateManager.multMatrix(modelMatrix.toFloatBuffer());
			//MathUtils.translateStack(viewMatrixStack, modelMatrix);
			//MathUtils.rotateStack(viewMatrixStack, transpose);
			layer.doRenderLayer(entity, 0, 0, 0, 0, 0, 0, 0.0925F);
			GlStateManager.popMatrix();//viewMatrixStack.pop();
		}
	}
}