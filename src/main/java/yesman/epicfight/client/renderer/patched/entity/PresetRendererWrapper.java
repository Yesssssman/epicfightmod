package yesman.epicfight.client.renderer.patched.entity;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.MeshProvider;
import yesman.epicfight.client.renderer.LayerRenderer;
import yesman.epicfight.client.renderer.patched.layer.LayerUtil;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * This class is for LivingEntity with renderer that doesn't extend LivingEntityRenderer (e.g. Geckolib renderer based entities)
 */
@OnlyIn(Dist.CLIENT)
public class PresetRendererWrapper extends PatchedEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityRenderer<LivingEntity>, AnimatedMesh> implements LayerRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<LivingEntity>> {
	private final PatchedLivingEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<LivingEntity>, LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>, AnimatedMesh> presetRenderer;
	protected final Map<Class<?>, PatchedLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<LivingEntity>, ? extends RenderLayer<LivingEntity, EntityModel<LivingEntity>>>> patchedLayers = Maps.newHashMap();
	protected final List<PatchedLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<LivingEntity>, ? extends RenderLayer<LivingEntity, EntityModel<LivingEntity>>>> customLayers = Lists.newArrayList();
	
	public PresetRendererWrapper(EntityRendererProvider.Context context, EntityType<?> entityType, PatchedLivingEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<LivingEntity>, LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>, AnimatedMesh> renderer) {
		super(null);
		
		this.presetRenderer = renderer;
		
		ResourceLocation type = EntityType.getKey(entityType);
		FileToIdConverter filetoidconverter = FileToIdConverter.json("animated_layers/" + type.getPath());
		List<Pair<ResourceLocation, JsonElement>> layers = Lists.newArrayList();
		
		for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(context.getResourceManager()).entrySet()) {
			Reader reader = null;
			
			try {
				reader = entry.getValue().openAsReader();
				JsonElement jsonelement = GsonHelper.fromJson(new GsonBuilder().create(), reader, JsonElement.class);
				layers.add(Pair.of(entry.getKey(), jsonelement));
			} catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
				EpicFightMod.LOGGER.error("Failed to parse layer file {} for {}", entry.getKey(), type);
				jsonparseexception.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
				}
			}
		}
		
		LayerUtil.addLayer(this, entityType, layers);
	}
	
	@Override
	public void render(LivingEntity entity, LivingEntityPatch<LivingEntity> entitypatch, EntityRenderer<LivingEntity> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		try {
			RenderNameTagEvent renderNameplateEvent = new RenderNameTagEvent(entity, entity.getDisplayName(), renderer, poseStack, buffer, packedLight, partialTicks);
			MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
			
			if (((boolean)shouldShowName.invoke(renderer, entity) || renderNameplateEvent.getResult() == Result.ALLOW) && renderNameplateEvent.getResult() != Result.DENY) {
				renderNameTag.invoke(renderer, entity, renderNameplateEvent.getContent(), poseStack, buffer, packedLight);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addPatchedLayer(Class<?> originalLayerClass, PatchedLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<LivingEntity>, ? extends RenderLayer<LivingEntity, EntityModel<LivingEntity>>> patchedLayer) {
		this.patchedLayers.putIfAbsent(originalLayerClass, patchedLayer);
	}
	
	@Override
	public void addCustomLayer(PatchedLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<LivingEntity>, ? extends RenderLayer<LivingEntity, EntityModel<LivingEntity>>> patchedLayer) {
		this.customLayers.add(patchedLayer);
	}

	@Override
	public MeshProvider<AnimatedMesh> getMeshProvider(LivingEntityPatch<LivingEntity> entitypatch) {
		return this.presetRenderer.getMeshProvider(entitypatch);
	}
}
