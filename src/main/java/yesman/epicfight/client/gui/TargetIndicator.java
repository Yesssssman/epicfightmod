package yesman.epicfight.client.gui;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class TargetIndicator extends EntityIndicator {
	@Override
	public boolean shouldDraw(LivingEntity entity, @Nullable LivingEntityPatch<?> entitypatch, LocalPlayerPatch playerpatch) {
		if (!EpicFightMod.CLIENT_CONFIGS.showTargetIndicator.getValue()) {
			return false;
		} else {
			if (playerpatch != null && entity != playerpatch.getTarget()) {
				return false;
			} else if (entity.isInvisible() || !entity.isAlive() || entity == playerpatch.getOriginal()) {
				return false;
			} else if (entity.distanceToSqr(Minecraft.getInstance().getCameraEntity()) >= 400) {
				return false;
			} else if (entity instanceof Player player) {
				return !player.isSpectator();
			}
		}
		
		return true;
	}
	
	@Override
	public void drawIndicator(LivingEntity entity, @Nullable LivingEntityPatch<?> entitypatch, LocalPlayerPatch playerpatch, PoseStack poseStack, MultiBufferSource multiBufferSource, float partialTicks) {
		Matrix4f mvMatrix = super.getMVMatrix(poseStack, entity, 0.0F, entity.getBbHeight() + 0.45F, 0.0F, true, partialTicks);
		
		if (entitypatch == null) {
			this.drawTexturedModalRect2DPlane(mvMatrix, multiBufferSource.getBuffer(EpicFightRenderTypes.entityIndicator(BATTLE_ICON)), -0.1F, -0.1F, 0.1F, 0.1F, 97, 2, 128, 33);
		} else {
			if (entity.tickCount % 2 == 0 && !entitypatch.flashTargetIndicator(playerpatch)) {
				this.drawTexturedModalRect2DPlane(mvMatrix, multiBufferSource.getBuffer(EpicFightRenderTypes.entityIndicator(BATTLE_ICON)), -0.1F, -0.1F, 0.1F, 0.1F, 132, 0, 167, 36);
			} else {
				this.drawTexturedModalRect2DPlane(mvMatrix, multiBufferSource.getBuffer(EpicFightRenderTypes.entityIndicator(BATTLE_ICON)), -0.1F, -0.1F, 0.1F, 0.1F, 97, 2, 128, 33);
			}
		}
	}
}