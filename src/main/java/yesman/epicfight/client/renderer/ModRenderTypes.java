package yesman.epicfight.client.renderer;

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
import yesman.epicfight.main.EpicFightMod;

public class ModRenderTypes extends RenderType {
	private static final RenderType ARMOR_ENTITY_GLINT = makeType(EpicFightMod.MODID + ":armor_entity_glint", DefaultVertexFormats.POSITION_TEX, GL11.GL_TRIANGLES, 256,
			RenderType.State.getBuilder()
					.texture(new RenderState.TextureState(ItemRenderer.RES_ITEM_GLINT, true, false))
					.writeMask(COLOR_WRITE)
					.cull(CULL_DISABLED)
					.depthTest(DEPTH_EQUAL)
					.transparency(GLINT_TRANSPARENCY)
					.texturing(ENTITY_GLINT_TEXTURING)
					.layer(VIEW_OFFSET_Z_LAYERING)
					.build(false)
			);
	
	public ModRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn,
			boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType getAnimatedModel(ResourceLocation locationIn) {
		RenderType.State state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(locationIn, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
				.alpha(DEFAULT_ALPHA)
				.cull(CULL_DISABLED)
				.lightmap(LIGHTMAP_ENABLED)
				.overlay(OVERLAY_ENABLED)
				.build(true);

		return makeType(EpicFightMod.MODID + ":animated_model2", DefaultVertexFormats.ENTITY, GL11.GL_TRIANGLES, 256, true, false, state);
	}
	
	public static RenderType getItemEntityTranslucentCull(ResourceLocation locationIn) {
		RenderType.State rendertype$state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(locationIn, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.target(ITEM_ENTITY_TARGET)
				.diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
				.alpha(DEFAULT_ALPHA)
				.lightmap(LIGHTMAP_ENABLED)
				.overlay(OVERLAY_ENABLED)
				.writeMask(RenderState.COLOR_DEPTH_WRITE)
				.build(true);
		
		return makeType(EpicFightMod.MODID + ":item_entity_translucent_cull", DefaultVertexFormats.ENTITY, GL11.GL_TRIANGLES, 256, true, false, rendertype$state);
	}
	
	public static RenderType getAimHelper() {
		RenderType.State rendertype$state = RenderType.State.getBuilder()
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.diffuseLighting(DIFFUSE_LIGHTING_DISABLED)
				.alpha(DEFAULT_ALPHA)
				.lightmap(LIGHTMAP_DISABLED)
				.overlay(OVERLAY_DISABLED)
				.writeMask(RenderState.COLOR_DEPTH_WRITE)
				.build(true);
		
		return makeType(EpicFightMod.MODID + ":aim_helper", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256, true, false, rendertype$state);
	}
	
	public static RenderType getAnimatedArmorModel(ResourceLocation locationIn) {
		RenderType.State state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(locationIn, false, false))
				.transparency(NO_TRANSPARENCY)
				.diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
				.alpha(DEFAULT_ALPHA)
				.cull(CULL_DISABLED)
				.lightmap(LIGHTMAP_ENABLED)
				.overlay(OVERLAY_ENABLED)
				.layer(VIEW_OFFSET_Z_LAYERING)
				.build(true);
		
		return makeType(EpicFightMod.MODID + ":animated_armor_model", DefaultVertexFormats.ENTITY, GL11.GL_TRIANGLES, 256, true, false, state);
	}
	
	public static RenderType getEntityCutoutNoCull(ResourceLocation locationIn) {
		RenderType.State state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(locationIn, false, false))
				.transparency(NO_TRANSPARENCY)
				.diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
				.alpha(DEFAULT_ALPHA)
				.cull(CULL_DISABLED)
				.lightmap(LIGHTMAP_ENABLED)
				.overlay(OVERLAY_ENABLED)
				.build(true);
		
		return makeType(EpicFightMod.MODID + ":entity_cutout_no_cull", DefaultVertexFormats.ENTITY, GL11.GL_TRIANGLES, 256, true, false, state);
	}
	
	public static RenderType getEntityIndicator(ResourceLocation locationIn) {
		RenderType.State state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(locationIn, false, false))
				.transparency(NO_TRANSPARENCY)
				.alpha(DEFAULT_ALPHA)
				.build(false);
		
		return makeType(EpicFightMod.MODID + ":entity_indicator", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, false, false, state);
	}
	
	public static RenderType getBox() {
		RenderType.State state = RenderType.State.getBuilder()
				.transparency(NO_TRANSPARENCY)
				.alpha(DEFAULT_ALPHA)
				.build(false);
		
		return makeType(EpicFightMod.MODID + ":box", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINE_STRIP, 256, false, false, state);
	}
	
	public static RenderType getLine() {
		RenderType.State state = RenderType.State.getBuilder()
				.transparency(NO_TRANSPARENCY)
				.alpha(DEFAULT_ALPHA)
				.build(false);
		
		return makeType(EpicFightMod.MODID + ":line", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256, false, false, state);
	}
	
	public static RenderType getEnchantedArmor() {
		return ARMOR_ENTITY_GLINT;
	}
	
	public static IVertexBuilder getArmorVertexBuilder(IRenderTypeBuffer buffer, RenderType renderType, boolean withGlint) {
		return withGlint ? VertexBuilderUtils.newDelegate(buffer.getBuffer(getEnchantedArmor()), buffer.getBuffer(renderType))
				: buffer.getBuffer(renderType);
	}
}