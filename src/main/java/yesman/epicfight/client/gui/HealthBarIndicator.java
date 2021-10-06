package yesman.epicfight.client.gui;

import java.util.Collection;
import java.util.Iterator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.CapabilityEntity;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.renderer.ModRenderTypes;
import yesman.epicfight.effects.ModEffect;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class HealthBarIndicator extends EntityIndicator {
	@Override
	public boolean shouldDraw(LivingEntity entityIn) {
		if (!EpicFightMod.CLIENT_INGAME_CONFIG.showHealthIndicator.getValue()) {
			return false;
		} else if (!entityIn.canChangeDimension() || entityIn.isInvisible() || entityIn == Minecraft.getInstance().player.getRidingEntity()) {
			return false;
		} else if (entityIn.getDistanceSq(Minecraft.getInstance().getRenderViewEntity()) >= 400) {
			return false;
		} else if (entityIn instanceof PlayerEntity) {
			PlayerEntity playerIn = (PlayerEntity) entityIn;
			if (playerIn == Minecraft.getInstance().player) {
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
	public void drawIndicator(LivingEntity entityIn, MatrixStack matStackIn, IRenderTypeBuffer bufferIn, float partialTicks) {
		Matrix4f mvMatrix = super.getMVMatrix(matStackIn, entityIn, 0.0F, entityIn.getHeight() + 0.25F, 0.0F, true, false, partialTicks);
		
		if (!entityIn.getActivePotionEffects().isEmpty()) {
			Collection<EffectInstance> activeEffects = entityIn.getActivePotionEffects();
			Iterator<EffectInstance> iter = activeEffects.iterator();
			int acives = activeEffects.size();
			int row = acives > 1 ? 1 : 0;
			int column = ((acives-1) / 2);
			float startX = -0.8F + -0.3F * row;
			float startY = -0.15F + 0.15F * column;
			
			for (int i = 0; i <= column; i++) {
				for (int j = 0; j <= row; j++) {
					Effect effect = iter.next().getPotion();
					ResourceLocation rl;
					
					if(effect instanceof ModEffect) {
						rl = ((ModEffect)effect).getIcon();
					} else {
						rl = new ResourceLocation("textures/mob_effect/" + effect.getRegistryName().getPath() + ".png");
					}
					
					Minecraft.getInstance().getTextureManager().bindTexture(rl);
					float x = startX + 0.3F * j;
					float y = startY + -0.3F * i;
					
					IVertexBuilder vertexBuilder1 = bufferIn.getBuffer(
							ModRenderTypes.getEntityIndicator(rl));
					
					this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder1, x, y, x + 0.3F, y + 0.3F, 0, 0, 256, 256);
					if(!iter.hasNext()) {
						break;
					}
				}
			}
		}
		
		IVertexBuilder vertexBuilder = bufferIn.getBuffer(ModRenderTypes.getEntityIndicator(BATTLE_ICON));
		float ratio = entityIn.getHealth() / entityIn.getMaxHealth();
		float healthRatio = -0.5F + ratio;
		int textureRatio = (int) (62 * ratio);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, healthRatio, 0.05F, 1, 15, textureRatio, 20);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, healthRatio, -0.05F, 0.5F, 0.05F, textureRatio, 10, 62, 15);
		float absorption = entityIn.getAbsorptionAmount();
		
		if(absorption > 0.0D) {
			float absorptionRatio = absorption / entityIn.getMaxHealth();
			int absTexRatio = (int) (62 * absorptionRatio);
			this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, absorptionRatio - 0.5F, 0.05F, 1, 20, absTexRatio, 25);
		}
		
		CapabilityEntity<?> entitycap = entityIn.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entitycap != null && entitycap instanceof LivingData) {
			renderStunShield((LivingData<?>)entitycap, mvMatrix, vertexBuilder);
		}
	}
	
	private void renderStunShield(LivingData<?> entitydataFighter, Matrix4f mvMatrix, IVertexBuilder vertexBuilder) {
		if (entitydataFighter.getStunShield() == 0) {
			return;
		}
		
		float ratio = entitydataFighter.getStunShield() / entitydataFighter.getStunArmor();
		float barRatio = -0.5F + ratio;
		int textureRatio = (int) (62 * ratio);
		
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.1F, barRatio, -0.05F, 1, 5, textureRatio, 10);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, barRatio, -0.1F, 0.5F, -0.05F, textureRatio, 0, 63, 5);
	}
}