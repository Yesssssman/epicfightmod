package maninthehouse.epicfight.client.gui;

import java.util.Collection;
import java.util.Iterator;

import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.CapabilityEntity;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.config.ConfigurationIngame;
import maninthehouse.epicfight.effects.ModEffect;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HealthBarIndicator extends EntityIndicator {
	@Override
	public boolean shouldDraw(EntityLivingBase entityIn) {
		if (!ConfigurationIngame.showHealthIndicator) {
			return false;
		} else if (!entityIn.isNonBoss() || entityIn.isInvisible() || entityIn == Minecraft.getMinecraft().player.getRidingEntity()) {
			return false;
		} else if (entityIn.getDistanceSq(Minecraft.getMinecraft().getRenderViewEntity()) >= 400) {
			return false;
		} else if (entityIn instanceof EntityPlayer) {
			EntityPlayer playerIn = (EntityPlayer) entityIn;
			if (playerIn == Minecraft.getMinecraft().player) {
				return false;
			} else if (playerIn.isCreative() || playerIn.isSpectator()) {
				return false;
			}
		}
		
		if(entityIn.getActivePotionEffects().isEmpty() && entityIn.getHealth() >= entityIn.getMaxHealth() || entityIn.deathTime >= 19) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void drawIndicator(EntityLivingBase entityIn, double x, double y, double z, float partialTicks) {
		GlStateManager.pushMatrix();
		VisibleMatrix4f mvMatrix = super.setupMatrix(x, y, z, 0.0F, entityIn.height + 0.25F, 0.0F, true, false, partialTicks);
		GlStateManager.multMatrix(mvMatrix.toFloatBuffer());
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        
		if (!entityIn.getActivePotionEffects().isEmpty()) {
			Collection<PotionEffect> activeEffects = entityIn.getActivePotionEffects();
			Iterator<PotionEffect> iter = activeEffects.iterator();
			int acives = activeEffects.size();
			int row = acives > 1 ? 1 : 0;
			int column = ((acives-1) / 2);
			float startX = -0.8F + -0.3F * row;
			float startY = -0.15F + 0.15F * column;
			
			for (int i = 0; i <= column; i++) {
				for (int j = 0; j <= row; j++) {
					Potion effect = iter.next().getPotion();
					ResourceLocation rl;
					
					float texminU, texmaxU, texminV, texmaxV;
					
					if(effect instanceof ModEffect) {
						texminU = 0;
						texmaxU = 0;
						texminV = 256;
						texmaxV = 256;
						rl = ((ModEffect)effect).getIcon();
					} else {
						int i1 = effect.getStatusIconIndex();
						texminU = i1 % 8 * 18;
						texmaxU = texminU + 18;
						texminV = 198 + i1 / 8 * 18;
						texmaxV = texminV + 18;
						rl = GuiContainer.INVENTORY_BACKGROUND;
					}
					
					Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
					float screenX = startX + 0.3F * j;
					float screenY = startY + -0.3F * i;
					
					this.drawTexturedModalRect2DPlane(screenX, screenY, screenX + 0.3F, screenY + 0.3F, texminU, texmaxU, texminV, texmaxV);
					if(!iter.hasNext()) {
						break;
					}
				}
			}
		}
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(BATTLE_ICON);
		float ratio = entityIn.getHealth() / entityIn.getMaxHealth();
		float healthRatio = -0.5F + ratio;
		int textureRatio = (int) (62 * ratio);
		this.drawTexturedModalRect2DPlane(-0.5F, -0.05F, healthRatio, 0.05F, 1, 15, textureRatio, 20);
		this.drawTexturedModalRect2DPlane(healthRatio, -0.05F, 0.5F, 0.05F, textureRatio, 10, 62, 15);
		float absorption = entityIn.getAbsorptionAmount();
		
		if(absorption > 0.0D) {
			float absorptionRatio = absorption / entityIn.getMaxHealth();
			int absTexRatio = (int) (62 * absorptionRatio);
			this.drawTexturedModalRect2DPlane(-0.5F, -0.05F, absorptionRatio - 0.5F, 0.05F, 1, 20, absTexRatio, 25);
		}
		
		CapabilityEntity<?> entitycap = entityIn.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		
		if(entitycap != null && entitycap instanceof LivingData) {
			renderStunArmor((LivingData<?>)entitycap);
		}
		
		RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.enableLightmap();
		GlStateManager.popMatrix();
	}
	
	public void renderStunArmor(LivingData<?> entitydataFighter) {
		if(entitydataFighter.getStunArmor() == 0) {
			return;
		}
		
		float ratio = entitydataFighter.getStunArmor() / entitydataFighter.getMaxStunArmor();
		float barRatio = -0.5F + ratio;
		int textureRatio = (int) (62 * ratio);
		
		this.drawTexturedModalRect2DPlane(-0.5F, -0.1F, barRatio, -0.05F, 1, 5, textureRatio, 10);
		this.drawTexturedModalRect2DPlane(barRatio, -0.1F, 0.5F, -0.05F, textureRatio, 0, 63, 5);
	}
}