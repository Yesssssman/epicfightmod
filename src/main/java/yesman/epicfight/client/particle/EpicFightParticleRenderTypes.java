package yesman.epicfight.client.particle;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings( {"deprecation"} )
@OnlyIn(Dist.CLIENT)
public class EpicFightParticleRenderTypes {
	public static final IParticleRenderType BLEND_LIGHTMAP_PARTICLE = new IParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager._enableBlend();
			GlStateManager._depthMask(false);
			textureManager.bind(AtlasTexture.LOCATION_PARTICLES);
	        RenderHelper.turnOff();
	        Minecraft minecraft = Minecraft.getInstance();
	        minecraft.gameRenderer.lightTexture().turnOffLightLayer();
	        bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE);
		}
		
		public void end(Tessellator tesselator) {
			tesselator.end();
	    }
		
		@Override
		public String toString() {
			return "HIT_PARTICLE";
		}
	};
	
	public static final IParticleRenderType PARTICLE_MODELED = new IParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.disableCull();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableBlend();
			RenderSystem.depthMask(true);
			bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.NEW_ENTITY);
		}
		
		public void end(Tessellator tesselator) {
			tesselator.end();
		}

		public String toString() {
			return "PARTICLE_ANIMATED_MODEL";
		}
	};
	
	public static final IParticleRenderType LIGHTNING = new IParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.disableCull();
		    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			RenderSystem.colorMask(true, true, true, true);
			RenderSystem.depthMask(false);
			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		}
		
		public void end(Tessellator tesselator) {
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
	
	public static final IParticleRenderType TRANSLUCENT_GLOWING = new IParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			Minecraft mc = Minecraft.getInstance();
			mc.gameRenderer.lightTexture().turnOffLightLayer();
			
			RenderHelper.turnOff();
			RenderSystem.disableBlend();
			RenderSystem.disableCull();
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(true);
	        RenderSystem.disableTexture();
	        
			bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.NEW_ENTITY);
		}
		
		public void end(Tessellator tesselator) {
			tesselator.getBuilder().sortQuads(0.0F, 0.0F, 0.0F);
			tesselator.end();
			
			Minecraft mc = Minecraft.getInstance();
			mc.gameRenderer.lightTexture().turnOnLightLayer();
			
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
	
	public static final IParticleRenderType TRANSLUCENT = new IParticleRenderType() {
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.disableCull();
		    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableDepthTest();
	        RenderSystem.disableTexture();
	        RenderHelper.turnBackOn();
			bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
		}
		
		public void end(Tessellator tesselator) {
			tesselator.getBuilder().sortQuads(0.0F, 0.0F, 0.0F);
			tesselator.end();
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableCull();
			RenderHelper.turnOff();
		}
		
		@Override
		public String toString() {
			return "AFTER_IMAGE";
		}
	};
}