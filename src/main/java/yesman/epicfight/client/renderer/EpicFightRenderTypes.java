package yesman.epicfight.client.renderer;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.datafixers.util.Pair;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.client.renderer.EpicFightVertexFormat.AnimationVertexFormat;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EpicFightRenderTypes extends RenderType {
	private static final Function<VertexFormat, VertexFormat> ANIMATION_VERTEX_FORMATS = Util.memoize((vertexFormat) -> {
		if (vertexFormat instanceof AnimationVertexFormat) {
			return vertexFormat;
		}
		
		ImmutableMap.Builder<String, VertexFormatElement> vertexFormatElements = ImmutableMap.builder();
		
		vertexFormat.getElementMapping().entrySet().stream().filter((entry) -> EpicFightVertexFormat.VERTEX_FORMAT_MAPPING.keySet().contains(entry.getValue()))
															.map((entry) -> Pair.of(EpicFightVertexFormat.toAnimationShaderAttributeName(entry.getKey()), EpicFightVertexFormat.VERTEX_FORMAT_MAPPING.get(entry.getValue())))
															.forEach((pair) -> vertexFormatElements.put(pair.getFirst(), pair.getSecond()));
		
		vertexFormatElements.put("Joints", EpicFightVertexFormat.ELEMENT_JOINTS);
		vertexFormatElements.put("Weights", EpicFightVertexFormat.ELEMENT_WEIGHTS);
		vertexFormatElements.put("Padding", DefaultVertexFormat.ELEMENT_PADDING);
		
		VertexFormat animationVertexFormat = new AnimationVertexFormat(vertexFormatElements.build());
		
		return animationVertexFormat;
	});
	
	public static VertexFormat getAnimationVertexFormat(VertexFormat vertexFormat) {
		if (vertexFormat instanceof AnimationVertexFormat) {
			return vertexFormat;
		}
		
		return ANIMATION_VERTEX_FORMATS.apply(vertexFormat);
	}
	
	public static VertexFormat getAnimationVertexFormat(RenderType renderType) {
		return getAnimationVertexFormat(renderType.format);
	}
	
	public static AnimationShaderInstance getAnimationShader(RenderType renderType) {
		if (renderType instanceof CompositeRenderType compositeRenderType) {
			Optional<Supplier<ShaderInstance>> shaderInstanceOptional = compositeRenderType.state.shaderState.shader;
			
			if (shaderInstanceOptional.isPresent()) {
				return AnimationShaderTransformer.getAnimationShader(shaderInstanceOptional.get().get());
			}
		}
		
		return null;
	}
	
	private static final Function<RenderType, RenderType> TRIANGULATED_RENDER_TYPES = Util.memoize((renderType$1) -> {
		if (renderType$1 instanceof CompositeRenderType compositeRenderType) {
			return new CompositeRenderType(renderType$1.name, renderType$1.format, VertexFormat.Mode.TRIANGLES, renderType$1.bufferSize(), renderType$1.affectsCrumbling(), renderType$1.sortOnUpload, compositeRenderType.state);
		} else {
			return renderType$1;
		}
	});
	
	public static RenderType getTriangulated(RenderType renderType) {
		return TRIANGULATED_RENDER_TYPES.apply(renderType);
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
	
	//Util class
	private EpicFightRenderTypes() {
		super(null, null, null, -1, false, false, null, null);
	}
}