package yesman.epicfight.client.particle;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class ParticleRenderTypes {
	public static final IParticleRenderType DISABLE_LIGHTMAP_PARTICLE = new IParticleRenderType() {
		@SuppressWarnings("deprecation")
		public void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_) {
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.depthMask(false);
	        p_217600_2_.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
	        RenderHelper.disableStandardItemLighting();
	        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
	        p_217600_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		}
		
		public void finishRender(Tessellator p_217599_1_) {
			p_217599_1_.draw();
			Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();
	    }
		
		@Override
		public String toString() {
			return "HIT_PARTICLE";
		}
	};
}
