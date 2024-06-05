package yesman.epicfight.client.renderer.patched.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PCutsomGeoEntityRenderer<E extends LivingEntity & GeoAnimatable> extends PatchedEntityRenderer<E, LivingEntityPatch<E>, GeoEntityRenderer<E>, AnimatedMesh> {
	private final AnimatedMesh mesh;
	
	public PCutsomGeoEntityRenderer(AnimatedMesh mesh) {
		this.mesh = mesh;
	}
	
	@Override
	public void render(E entityIn, LivingEntityPatch<E> entitypatch, GeoEntityRenderer<E> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		super.render(entityIn, entitypatch, renderer, buffer, poseStack, packedLight, partialTicks);
		
		Minecraft mc = Minecraft.getInstance();
		boolean isVisibleToPlayer = !entityIn.isInvisibleTo(mc.player);
		RenderType renderType = renderer.getRenderType(entityIn, renderer.getTextureLocation(entityIn), buffer, partialTicks);
		Armature armature = entitypatch.getArmature();
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
		OpenMatrix4f[] poseMatrices = this.getPoseMatrices(entitypatch, armature, partialTicks);
		
		if (renderType != null) {
			this.mesh.initialize();
			PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, this.mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);
			
			if (!MinecraftForge.EVENT_BUS.post(prepareModelEvent)) {
				VertexConsumer builder = buffer.getBuffer(renderType);
				this.mesh.drawModelWithPose(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F, renderer.getPackedOverlay(entityIn, partialTicks), armature, poseMatrices);
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
	
	protected void renderLayer(GeoEntityRenderer<E> renderer, LivingEntityPatch<E> entitypatch, E entityIn, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLightIn, float partialTicks) {
		List<GeoRenderLayer<E>> layers = new ArrayList<>(renderer.getRenderLayers());
		Iterator<GeoRenderLayer<E>> iter = layers.iterator();
		/**
		float f = MathUtils.lerpBetween(entityIn.yBodyRotO, entityIn.yBodyRot, partialTicks);
        float f1 = MathUtils.lerpBetween(entityIn.yHeadRotO, entityIn.yHeadRot, partialTicks);
        float f2 = f1 - f;
		float f7 = entityIn.getViewXRot(partialTicks);
		float bob = entityIn.tickCount + partialTicks;
		**/
		while (iter.hasNext()) {
			GeoRenderLayer<E> layer = iter.next();
			Class<?> rendererClass = layer.getClass();
			
			if (rendererClass.isAnonymousClass()) {
				rendererClass = rendererClass.getSuperclass();
			}
			/**
			this.patchedLayers.computeIfPresent(rendererClass, (key, val) -> {
				val.renderLayer(0, entitypatch, entityIn, layer, poseStack, buffer, packedLightIn, poses, bob, f2, f7, partialTicks);
				iter.remove();
				return val;
			});
			**/
		}
		
		OpenMatrix4f modelMatrix = new OpenMatrix4f().mulFront(poses[entitypatch.getArmature().getRootJoint().getId()]);
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		
		poseStack.pushPose();
		MathUtils.translateStack(poseStack, modelMatrix);
		MathUtils.rotateStack(poseStack, transpose);
		poseStack.translate(0.0D, this.getLayerCorrection(), 0.0D);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		
		//BakedGeoModel model = renderer.getGeoModel().getBakedModel(renderer.getGeoModel().getModelResource(entityIn));
		
		layers.forEach((layer) -> {
			//layer.render(poseStack, buffer, packedLightIn, entityIn, entityIn.walkAnimation.position(), entityIn.walkAnimation.speed(), partialTicks, bob, f2, f7);
		});
		
		poseStack.popPose();
	}
	
	@Override
	public void mulPoseStack(PoseStack poseStack, Armature armature, E entityIn, LivingEntityPatch<E> entitypatch, float partialTicks) {
		super.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
        
        if (entityIn.isCrouching()) {
			poseStack.translate(0.0D, 0.15D, 0.0D);
		}
	}
	/**
	public void addPatchedLayer(Class<?> originalLayerClass, PatchedLayer<E, T, M, ? extends RenderLayer<E, M>, AM> patchedLayer) {
		this.patchedLayers.put(originalLayerClass, patchedLayer);
	}
	**/
	protected double getLayerCorrection() {
		return 1.15D;
	}
	
	@Override
	public AnimatedMesh getMesh(LivingEntityPatch<E> entitypatch) {
		return this.mesh;
	}
}
