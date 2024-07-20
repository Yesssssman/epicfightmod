package yesman.epicfight.client.renderer.patched.entity;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.layer.LayerUtil;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.client.renderer.patched.layer.RenderOriginalModelLayer;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public abstract class PatchedLivingEntityRenderer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, R extends LivingEntityRenderer<E, M>, AM extends AnimatedMesh> extends PatchedEntityRenderer<E, T, R, AM> {
	protected static Method isBodyVisible;
	protected static Method getRenderType;
	
	static {
		isBodyVisible = ObfuscationReflectionHelper.findMethod(LivingEntityRenderer.class, "m_5933_", LivingEntity.class);
		getRenderType = ObfuscationReflectionHelper.findMethod(LivingEntityRenderer.class, "m_7225_", LivingEntity.class, boolean.class, boolean.class, boolean.class);
	}
	
	protected Map<Class<?>, PatchedLayer<E, T, M, ? extends RenderLayer<E, M>>> patchedLayers = Maps.newHashMap();
	protected List<PatchedLayer<E, T, M, ? extends RenderLayer<E, M>>> customLayers = Lists.newArrayList();
	
	public PatchedLivingEntityRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context);
		
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
	
	@SuppressWarnings("unchecked")
	public PatchedLivingEntityRenderer<E, T, M, R, AM> initLayerLast(EntityRendererProvider.Context context, EntityType<?> entityType) {
		List<RenderLayer<E, M>> vanillaLayers = null;
		
		if (entityType == EntityType.PLAYER) {
			if (context.getEntityRenderDispatcher().playerRenderers.get("default") instanceof LivingEntityRenderer livingentityrenderer) {
				vanillaLayers = livingentityrenderer.layers;
			}
		} else {
			if (context.getEntityRenderDispatcher().renderers.get(entityType) instanceof LivingEntityRenderer livingentityrenderer) {
				vanillaLayers = livingentityrenderer.layers;
			}
		}
		
		if (vanillaLayers != null) {
			for (RenderLayer<E, M> layer : vanillaLayers) {
				Class<?> layerClass = layer.getClass();
				
				if (layerClass.isAnonymousClass()) {
					layerClass = layer.getClass().getSuperclass();
				}
				
				if (this.patchedLayers.containsKey(layerClass)) {
					continue;
				}
				
				this.addPatchedLayer(layerClass, new RenderOriginalModelLayer<> ("Root", new Vec3f(0.0F, this.getDefaultLayerHeightCorrection(), 0.0F), new Vec3f(0.0F, 0.0F, 0.0F)));
			}
		}
		
		return this;
	}
	
	@Override
	public void render(E entityIn, T entitypatch, R renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		super.render(entityIn, entitypatch, renderer, buffer, poseStack, packedLight, partialTicks);
		
		Minecraft mc = Minecraft.getInstance();
		boolean isVisible = this.isVisible(renderer, entityIn);
		boolean isVisibleToPlayer = !isVisible && !entityIn.isInvisibleTo(mc.player);
		boolean isGlowing = mc.shouldEntityAppearGlowing(entityIn);
		RenderType renderType = this.getRenderType(entityIn, entitypatch, renderer, isVisible, isVisibleToPlayer, isGlowing);
		Armature armature = entitypatch.getArmature();
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
		OpenMatrix4f[] poseMatrices = this.getPoseMatrices(entitypatch, armature, partialTicks);
		
		if (renderType != null) {
		    this.prepareVanillaModel(entityIn, renderer.getModel(), renderer, partialTicks);
			
			AM mesh = this.getMesh(entitypatch);
			this.prepareModel(mesh, entityIn, entitypatch, renderer);
			
			PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);
			
			if (!MinecraftForge.EVENT_BUS.post(prepareModelEvent)) {
				VertexConsumer builder = buffer.getBuffer(renderType);
				mesh.drawModelWithPose(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F, this.getOverlayCoord(entityIn, entitypatch, partialTicks), armature, poseMatrices);
			}
		}
		
		if (!entityIn.isSpectator()) {
			this.renderLayer(renderer, entitypatch, entityIn, poseMatrices, buffer, poseStack, packedLight, partialTicks);
		}
		
		if (renderType != null) {
			if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				for (Layer layer : entitypatch.getClientAnimator().getAllLayers()) {
					AnimationPlayer animPlayer = layer.animationPlayer;
					float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
					animPlayer.getAnimation().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
				}
			}
		}
		
		poseStack.popPose();
	}
	
	// can't transform the access modifier of getBob method because of overriding
	public float getVanillaRendererBob(E entity, LivingEntityRenderer<E, M> renderer, float partialTicks) {
		return entity.tickCount + partialTicks;
	}
	
	protected void prepareVanillaModel(E entityIn, M model, LivingEntityRenderer<E, M> renderer, float partialTicks) {
		boolean shouldSit = entityIn.isPassenger() && (entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit());
		model.riding = shouldSit;
		model.young = entityIn.isBaby();
		float f = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
		float f1 = Mth.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
		float f2 = f1 - f;
		
		if (shouldSit && entityIn.getVehicle() instanceof LivingEntity livingentity) {
			f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
			f2 = f1 - f;
			float f3 = Mth.wrapDegrees(f2);
			if (f3 < -85.0F) {
				f3 = -85.0F;
			}

			if (f3 >= 85.0F) {
				f3 = 85.0F;
			}

			f = f1 - f3;
			if (f3 * f3 > 2500.0F) {
				f += f3 * 0.2F;
			}

			f2 = f1 - f;
		}

		float f6 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
		
		if (LivingEntityRenderer.isEntityUpsideDown(entityIn)) {
			f6 *= -1.0F;
			f2 *= -1.0F;
		}
		
		float f7 = this.getVanillaRendererBob(entityIn, renderer, partialTicks);
		float f8 = 0.0F;
		float f5 = 0.0F;
		
		if (!shouldSit && entityIn.isAlive()) {
			f8 = entityIn.walkAnimation.speed(partialTicks);
			f5 = entityIn.walkAnimation.position() - entityIn.walkAnimation.speed() * (1.0F - partialTicks);
			if (entityIn.isBaby()) {
				f5 *= 3.0F;
			}

			if (f8 > 1.0F) {
				f8 = 1.0F;
			}
		}
		
		model.prepareMobModel(entityIn, f5, f8, partialTicks);
		model.setupAnim(entityIn, f5, f8, f7, f2, f6);
	}
	
	protected void prepareModel(AM mesh, E entity, T entitypatch, R renderer) {
		mesh.initialize();
	}
	
	protected void renderLayer(LivingEntityRenderer<E, M> renderer, T entitypatch, E entity, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		float f = MathUtils.lerpBetween(entity.yBodyRotO, entity.yBodyRot, partialTicks);
        float f1 = MathUtils.lerpBetween(entity.yHeadRotO, entity.yHeadRot, partialTicks);
        float f2 = f1 - f;
		float f7 = entity.getViewXRot(partialTicks);
		float bob = this.getVanillaRendererBob(entity, renderer, partialTicks);
		
		for (RenderLayer<E, M> layer : renderer.layers) {
			Class<?> layerClass = layer.getClass();
			
			if (layerClass.isAnonymousClass()) {
				layerClass = layerClass.getSuperclass();
			}
			
			if (this.patchedLayers.containsKey(layerClass)) {
				this.patchedLayers.get(layerClass).renderLayer(entity, entitypatch, layer, poseStack, buffer, packedLight, poses, bob, f2, f7, partialTicks);
			}
		}
		
		for (PatchedLayer<E, T, M, ? extends RenderLayer<E, M>> patchedLayer : this.customLayers) {
			patchedLayer.renderLayer(entity, entitypatch, null, poseStack, buffer, packedLight, poses, bob, f2, f7, partialTicks);
		}
	}
	
	public RenderType getRenderType(E entityIn, T entitypatch, LivingEntityRenderer<E, M> renderer, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
		try {
			RenderType renderType = (RenderType)getRenderType.invoke(renderer, entityIn, isVisible, isVisibleToPlayer, isGlowing);
			
			if (renderType != null) {
				renderType = EpicFightRenderTypes.getTriangulated(renderType);
			}
			
			return renderType;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			EpicFightMod.LOGGER.error("Reflection Exception");
			e.printStackTrace();
			return null;
		}
	}
	
	protected boolean isVisible(LivingEntityRenderer<E, M> renderer, E entityIn) {
		try {
			return (boolean) isBodyVisible.invoke(renderer, entityIn);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			EpicFightMod.LOGGER.error("Reflection Exception");
			e.printStackTrace();
			
			return true;
		}
	}
	
	protected int getOverlayCoord(E entity, T entitypatch, float partialTicks) {
		return OverlayTexture.pack(0, OverlayTexture.v(entity.hurtTime > 5));
	}
	
	@Override
	public void mulPoseStack(PoseStack poseStack, Armature armature, E entityIn, T entitypatch, float partialTicks) {
		super.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
        
        if (entityIn.isCrouching()) {
			poseStack.translate(0.0D, 0.15D, 0.0D);
		}
	}
	
	public void addPatchedLayer(Class<?> originalLayerClass, PatchedLayer<E, T, M, ? extends RenderLayer<E, M>> patchedLayer) {
		this.patchedLayers.putIfAbsent(originalLayerClass, patchedLayer);
	}
	
	public void addCustomLayer(PatchedLayer<E, T, M, ? extends RenderLayer<E, M>> patchedLayer) {
		this.customLayers.add(patchedLayer);
	}
	
	protected float getDefaultLayerHeightCorrection() {
		return 1.15F;
	}
}