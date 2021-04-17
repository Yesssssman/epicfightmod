package maninthehouse.epicfight.client.renderer.item;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.utils.math.MathUtils;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderElytra extends RenderItemBase {
	private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
	private final ModelElytra modelElytra;
	
	public RenderElytra() {
		super();
		modelElytra = new ModelElytra();
	}
	
	@Override
	public void renderItemOnHead(ItemStack stack, LivingData<?> itemHolder, float partialTicks) {
		EntityLivingBase entity = itemHolder.getOriginalEntity();
		VisibleMatrix4f modelMatrix = new VisibleMatrix4f();
		VisibleMatrix4f.scale(new Vec3f(-0.9F, -0.9F, 0.9F), modelMatrix, modelMatrix);
		VisibleMatrix4f.translate(new Vec3f(0F, -0.5F, 0.125F), modelMatrix, modelMatrix);
		VisibleMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(8).getAnimatedTransform(), modelMatrix, modelMatrix);
		GlStateManager.multMatrix(modelMatrix.toFloatBuffer());
		
		if (entity instanceof AbstractClientPlayer) {
			AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) entity;
			if (abstractclientplayer.isPlayerInfoSet() && abstractclientplayer.getLocationElytra() != null) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(abstractclientplayer.getLocationElytra());
			} else if (abstractclientplayer.hasPlayerInfo() && abstractclientplayer.getLocationCape() != null
					&& abstractclientplayer.isWearing(EnumPlayerModelParts.CAPE)) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(abstractclientplayer.getLocationCape());
			} else {
				Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_ELYTRA);
			}
		} else {
			Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_ELYTRA);
		}
		
		float f = MathUtils.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
        float f1 = MathUtils.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
        float f2 = f1 - f;
		float f7 = itemHolder.getPitch(partialTicks);
		
		this.modelElytra.isChild = entity.isChild();
		this.modelElytra.setRotationAngles(entity.limbSwing, entity.limbSwingAmount, entity.ticksExisted, f2, f7, 0.0625F, entity);
	    this.modelElytra.render(entity, entity.limbSwing, entity.limbSwingAmount, entity.ticksExisted, f2, f7, 0.0625F);
	}
}
