package yesman.epicfight.client.gui.screen.overlay;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlendingTextureOverlay extends OverlayManager.Overlay {
	public ResourceLocation texture;
	
	public BlendingTextureOverlay(ResourceLocation texture) {
		this.texture = texture;
	}
	
	@Override
	public void render(int xResolution, int yResolution) {
		//RenderSystem.setShader(GameRenderer::getPositionTexShader);
		//RenderSystem.setShaderTexture(0, this.texture);
		
		Minecraft.getInstance().getTextureManager().bind(this.texture);
		
		GlStateManager._enableBlend();
		GlStateManager._disableDepthTest();
		GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.getInstance();
	    BufferBuilder bufferbuilder = tessellator.getBuilder();
	    bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	    bufferbuilder.vertex(0, 0, 1).uv(0, 0).endVertex();
	    bufferbuilder.vertex(0, yResolution, 1).uv(0, 1).endVertex();
	    bufferbuilder.vertex(xResolution, yResolution, 1).uv(1, 1).endVertex();
	    bufferbuilder.vertex(xResolution, 0, 1).uv(1, 0).endVertex();
	    tessellator.end();
	}
}