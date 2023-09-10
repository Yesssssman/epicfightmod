package yesman.epicfight.client.renderer.patched.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
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
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public abstract class PatchedLivingEntityRenderer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, AM extends AnimatedMesh> extends PatchedEntityRenderer<E, T, LivingEntityRenderer<E, M>, AM> {
	protected static Method isBodyVisible;
	protected static Method getRenderType;
	
	static {
		isBodyVisible = ObfuscationReflectionHelper.findMethod(LivingEntityRenderer.class, "m_5933_", LivingEntity.class);
		getRenderType = ObfuscationReflectionHelper.findMethod(LivingEntityRenderer.class, "m_7225_", LivingEntity.class, boolean.class, boolean.class, boolean.class);
	}
	
	private Map<Class<?>, PatchedLayer<E, T, M, ? extends RenderLayer<E, M>, AM>> patchedLayers = Maps.newHashMap();
	
	@Override
	public void render(E entityIn, T entitypatch, LivingEntityRenderer<E, M> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
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
			this.prepareModel(mesh, entityIn, entitypatch);
			
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
		if (shouldSit && entityIn.getVehicle() instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity) entityIn.getVehicle();
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
			f8 = Mth.lerp(partialTicks, entityIn.animationSpeedOld, entityIn.animationSpeed);
			f5 = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - partialTicks);
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
	
	protected void prepareModel(AM mesh, E entity, T entitypatch) {
		mesh.initialize();
	}
	
	protected void renderLayer(LivingEntityRenderer<E, M> renderer, T entitypatch, E entityIn, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLightIn, float partialTicks) {
		List<RenderLayer<E, M>> layers = Lists.newArrayList();
		renderer.layers.forEach(layers::add);
		Iterator<RenderLayer<E, M>> iter = layers.iterator();
		float f = MathUtils.lerpBetween(entityIn.yBodyRotO, entityIn.yBodyRot, partialTicks);
        float f1 = MathUtils.lerpBetween(entityIn.yHeadRotO, entityIn.yHeadRot, partialTicks);
        float f2 = f1 - f;
		float f7 = entityIn.getViewXRot(partialTicks);
		
		while (iter.hasNext()) {
			RenderLayer<E, M> layer = iter.next();
			Class<?> rendererClass = layer.getClass();
			
			if (rendererClass.isAnonymousClass()) {
				rendererClass = rendererClass.getSuperclass();
			}
			
			this.patchedLayers.computeIfPresent(rendererClass, (key, val) -> {
				val.renderLayer(0, entitypatch, entityIn, layer, poseStack, buffer, packedLightIn, poses, f2, f7, partialTicks);
				iter.remove();
				return val;
			});
		}
		
		OpenMatrix4f modelMatrix = new OpenMatrix4f().mulFront(poses[this.getRootJointIndex()]);
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		
		poseStack.pushPose();
		MathUtils.translateStack(poseStack, modelMatrix);
		MathUtils.rotateStack(poseStack, transpose);
		poseStack.translate(0.0D, this.getLayerCorrection(), 0.0D);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		
		layers.forEach((layer) -> {
			layer.render(poseStack, buffer, packedLightIn, entityIn, entityIn.animationPosition, entityIn.animationSpeed, partialTicks, entityIn.tickCount, f2, f7);
		});
		
		poseStack.popPose();
	}
	
	public RenderType getRenderType(E entityIn, T entitypatch, LivingEntityRenderer<E, M> renderer, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
		try {
			RenderType renderType = (RenderType)getRenderType.invoke(renderer, entityIn, isVisible, isVisibleToPlayer, isGlowing);
			
			if (renderType != null) {
				renderType = EpicFightRenderTypes.triangles(renderType);
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
	
	public void addPatchedLayer(Class<?> originalLayerClass, PatchedLayer<E, T, M, ? extends RenderLayer<E, M>, AM> patchedLayer) {
		this.patchedLayers.put(originalLayerClass, patchedLayer);
	}
	
	protected int getRootJointIndex() {
		return 0;
	}
	
	protected double getLayerCorrection() {
		return 1.15D;
	}
}