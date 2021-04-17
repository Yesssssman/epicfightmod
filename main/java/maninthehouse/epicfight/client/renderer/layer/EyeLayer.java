package maninthehouse.epicfight.client.renderer.layer;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EyeLayer<E extends EntityLivingBase, T extends LivingData<E>> extends Layer<E, T> {
	private final ResourceLocation eyeTexture;
	
	public EyeLayer(ResourceLocation eyeTexture) {
		this.eyeTexture = eyeTexture;
	}
	
	@Override
	public void renderLayer(T entitydata, E entityliving, VisibleMatrix4f[] poses, float partialTicks) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.eyeTexture);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlpha();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
		entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).draw(poses);
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}
}