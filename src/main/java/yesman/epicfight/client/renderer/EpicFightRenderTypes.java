package yesman.epicfight.client.renderer;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;

import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class EpicFightRenderTypes extends RenderType {
	
	private static Map<RenderType, RenderType> renderTypeCache = Maps.newHashMap();
	
	public static RenderType triangles(RenderType renderType) {
		return renderTypeCache.computeIfAbsent(renderType, (key) -> {
			RenderType trianglesRenderType = null;
			
			if (renderType instanceof CompositeRenderType) {
				CompositeRenderType compositeRenderType = (CompositeRenderType)renderType;
				trianglesRenderType = new CompositeRenderType(renderType.name, renderType.format, VertexFormat.Mode.TRIANGLES, renderType.bufferSize(), renderType.affectsCrumbling(), renderType.sortOnUpload, compositeRenderType.state);
			}
			
			return trianglesRenderType;
		});
	}
	
	private EpicFightRenderTypes(String name, VertexFormat format, VertexFormat.Mode drawingMode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clean) {
		super(name, format, drawingMode, bufferSize, affectsCrumbling, sortOnUpload, setup, clean);
	}
	
	private static VertexConsumer getTriangleBuffer(MultiBufferSource bufferSource, RenderType renderType) {
		RenderType triangleRenderType = triangles(renderType);
		
		if (bufferSource instanceof MultiBufferSource.BufferSource) {
			MultiBufferSource.BufferSource cast = (MultiBufferSource.BufferSource)bufferSource;
			
			if (cast.fixedBuffers.containsKey(renderType)) {
				cast.fixedBuffers.computeIfAbsent(triangleRenderType, (key) -> new BufferBuilder(renderType.bufferSize()));
				
				return cast.getBuffer(triangleRenderType);
			}
			
			return cast.getBuffer(triangleRenderType);
		}
		
		return bufferSource.getBuffer(triangleRenderType);
	}
	
	public static VertexConsumer getArmorFoilBufferTriangles(MultiBufferSource bufferSource, RenderType renderType, boolean isEntity, boolean hasEffect) {
		return hasEffect ? VertexMultiConsumer.create( getTriangleBuffer(bufferSource, isEntity ? RenderType.armorGlint() : RenderType.armorEntityGlint()),
				getTriangleBuffer(bufferSource, renderType)) : getTriangleBuffer(bufferSource, renderType);
	}
	
	private static final Function<ResourceLocation, RenderType> ENTITY_INDICATOR = Util.memoize((textureLocation) -> {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(POSITION_TEX_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setLightmapState(NO_LIGHTMAP)
				.setOverlayState(NO_OVERLAY)
				.createCompositeState(true);
		return create(EpicFightMod.MODID + ":entity_indicator", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, true, false, state);
	});
	
	private static final RenderType DEBUG_COLLIDER = create(EpicFightMod.MODID + ":debug_collider", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINE_STRIP, 256, false, false,
			RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_SHADER)
				.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setWriteMaskState(COLOR_DEPTH_WRITE)
				.setCullState(NO_CULL)
				.createCompositeState(false)
	);
	
	private static final RenderType DEBUG_QUADS = create(EpicFightMod.MODID + ":debug_quad", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
			RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_SHADER)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(NO_TRANSPARENCY)
				.setWriteMaskState(COLOR_DEPTH_WRITE)
				.setCullState(NO_CULL)
				.createCompositeState(false)
	);
	
	public static RenderType entityIndicator(ResourceLocation locationIn) {
		return ENTITY_INDICATOR.apply(locationIn);
	}
	
	public static RenderType debugCollider() {
		return DEBUG_COLLIDER;
	}
	
	public static RenderType debugQuads() {
		return DEBUG_QUADS;
	}
}