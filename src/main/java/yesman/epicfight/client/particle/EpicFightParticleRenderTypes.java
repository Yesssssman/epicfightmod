package yesman.epicfight.client.particle;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
@SuppressWarnings( {"deprecation"} )
@OnlyIn(Dist.CLIENT)
public class EpicFightParticleRenderTypes {
	public static final ParticleRenderType BLEND_LIGHTMAP_PARTICLE = new ParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderSystem.depthMask(false);
	        RenderSystem.setShader(GameRenderer::getParticleShader);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}
		
		public void end(Tesselator tesselator) {
			tesselator.end();
	    }
		
		@Override
		public String toString() {
			return "HIT_PARTICLE";
		}
	};
	
	public static final ParticleRenderType PARTICLE_MODELED = new ParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.disableCull();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableBlend();
			RenderSystem.depthMask(true);
			RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
			
			Minecraft mc = Minecraft.getInstance();
			mc.gameRenderer.overlayTexture().setupOverlayColor();
			
			bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
		}
		
		public void end(Tesselator tesselator) {
			tesselator.end();
			
			Minecraft mc = Minecraft.getInstance();
			mc.gameRenderer.overlayTexture().teardownOverlayColor();
		}

		public String toString() {
			return "PARTICLE_ANIMATED_MODEL";
		}
	};
	
	public static final ParticleRenderType LIGHTNING = new ParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.disableCull();
		    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			RenderSystem.colorMask(true, true, true, true);
			RenderSystem.depthMask(false);
	        RenderSystem.setShader(GameRenderer::getRendertypeLightningShader);
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		}
		
		public void end(Tesselator tesselator) {
			tesselator.end();
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableCull();
		}

		public String toString() {
			return "LIGHTING";
		}
	};
	
	public static final ParticleRenderType TRANSLUCENT_GLOWING = new ParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.disableCull();
		    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(true);
	        RenderSystem.setShader(GameRenderer::getPositionColorShader);
	        RenderSystem.disableTexture();
			bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
		}
		
		public void end(Tesselator tesselator) {
			tesselator.getBuilder().setQuadSortOrigin(0.0F, 0.0F, 0.0F);
			tesselator.end();
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableCull();
		}
		
		@Override
		public String toString() {
			return "AFTER_IMAGE";
		}
	};
	
	public static final ParticleRenderType TRANSLUCENT = new ParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.disableCull();
		    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableDepthTest();
	        RenderSystem.setShader(GameRenderer::getPositionColorLightmapShader);
	        RenderSystem.disableTexture();
			bufferBuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
		}
		
		public void end(Tesselator tesselator) {
			tesselator.getBuilder().setQuadSortOrigin(0.0F, 0.0F, 0.0F);
			tesselator.end();
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableCull();
		}
		
		@Override
		public String toString() {
			return "AFTER_IMAGE";
		}
	};
}