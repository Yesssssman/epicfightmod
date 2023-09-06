package yesman.epicfight.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class BetaWarningMessage extends GuiComponent {
	private final Minecraft minecraft;
	
	public BetaWarningMessage(Minecraft minecraft) {
		this.minecraft = minecraft;
	}
	
	public void drawMessage(PoseStack poseStack) {
		Window sr = this.minecraft.getWindow();
		int width = sr.getGuiScaledWidth();
		
		String l1 = "Hello " + this.minecraft.player.getName().getString() + "!";
		String l2 = "You're using Epic Fight in Beta Test";
		
		Matrix4f proj = RenderSystem.getProjectionMatrix();
		Matrix4f matrix4f = Matrix4f.orthographic(0.0F, (float)((double)sr.getWidth() / sr.getGuiScale()), 0.0F, (float)((double)sr.getHeight() / sr.getGuiScale()), 1000.0F, net.minecraftforge.client.ForgeHooksClient.getGuiFarPlane());
        RenderSystem.setProjectionMatrix(matrix4f);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.setIdentity();
        posestack.translate(0.0D, 0.0D, 1000F - ForgeHooksClient.getGuiFarPlane());
		RenderSystem.applyModelViewMatrix();
		FogRenderer.setupNoFog();
		
		poseStack.pushPose();
		poseStack.setIdentity();
		
		this.minecraft.font.drawShadow(poseStack, l1, ((float)width - this.minecraft.font.width(l1) - 2.0F), 8.0F, 16777215);
		this.minecraft.font.drawShadow(poseStack, l2, ((float)width - this.minecraft.font.width(l2) - 2.0F), 20.0F, 16777215);
		
		poseStack.popPose();
		posestack.popPose();
		
		RenderSystem.setProjectionMatrix(proj);
	}
}