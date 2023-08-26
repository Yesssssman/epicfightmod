package yesman.epicfight.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class EntityAfterImageParticle extends CustomModelParticle<AnimatedMesh> {
	private OpenMatrix4f[] poseMatrices;
	private Matrix4f modelMatrix;
	private float alphaO;
	
	public EntityAfterImageParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, AnimatedMesh particleMesh, OpenMatrix4f[] matrices, Matrix4f modelMatrix) {
		super(level, x, y, z, xd, yd, zd, particleMesh);
		this.poseMatrices = matrices;
		this.modelMatrix = modelMatrix;
		this.lifetime = 20;
		this.rCol = 1.0F;
		this.gCol = 1.0F;
		this.bCol = 1.0F;
		this.alphaO = 0.3F;
		this.alpha = 0.3F;
	}
	
	@Override
	public void tick() {
		super.tick();
		this.alphaO = this.alpha;
		this.alpha = (float)(this.lifetime - this.age) / (float)this.lifetime * 0.8F;
	}
	
	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
		PoseStack poseStack = new PoseStack();
		this.setupPoseStack(poseStack, camera, partialTicks);
		poseStack.mulPoseMatrix(this.modelMatrix);
		float alpha = this.alphaO + (this.alpha - this.alphaO) * partialTicks;
		this.particleMesh.drawWithPoseNoTexture(poseStack, vertexConsumer, this.getLightColor(partialTicks), this.rCol, this.gCol, this.bCol, alpha, OverlayTexture.NO_OVERLAY, this.poseMatrices);
	}
	
	@Override
	public ParticleRenderType getRenderType() {
		return EpicFightParticleRenderTypes.TRANSLUCENT;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
			LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
			
			if (entitypatch != null && ClientEngine.getInstance().renderEngine.hasRendererFor(entitypatch.getOriginal())) {
				PatchedEntityRenderer renderer = ClientEngine.getInstance().renderEngine.getEntityRenderer(entitypatch.getOriginal());
				Armature armature = entitypatch.getArmature();
				PoseStack poseStack = new PoseStack();
				OpenMatrix4f[] matrices = renderer.getPoseMatrices(entitypatch, armature, 1.0F);
				renderer.mulPoseStack(poseStack, armature, entitypatch.getOriginal(), entitypatch, 1.0F);
				
				for (int i = 0; i < matrices.length; i++) {
					matrices[i] = OpenMatrix4f.mul(matrices[i], armature.searchJointById(i).getToOrigin(), null);
				}
				
				AnimatedMesh mesh = ClientEngine.getInstance().renderEngine.getEntityRenderer(entitypatch.getOriginal()).getMesh(entitypatch);
				EntityAfterImageParticle particle = new EntityAfterImageParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, mesh, matrices, poseStack.last().pose());
				
				return particle;
			}
			
			return null;
		}
	}
}