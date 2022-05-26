package yesman.epicfight.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class TargetIndicator extends EntityIndicator {
	@Override
	public boolean shouldDraw(LocalPlayer player, LivingEntity entityIn) {
		if (!EpicFightMod.CLIENT_INGAME_CONFIG.showTargetIndicator.getValue()) {
			return false;
		} else {
			LocalPlayerPatch playerpatch = ClientEngine.instance.getPlayerPatch();
			
			if (playerpatch != null && entityIn != playerpatch.getTarget()) {
				return false;
			} else if (entityIn.isInvisible() || !entityIn.isAlive() || entityIn == player.getVehicle()) {
				return false;
			} else if (entityIn.distanceToSqr(Minecraft.getInstance().getCameraEntity()) >= 400) {
				return false;
			} else if (entityIn instanceof Player) {
				Player playerIn = (Player)entityIn;
				
				if (playerIn.isSpectator()) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public void drawIndicator(LivingEntity entityIn, PoseStack matStackIn, MultiBufferSource bufferIn, float partialTicks) {
		Matrix4f mvMatrix = super.getMVMatrix(matStackIn, entityIn, 0.0F, entityIn.getBbHeight() + 0.45F, 0.0F, true, partialTicks);
		this.drawTexturedModalRect2DPlane(mvMatrix, bufferIn.getBuffer(EpicFightRenderTypes.entityIndicator(BATTLE_ICON)), -0.1F, -0.1F, 0.1F, 0.1F, 65, 2, 91, 36);
	}
}