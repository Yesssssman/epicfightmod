package yesman.epicfight.client.renderer.patched.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.WitherAuraLayer;
import net.minecraft.client.renderer.entity.model.WitherModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.layer.PatchedWitherArmorLayer;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherPatch;

@OnlyIn(Dist.CLIENT)
public class PWitherRenderer extends PatchedLivingEntityRenderer<WitherEntity, WitherPatch, WitherModel<WitherEntity>> {
	public static final ResourceLocation WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
	private static final ResourceLocation WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");
	
	public PWitherRenderer() {
		this.addPatchedLayer(WitherAuraLayer.class, new PatchedWitherArmorLayer());
	}
	
	@Override
	public void render(WitherEntity entityIn, WitherPatch entitypatch, LivingRenderer<WitherEntity, WitherModel<WitherEntity>> renderer, IRenderTypeBuffer buffer, MatrixStack poseStack, int packedLight, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		boolean isVisible = this.isVisible(entityIn, entitypatch);
		boolean isVisibleToPlayer = !isVisible && !entityIn.isInvisibleTo(mc.player);
		boolean isGlowing = mc.shouldEntityAppearGlowing(entityIn);
		RenderType renderType = this.getRenderType(entityIn, entitypatch, renderer, isVisible, isVisibleToPlayer, isGlowing);
		ClientModel model = entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT);
		Armature armature = model.getArmature();
		poseStack.pushPose();
		this.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
		OpenMatrix4f[] poseMatrices = this.getPoseMatrices(entitypatch, armature, partialTicks);
		
		if (renderType != null) {
			int transparencyCount = entitypatch.getTransparency();
			
			if (transparencyCount == 0) {
				if (!entitypatch.isGhost()) {
					IVertexBuilder builder = buffer.getBuffer(renderType);
					model.drawAnimatedModel(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entityIn, entitypatch, partialTicks), poseMatrices);
				}
			} else {
				float transparency = (Math.abs(transparencyCount) + partialTicks) / 41.0F;
				
				if (transparencyCount < 0) {
					transparency = 1.0F - transparency;
				}
				
				renderType = EpicFightRenderTypes.entityTranslucentTriangles(WITHER_LOCATION);
				IVertexBuilder builder1 = buffer.getBuffer(renderType);
				model.drawAnimatedModel(poseStack, builder1, packedLight, 1.0F, 1.0F, 1.0F, transparency, OverlayTexture.NO_OVERLAY, poseMatrices);
				
				renderType = EpicFightRenderTypes.entityTranslucentTriangles(WITHER_INVULNERABLE_LOCATION);
				IVertexBuilder builder2 = buffer.getBuffer(renderType);
				model.drawAnimatedModel(poseStack, builder2, packedLight, 1.0F, 1.0F, 1.0F, MathHelper.sin(transparency * 3.1415F), OverlayTexture.NO_OVERLAY, poseMatrices);
			}
			
			this.renderLayer(renderer, entitypatch, entityIn, poseMatrices, buffer, poseStack, packedLight, partialTicks);
		}
		
		if (renderType != null) {
			if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				for (Layer.Priority priority : Layer.Priority.values()) {
					AnimationPlayer animPlayer = entitypatch.getClientAnimator().getCompositeLayer(priority).animationPlayer;
					float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
					animPlayer.getAnimation().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
				}
			}
		}
		
		poseStack.popPose();
	}
	
	@Override
	protected boolean isVisible(WitherEntity witherboss, WitherPatch witherpatch) {
		return !witherpatch.isGhost() || witherpatch.getTransparency() != 0;
	}
	
	@Override
	public void mulPoseStack(MatrixStack poseStack, Armature armature, WitherEntity witherboss, WitherPatch entitypatch, float partialTicks) {
		super.mulPoseStack(poseStack, armature, witherboss, entitypatch, partialTicks);
        
		float f = 1.0F;
		int i = witherboss.getInvulnerableTicks();
		
		if (i > 0) {
			f -= ((float) i - partialTicks) / 440.0F;
		}

		poseStack.scale(f, f, f);
	}
	
	@Override
	protected void setJointTransforms(WitherPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(1, armature, entitypatch.getHeadMatrix(partialTicks));
		WitherEntity witherBoss = entitypatch.getOriginal();
		
		float leftHeadYRot = witherBoss.yRotOHeads[0] + (witherBoss.yRotHeads[0] - witherBoss.yRotOHeads[0]) * partialTicks;
		float rightHeadYRot = witherBoss.yRotOHeads[1] + (witherBoss.yRotHeads[1] - witherBoss.yRotOHeads[1]) * partialTicks;
		float leftHeadXRot = witherBoss.xRotOHeads[0] + (witherBoss.xRotHeads[0] - witherBoss.xRotOHeads[0]) * partialTicks;
		float rightHeadXRot = witherBoss.xRotOHeads[1] + (witherBoss.xRotHeads[1] - witherBoss.xRotOHeads[1]) * partialTicks;
		
		this.setJointTransform(2, armature, OpenMatrix4f.createRotatorDeg(witherBoss.yBodyRot - rightHeadYRot, Vec3f.Y_AXIS).rotateDeg(-rightHeadXRot, Vec3f.X_AXIS));
		this.setJointTransform(3, armature, OpenMatrix4f.createRotatorDeg(witherBoss.yBodyRot - leftHeadYRot, Vec3f.Y_AXIS).rotateDeg(-leftHeadXRot, Vec3f.X_AXIS));
	}
}