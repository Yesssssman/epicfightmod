package yesman.epicfight.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.DragonCrystalLinkPhase;

@OnlyIn(Dist.CLIENT)
public class ForceFieldParticle extends TexturedCustomModelParticle {
	private LivingEntityPatch<?> caster;
	
	public ForceFieldParticle(ClientWorld level, double x, double y, double z, double xd, double yd, double zd, ClientModel particleMesh, ResourceLocation texture) {
		super(level, x, y, z, xd, yd, zd, particleMesh, texture);
		this.lifetime = DragonCrystalLinkPhase.CHARGING_TICK;
		this.hasPhysics = false;
		this.roll = (float)xd;
		this.pitch = (float)zd;
		
		Entity entity = level.getEntity((int)Double.doubleToLongBits(yd));
		
		if (entity != null) {
			this.caster = (LivingEntityPatch<?>)entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		}
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return EpicFightParticleRenderTypes.PARTICLE_MODELED;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		this.yaw += 36.0F;
		this.scale += (float)(Math.max(30 - this.age, 0)) / 140.0F;
		
		if (this.caster != null && this.caster.getStunShield() <= 0.0F) {
			this.remove();
		}
		
		for (int x = -1; x <= 1; x+=2) {
			for (int z = -1; z <= 1; z += 2) {
				Vector3d rand = new Vector3d(Math.random() * x, Math.random(), Math.random() * z).normalize().scale(10.0D);
				this.level.addParticle(EpicFightParticles.DUST_CONTRACTIVE.get(), this.x + rand.x, this.y + rand.y - 1.0D, this.z + rand.z, -rand.x, -rand.y, -rand.z);
			}
		}
	}
	
	@Override
	protected void setupMatrixStack(MatrixStack poseStack, ActiveRenderInfo camera, float partialTicks) {
		float yaw = MathHelper.lerp(partialTicks, this.yawO, this.yaw);
		Vector3d vec3 = camera.getPosition();
		float x = (float)(MathHelper.lerp((double)partialTicks, this.xo, this.x) - vec3.x());
		float y = (float)(MathHelper.lerp((double)partialTicks, this.yo, this.y) - vec3.y());
		float z = (float)(MathHelper.lerp((double)partialTicks, this.zo, this.z) - vec3.z());
		float scale = (float)MathHelper.lerp((double)partialTicks, this.scaleO, this.scale);
		poseStack.translate(x, y, z);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(this.pitch));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(this.roll));
		poseStack.mulPose(Vector3f.YP.rotationDegrees(yaw));
		poseStack.scale(scale, scale, scale);
	}
	
	@Override
	public int getLightColor(float p_107086_) {
		int i = super.getLightColor(p_107086_);
		int k = i >> 16 & 255;
		return 240 | k << 16;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements IParticleFactory<BasicParticleType> {
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ForceFieldParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, ClientModels.LOGICAL_CLIENT.forceField, EnderDragonRenderer.CRYSTAL_BEAM_LOCATION);
		}
	}
}