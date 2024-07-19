package yesman.epicfight.client.renderer.patched.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PCustomEntityRenderer extends PatchedEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityRenderer<LivingEntity>, AnimatedMesh> {
	private final AnimatedMesh mesh;
	
	public PCustomEntityRenderer(AnimatedMesh mesh, EntityRendererProvider.Context context) {
		super(context);
		this.mesh = mesh;
	}
	
	@Override
	public void render(LivingEntity entity, LivingEntityPatch<LivingEntity> entitypatch, EntityRenderer<LivingEntity> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		super.render(entity, entitypatch, renderer, buffer, poseStack, packedLight, partialTicks);
		
		Minecraft mc = Minecraft.getInstance();
		boolean isGlowing = mc.shouldEntityAppearGlowing(entity);
		ResourceLocation textureLocation = renderer.getTextureLocation(entity);
		RenderType renderType = EpicFightRenderTypes.getTriangulated(isGlowing ? RenderType.outline(textureLocation) : RenderType.entityCutoutNoCull(textureLocation));
		Armature armature = entitypatch.getArmature();
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
		OpenMatrix4f[] poseMatrices = this.getPoseMatrices(entitypatch, armature, partialTicks);
		
		if (renderType != null) {
		    AnimatedMesh mesh = this.getMesh(entitypatch);
			PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, this.mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);
			
			if (!MinecraftForge.EVENT_BUS.post(prepareModelEvent)) {
				VertexConsumer builder = buffer.getBuffer(renderType);
				mesh.drawModelWithPose(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, !entity.isInvisibleTo(mc.player) ? 0.15F : 1.0F, this.getOverlayCoord(entity, entitypatch, partialTicks), armature, poseMatrices);
			}
		}
		
		if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
			for (Layer layer : entitypatch.getClientAnimator().getAllLayers()) {
				AnimationPlayer animPlayer = layer.animationPlayer;
				float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
				animPlayer.getAnimation().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
			}
		}
		
		poseStack.popPose();
	}
	
	protected int getOverlayCoord(LivingEntity entity, LivingEntityPatch<LivingEntity> entitypatch, float partialTicks) {
		return OverlayTexture.pack(0, OverlayTexture.v(entity.hurtTime > 5));
	}
	
	@Override
	public AnimatedMesh getMesh(LivingEntityPatch<LivingEntity> entitypatch) {
		return this.mesh;
	}
}
