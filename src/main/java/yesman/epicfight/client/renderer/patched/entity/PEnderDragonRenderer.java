package yesman.epicfight.client.renderer.patched.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.LightningRenderHelper;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.DragonCrystalLinkPhase;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.PatchedPhases;

@OnlyIn(Dist.CLIENT)
public class PEnderDragonRenderer extends PatchedEntityRenderer<EnderDragon, EnderDragonPatch, EnderDragonRenderer> {
	private static final ResourceLocation DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");
	private static final ResourceLocation DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
	
	@Override
	public void render(EnderDragon entityIn, EnderDragonPatch entitypatch, EnderDragonRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		ClientModel model = entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT);
		Armature armature = model.getArmature();
		poseStack.pushPose();
        this.mulPoseStack(poseStack, armature, entityIn, entitypatch, partialTicks);
		OpenMatrix4f[] poses = this.getPoseMatrices(entitypatch, armature, partialTicks);
		
		if (entityIn.dragonDeathTime > 0) {
			poseStack.translate(entityIn.getRandom().nextGaussian() * 0.08D, 0.0D, entityIn.getRandom().nextGaussian() * 0.08D);
			float deathTimeProgression = ((float) entityIn.dragonDeathTime + partialTicks) / 200.0F;
			
			VertexConsumer builder = buffer.getBuffer(EpicFightRenderTypes.dragonExplosionAlphaTriangles(DRAGON_EXPLODING_LOCATION));
			model.drawAnimatedModel(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, deathTimeProgression, OverlayTexture.NO_OVERLAY, poses);
			VertexConsumer builder2 = buffer.getBuffer(EpicFightRenderTypes.entityDecalTriangles(DRAGON_LOCATION));
			model.drawAnimatedModel(poseStack, builder2, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entityIn, entitypatch, partialTicks), poses);
		} else {
			VertexConsumer builder = buffer.getBuffer(EpicFightRenderTypes.animatedModel(DRAGON_LOCATION));
			model.drawAnimatedModel(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entityIn, entitypatch, partialTicks), poses);
		}
		
		if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
			for (Layer.Priority priority : Layer.Priority.values()) {
				AnimationPlayer animPlayer = entitypatch.getClientAnimator().getCompositeLayer(priority).animationPlayer;
				float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
				animPlayer.getAnimation().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
			}
		}
		
		poseStack.popPose();
		
		if (entityIn.nearestCrystal != null) {
			float x = (float)(entityIn.nearestCrystal.getX() - Mth.lerp(partialTicks, entityIn.xo, entityIn.getX()));
	        float y = (float)(entityIn.nearestCrystal.getY() - Mth.lerp(partialTicks, entityIn.yo, entityIn.getY()));
	        float z = (float)(entityIn.nearestCrystal.getZ() - Mth.lerp(partialTicks, entityIn.zo, entityIn.getZ()));
	        poseStack.pushPose();
			EnderDragonRenderer.renderCrystalBeams(x, y + EndCrystalRenderer.getY(entityIn.nearestCrystal, partialTicks), z, partialTicks, entityIn.tickCount, poseStack, buffer, packedLight);
			poseStack.popPose();
		}
		
		if (entityIn.dragonDeathTime > 0) {
			float deathTimeProgression = ((float) entityIn.dragonDeathTime + partialTicks) / 200.0F;
			VertexConsumer lightningBuffer = buffer.getBuffer(RenderType.lightning());
			int density = (int)((deathTimeProgression + deathTimeProgression * deathTimeProgression) / 2.0F * 60.0F);
			float f7 = Math.min(deathTimeProgression > 0.8F ? (deathTimeProgression - 0.8F) / 0.2F : 0.0F, 1.0F);
			
			poseStack.pushPose();
			LightningRenderHelper.renderCyclingLight(lightningBuffer, poseStack, 255, 0, 255, density, 1.0F, deathTimeProgression, f7);
			poseStack.popPose();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void mulPoseStack(PoseStack matStack, Armature armature, EnderDragon entityIn, EnderDragonPatch entitypatch, float partialTicks) {
		OpenMatrix4f modelMatrix;
		
		if (!entitypatch.isGroundPhase() || entitypatch.getOriginal().dragonDeathTime > 0) {
			float f = (float)entityIn.getLatencyPos(7, partialTicks)[0];
		    float f1 = (float)(entityIn.getLatencyPos(5, partialTicks)[1] - entityIn.getLatencyPos(10, partialTicks)[1]);
		    float f2 = entitypatch.getOriginal().dragonDeathTime > 0 ? 0.0F : (float)Mth.rotWrap((entityIn.getLatencyPos(5, partialTicks)[0] - entityIn.getLatencyPos(10, partialTicks)[0]));
			modelMatrix = MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, f1, f1, f, f, partialTicks, 1.0F, 1.0F, 1.0F).rotateDeg(-f2 * 1.5F, Vec3f.Z_AXIS);
		} else {
			modelMatrix = entitypatch.getModelMatrix(partialTicks).scale(-1.0F, 1.0F, -1.0F);
		}
		
        OpenMatrix4f transpose = new OpenMatrix4f(modelMatrix).transpose();
        MathUtils.translateStack(matStack, modelMatrix);
        MathUtils.rotateStack(matStack, transpose);
        MathUtils.scaleStack(matStack, transpose);
	}
	
	protected int getOverlayCoord(EnderDragon entity, EnderDragonPatch entitypatch, float partialTicks) {
		DragonPhaseInstance currentPhase = entity.getPhaseManager().getCurrentPhase();
		float chargingTick = DragonCrystalLinkPhase.CHARGING_TICK;
		float progression = currentPhase.getPhase() == PatchedPhases.CRYSTAL_LINK ? (chargingTick - (float)((DragonCrystalLinkPhase)currentPhase).getChargingCount()) / chargingTick : 0.0F;
		
		return OverlayTexture.pack(OverlayTexture.u(progression), OverlayTexture.v(entity.hurtTime > 5 || entity.deathTime > 0));
	}
}