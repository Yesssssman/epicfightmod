package yesman.epicfight.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.client.renderer.ModRenderTypes;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class TargetIndicator extends EntityIndicator {
	@Override
	public boolean shouldDraw(LivingEntity entityIn) {
		if (!EpicFightMod.CLIENT_INGAME_CONFIG.showTargetIndicator.getValue()) {
			return false;
		} else {
			ClientPlayerData playerdata = ClientEngine.instance.getPlayerData();
			if (playerdata != null && entityIn != playerdata.getAttackTarget()) {
				return false;
			} else if (entityIn.isInvisible() || !entityIn.isAlive() || entityIn == Minecraft.getInstance().player.getRidingEntity()) {
				return false;
			} else if (entityIn.getDistanceSq(Minecraft.getInstance().getRenderViewEntity()) >= 400) {
				return false;
			} else if (entityIn instanceof PlayerEntity) {
				PlayerEntity playerIn = (PlayerEntity) entityIn;
				if (playerIn.isSpectator()) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public void drawIndicator(LivingEntity entityIn, MatrixStack matStackIn, IRenderTypeBuffer bufferIn, float partialTicks) {
		Matrix4f mvMatrix = super.getMVMatrix(matStackIn, entityIn, 0.0F, entityIn.getHeight() + 0.45F, 0.0F, true, false, partialTicks);
		this.drawTexturedModalRect2DPlane(mvMatrix, bufferIn.getBuffer(ModRenderTypes.getEntityIndicator(BATTLE_ICON)),
				-0.1F, -0.1F, 0.1F, 0.1F, 65, 2, 91, 36);
	}
}