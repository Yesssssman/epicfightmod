package maninthehouse.epicfight.client.gui;

import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TargetIndicator extends EntityIndicator {
	@Override
	public boolean shouldDraw(EntityLivingBase entityIn) {
		if(entityIn != ClientEngine.INSTANCE.getPlayerData().getAttackTarget())
			return false;
		else if(entityIn.isInvisible() || !entityIn.isEntityAlive() || entityIn == Minecraft.getMinecraft().player.getRidingEntity())
			return false;
		else if(entityIn.getDistanceSq(Minecraft.getMinecraft().getRenderViewEntity()) >= 400)
			return false;
		else if (entityIn instanceof EntityPlayer) {
			EntityPlayer playerIn = (EntityPlayer) entityIn;
			if(playerIn.isSpectator())
				return false;
		}
		
		return true;
	}
	
	@Override
	public void drawIndicator(EntityLivingBase entityIn, double x, double y, double z, float partialTicks) {
		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
		VisibleMatrix4f mvMatrix = this.setupMatrix(x, y, z, 0.0F, entityIn.height + 0.45F, 0.0F, true, false, partialTicks);
		GlStateManager.multMatrix(mvMatrix.toFloatBuffer());
		Minecraft.getMinecraft().getTextureManager().bindTexture(BATTLE_ICON);
		this.drawTexturedModalRect2DPlane(-0.1F, -0.1F, 0.1F, 0.1F, 65, 2, 91, 36);
		RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.enableLightmap();
		GlStateManager.popMatrix();
	}
}