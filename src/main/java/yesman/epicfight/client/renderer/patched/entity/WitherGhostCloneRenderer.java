package yesman.epicfight.client.renderer.patched.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.entity.NoopLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherGhostPatch;
import yesman.epicfight.world.entity.WitherGhostClone;

@OnlyIn(Dist.CLIENT)
public class WitherGhostCloneRenderer extends PatchedEntityRenderer<WitherGhostClone, WitherGhostPatch, NoopLivingEntityRenderer<WitherGhostClone>> {
	@Override
	public void render(WitherGhostClone entityIn, WitherGhostPatch entitypatch, NoopLivingEntityRenderer<WitherGhostClone> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		RenderType renderType = EpicFightRenderTypes.entityTranslucentTriangles(PWitherRenderer.WITHER_INVULNERABLE_LOCATION);
		ClientModel model = entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT);
		Armature armature = model.getArmature();
		float tranparency = Mth.sin((entityIn.tickCount + partialTicks) / 40.0F * 3.1415F) * 0.6F;
		
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
		OpenMatrix4f[] poseMatrices = this.getPoseMatrices(entitypatch, armature, partialTicks);
		VertexConsumer builder = buffer.getBuffer(renderType);
		model.drawAnimatedModel(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, tranparency, OverlayTexture.NO_OVERLAY, poseMatrices);
		
		if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
			for (Layer.Priority priority : Layer.Priority.values()) {
				AnimationPlayer animPlayer = entitypatch.getClientAnimator().getCompositeLayer(priority).animationPlayer;
				float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
				animPlayer.getAnimation().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
			}
		}
		
		poseStack.popPose();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(WitherGhostPatch entitypatch, NoopLivingEntityRenderer<WitherGhostClone> renderer) {
		return PWitherRenderer.WITHER_INVULNERABLE_LOCATION;
	}
}