package yesman.epicfight.client.renderer;

import java.util.OptionalDouble;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;

import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class EpicFightRenderTypes extends RenderType {
	private EpicFightRenderTypes(String p_173178_, VertexFormat p_173179_, Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
		super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
	}
	
	private static final Function<ResourceLocation, RenderType> ANIMATED_MODEL = Util.memoize((textureLocation) -> {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
			.createCompositeState(true);
	    return create(EpicFightMod.MODID + ":animated_model", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, false, state);
	});
	
	private static final Function<ResourceLocation, RenderType> ANIMATED_ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize((textureLocation) -> {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
			.setTextureState(new TextureStateShard(textureLocation, false, false))
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setOutputState(ITEM_ENTITY_TARGET)
			.setLightmapState(LIGHTMAP)
			.setOverlayState(OVERLAY)
			.setWriteMaskState(COLOR_DEPTH_WRITE)
		.createCompositeState(true);
		return create(EpicFightMod.MODID + ":animated_item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, true, state);
	});
	
	private static final Function<ResourceLocation, RenderType> ANIMATED_ARMOR_CUTOUT_NO_CULL = Util.memoize((p_173206_) -> {
		RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(p_173206_, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(true);
		return create(EpicFightMod.MODID + ":armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, false, rendertype$compositestate);
	});
	
	private static final Function<ResourceLocation, RenderType> ANIMATED_ARMOR_TRANSPARENT_CUTOUT_NO_CULL = Util.memoize((p_173206_) -> {
		RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(p_173206_, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(true);
		return create(EpicFightMod.MODID + ":armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, true, rendertype$compositestate);
	});
	
	private static final RenderType ANIMATED_ARMOR_GLINT = create(EpicFightMod.MODID + ":animated_armor_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES, 256, false, false,
		RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_ARMOR_GLINT_SHADER)
			.setTextureState(new TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false))
			.setWriteMaskState(COLOR_WRITE)
			.setCullState(NO_CULL)
			.setDepthTestState(EQUAL_DEPTH_TEST)
			.setTransparencyState(GLINT_TRANSPARENCY)
			.setTexturingState(ENTITY_GLINT_TEXTURING)
			.setLayeringState(VIEW_OFFSET_Z_LAYERING)
		.createCompositeState(false)
	);
	
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
	
	private static final Function<ResourceLocation, RenderType> ENTITY_DECAL_TRIANGLES = Util.memoize((p_173194_) -> {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_DECAL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(p_173194_, false, false))
				.setDepthTestState(EQUAL_DEPTH_TEST)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false);
		return create(EpicFightMod.MODID + ":entity_decal_triangles", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, false, state);
	});
	
	private static final Function<ResourceLocation, RenderType> DRAGON_EXPLOSION_ALPHA_TRIANGLES = Util.memoize((textureLocation) -> {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_ALPHA_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, false))
				.setCullState(NO_CULL)
				.createCompositeState(true);
		return create(EpicFightMod.MODID + ":dragon_explosion_triangles", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, false, state);
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
	
	private static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_TRIANGLES = (texture) -> create(EpicFightMod.MODID + ":entity_transparent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
			.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setCullState(NO_CULL)
			.setLightmapState(LIGHTMAP)
			.setOverlayState(OVERLAY)
			.setWriteMaskState(COLOR_DEPTH_WRITE)
		.createCompositeState(false));
	
	
	public static RenderType animatedModel(ResourceLocation locationIn) {
		return ANIMATED_MODEL.apply(locationIn);
	}
	
	public static RenderType itemEntityTranslucentCull(ResourceLocation locationIn) {
		return ANIMATED_ITEM_ENTITY_TRANSLUCENT_CULL.apply(locationIn);
	}
	
	public static RenderType animatedArmor(ResourceLocation locationIn, boolean transparent) {
		return transparent ? ANIMATED_ARMOR_TRANSPARENT_CUTOUT_NO_CULL.apply(locationIn) : ANIMATED_ARMOR_CUTOUT_NO_CULL.apply(locationIn);
	}
	
	public static RenderType enchantedAnimatedArmor() {
		return ANIMATED_ARMOR_GLINT;
	}
	
	public static RenderType entityIndicator(ResourceLocation locationIn) {
		return ENTITY_INDICATOR.apply(locationIn);
	}
	
	public static RenderType dragonExplosionAlphaTriangles(ResourceLocation locationIn) {
		return DRAGON_EXPLOSION_ALPHA_TRIANGLES.apply(locationIn);
	}
	
	public static RenderType entityDecalTriangles(ResourceLocation locationIn) {
		return ENTITY_DECAL_TRIANGLES.apply(locationIn);
	}
	
	public static RenderType debugQuads() {
		return DEBUG_QUADS;
	}
	
	public static RenderType debugCollider() {
		return DEBUG_COLLIDER;
	}
	
	public static RenderType energySwirlTrianlges(ResourceLocation texture, float u, float v) {
		return create(EpicFightMod.MODID + ":energy_swirl_triangles", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
				.setTransparencyState(ADDITIVE_TRANSPARENCY).setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
			.createCompositeState(false));
	}
	
	public static RenderType entityTranslucentTriangles(ResourceLocation texture) {
		return ENTITY_TRANSLUCENT_TRIANGLES.apply(texture);
	}
	
	public static VertexConsumer getArmorVertexBuilder(MultiBufferSource buffer, RenderType renderType, boolean withGlint) {
		return withGlint ? VertexMultiConsumer.create(buffer.getBuffer(enchantedAnimatedArmor()), buffer.getBuffer(renderType)) : buffer.getBuffer(renderType);
	}
}