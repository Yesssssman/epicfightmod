package yesman.epicfight.client.gui;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.ModList;

@OnlyIn(Dist.CLIENT)
public class BetaWarningMessage {
	private final Minecraft minecraft;
	
	public BetaWarningMessage(Minecraft minecraft) {
		this.minecraft = minecraft;
	}
	
	public void drawMessage(GuiGraphics guiGraphics) {
		Window sr = this.minecraft.getWindow();
		int width = sr.getGuiScaledWidth();
		
		String l1 = "Hello " + this.minecraft.player.getName().getString() + "!";
		String l2 = "You're using Epic Fight " + ModList.get().getModFileById("epicfight").versionString();
		
		Matrix4f proj = RenderSystem.getProjectionMatrix();
		Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, (float)((double)sr.getWidth() / sr.getGuiScale()), 0.0F, (float)((double)sr.getHeight() / sr.getGuiScale()), 0.1F, 1000.0F);
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.setIdentity();
        posestack.translate(0.0D, 0.0D, 1000F - ForgeHooksClient.getGuiFarPlane());
		RenderSystem.applyModelViewMatrix();
		FogRenderer.setupNoFog();
		
		posestack.pushPose();
		posestack.setIdentity();
		guiGraphics.drawString(this.minecraft.font, l1, (width - this.minecraft.font.width(l1) - 2), 8, 16777215);
		guiGraphics.drawString(this.minecraft.font, l2, (width - this.minecraft.font.width(l2) - 2), 20, 16777215);
		posestack.popPose();
		
		RenderSystem.setProjectionMatrix(proj, VertexSorting.ORTHOGRAPHIC_Z);
	}
}
