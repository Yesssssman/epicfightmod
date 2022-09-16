package yesman.epicfight.client.renderer;

import java.util.OptionalDouble;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class EpicFightRenderTypes extends RenderType {
	private EpicFightRenderTypes(String p_173178_, VertexFormat p_173179_, int drawingMode, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
		super(p_173178_, p_173179_, drawingMode, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
	}
	
	private static final Function<ResourceLocation, RenderType> ANIMATED_MODEL = (textureLocation) -> {
		RenderType.State state = RenderType.State.builder()
			.setTextureState(new RenderState.TextureState(textureLocation, false, false))
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setDiffuseLightingState(DIFFUSE_LIGHTING)
			.setAlphaState(DEFAULT_ALPHA)
			.setCullState(NO_CULL)
			.setLightmapState(LIGHTMAP)
			.setOverlayState(OVERLAY)
			.createCompositeState(true);
	    return create(EpicFightMod.MODID + ":animated_model", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES, 256, true, false, state);
	};
	
	private static final Function<ResourceLocation, RenderType> ANIMATED_ITEM_ENTITY_TRANSLUCENT_CULL = (textureLocation) -> {
		RenderType.State state = RenderType.State.builder()
			.setTextureState(new TextureState(textureLocation, false, false))
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setOutputState(ITEM_ENTITY_TARGET)
			.setLightmapState(LIGHTMAP)
			.setOverlayState(OVERLAY)
			.setWriteMaskState(COLOR_DEPTH_WRITE)
		.createCompositeState(true);
		return create(EpicFightMod.MODID + ":animated_item_entity_translucent_cull", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES, 256, true, true, state);
	};
	
	private static final Function<ResourceLocation, RenderType> ANIMATED_ARMOR_CUTOUT_NO_CULL = (p_173206_) -> {
		RenderType.State rendertype$compositestate = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(p_173206_, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setDiffuseLightingState(DIFFUSE_LIGHTING)
				.setOverlayState(OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setAlphaState(DEFAULT_ALPHA)
			.createCompositeState(true);
		return create(EpicFightMod.MODID + ":armor_cutout_no_cull", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES, 256, true, false, rendertype$compositestate);
	};
	
	private static final Function<ResourceLocation, RenderType> ANIMATED_ARMOR_TRANSLUCENT_CUTOUT_NO_CULL = (p_173206_) -> {
		RenderType.State rendertype$compositestate = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(p_173206_, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(true);
		return create(EpicFightMod.MODID + ":armor_cutout_no_cull", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES, 256, true, true, rendertype$compositestate);
	};
	
	private static final RenderType ANIMATED_ARMOR_GLINT = create(EpicFightMod.MODID + ":animated_armor_glint", DefaultVertexFormats.POSITION_TEX, GL11.GL_TRIANGLES, 256, false, false,
		RenderType.State.builder()
			.setTextureState(new TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false))
			.setWriteMaskState(COLOR_WRITE)
			.setCullState(NO_CULL)
			.setDepthTestState(EQUAL_DEPTH_TEST)
			.setTransparencyState(GLINT_TRANSPARENCY)
			.setTexturingState(ENTITY_GLINT_TEXTURING)
			.setLayeringState(VIEW_OFFSET_Z_LAYERING)
		.createCompositeState(false)
	);
	
	private static final Function<ResourceLocation, RenderType> ENTITY_INDICATOR = (textureLocation) -> {
		RenderType.State state = RenderType.State.builder()
			.setTextureState(new RenderState.TextureState(textureLocation, false, false))
			.setTransparencyState(NO_TRANSPARENCY)
			.setLightmapState(NO_LIGHTMAP)
			.setOverlayState(NO_OVERLAY)
			.createCompositeState(true);
		return create(EpicFightMod.MODID + ":entity_indicator", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, true, false, state);
	};
	
	private static final Function<ResourceLocation, RenderType> ENTITY_DECAL_GL_TRIANGLES = (p_173194_) -> {
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(p_173194_, false, false))
				.setDepthTestState(EQUAL_DEPTH_TEST)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false);
		return create(EpicFightMod.MODID + ":entity_decal_triangles", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES, 256, false, false, state);
	};
	
	private static final Function<ResourceLocation, RenderType> DRAGON_EXPLOSION_ALPHA_GL_TRIANGLES = (textureLocation) -> {
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(textureLocation, false, false))
				.setCullState(NO_CULL)
				.createCompositeState(true);
		return create(EpicFightMod.MODID + ":dragon_explosion_triangles", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES, 256, false, false, state);
	};
	
	private static final RenderType DEBUG_COLLIDER = create(EpicFightMod.MODID + ":debug_collider", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINE_STRIP, 256, false, false,
			RenderType.State.builder()
				.setLineState(new RenderState.LineState(OptionalDouble.empty()))
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(NO_TRANSPARENCY)
				.setWriteMaskState(COLOR_DEPTH_WRITE)
				.setCullState(NO_CULL)
				.createCompositeState(false)
	);
	
	private static final RenderType DEBUG_QUADS = create(EpicFightMod.MODID + ":debug_quad", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false,
			RenderType.State.builder()
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(NO_TRANSPARENCY)
				.setWriteMaskState(COLOR_DEPTH_WRITE)
				.setCullState(NO_CULL)
				.createCompositeState(false)
	);
	
	public static RenderType animatedModel(ResourceLocation locationIn) {
		return ANIMATED_MODEL.apply(locationIn);
	}
	
	public static RenderType itemEntityTranslucentCull(ResourceLocation locationIn) {
		return ANIMATED_ITEM_ENTITY_TRANSLUCENT_CULL.apply(locationIn);
	}
	
	public static RenderType animatedArmor(ResourceLocation locationIn, boolean transparent) {
		return transparent ? ANIMATED_ARMOR_TRANSLUCENT_CUTOUT_NO_CULL.apply(locationIn) : ANIMATED_ARMOR_CUTOUT_NO_CULL.apply(locationIn);
	}
	
	public static RenderType enchantedAnimatedArmor() {
		return ANIMATED_ARMOR_GLINT;
	}
	
	public static RenderType entityIndicator(ResourceLocation locationIn) {
		return ENTITY_INDICATOR.apply(locationIn);
	}
	
	public static RenderType dragonExplosionAlphaTriangles(ResourceLocation locationIn) {
		return DRAGON_EXPLOSION_ALPHA_GL_TRIANGLES.apply(locationIn);
	}
	
	public static RenderType entityDecalTriangles(ResourceLocation locationIn) {
		return ENTITY_DECAL_GL_TRIANGLES.apply(locationIn);
	}
	
	public static RenderType debugQuads() {
		return DEBUG_QUADS;
	}
	
	public static RenderType debugCollider() {
		return DEBUG_COLLIDER;
	}
	
	public static RenderType energySwirlTrianlges(ResourceLocation texture, float u, float v) {
		return create(EpicFightMod.MODID + ":energy_swirl_trianlges", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES, 256, false, true, RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(texture, false, false))
				.setTexturingState(new RenderState.OffsetTexturingState(u, v))
				.setTransparencyState(ADDITIVE_TRANSPARENCY)
				.setDiffuseLightingState(DIFFUSE_LIGHTING)
				.setAlphaState(DEFAULT_ALPHA)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
			.createCompositeState(false));
	}
	
	public static RenderType entityTranslucentTriangles(ResourceLocation texture) {
		return create(EpicFightMod.MODID + ":entity_transparent", DefaultVertexFormats.NEW_ENTITY, GL11.GL_TRIANGLES, 256, false, true, RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setWriteMaskState(COLOR_DEPTH_WRITE)
			.createCompositeState(false));
	}
	
	public static IVertexBuilder getArmorVertexBuilder(IRenderTypeBuffer buffer, RenderType renderType, boolean withGlint) {
		return withGlint ? VertexBuilderUtils.create(buffer.getBuffer(enchantedAnimatedArmor()), buffer.getBuffer(renderType)) : buffer.getBuffer(renderType);
	}
}