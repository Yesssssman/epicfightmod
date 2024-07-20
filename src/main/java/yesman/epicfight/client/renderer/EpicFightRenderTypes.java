package yesman.epicfight.client.renderer;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState.CompositeStateBuilder;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EpicFightRenderTypes extends RenderType {
	//Util class
	private EpicFightRenderTypes() {
		super(null, null, null, -1, false, false, null, null);
	}
	
	private static final BiFunction<RenderType, Boolean, RenderType> TRIANGULATED_RENDER_TYPES = Util.memoize((renderType$1, transformShader) -> {
		if (renderType$1 instanceof CompositeRenderType compositeRenderType) {
			Optional<Supplier<ShaderInstance>> shaderInstanceOptional = compositeRenderType.state.shaderState.shader;
			
			if (transformShader && shaderInstanceOptional.isPresent()) {
				CompositeStateBuilder builder = CompositeState.builder();
				builder.setTextureState(compositeRenderType.state.textureState);
				builder.setShaderState(new ShaderStateShard(() -> AnimationShaderTransformer.getAnimationShader(shaderInstanceOptional.get().get().getName())));
				builder.setTransparencyState(compositeRenderType.state.transparencyState);
				builder.setDepthTestState(compositeRenderType.state.depthTestState);
				builder.setCullState(compositeRenderType.state.cullState);
				builder.setLightmapState(compositeRenderType.state.lightmapState);
				builder.setOverlayState(compositeRenderType.state.overlayState);
				builder.setLayeringState(compositeRenderType.state.layeringState);
				builder.setOutputState(compositeRenderType.state.outputState);
				builder.setTexturingState(compositeRenderType.state.texturingState);
				builder.setWriteMaskState(compositeRenderType.state.writeMaskState);
				builder.setLineState(compositeRenderType.state.lineState);
				builder.setColorLogicState(compositeRenderType.state.colorLogicState);
				
				return new CompositeRenderType(renderType$1.name, renderType$1.format, VertexFormat.Mode.TRIANGLES, renderType$1.bufferSize(), renderType$1.affectsCrumbling(), renderType$1.sortOnUpload, builder.createCompositeState(compositeRenderType.state.outlineProperty));
			} else {
				return new CompositeRenderType(renderType$1.name, renderType$1.format, VertexFormat.Mode.TRIANGLES, renderType$1.bufferSize(), renderType$1.affectsCrumbling(), renderType$1.sortOnUpload, compositeRenderType.state);
			}
		} else {
			return renderType$1;
		}
	});
	
	public static RenderType getTriangulated(RenderType renderType) {
		return getTriangulated(renderType, true);
	}
	
	public static RenderType getTriangulated(RenderType renderType, boolean transformShader) {
		return TRIANGULATED_RENDER_TYPES.apply(renderType, transformShader);
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