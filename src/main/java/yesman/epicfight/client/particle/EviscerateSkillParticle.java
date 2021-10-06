package yesman.epicfight.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.MetaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.utils.math.MathUtils;

@OnlyIn(Dist.CLIENT)
public class EviscerateSkillParticle extends MetaParticle {
	protected EviscerateSkillParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		super(world, x, y, z, motionX, motionY, motionZ);
		for(int i = 0; i < 50; i++) {
			Vector3d rot = MathUtils.getVectorForRotation(0, (float)motionY);
			double particleMotionX = rot.x * this.rand.nextFloat() * -0.5F;
			double particleMotionZ = rot.z * this.rand.nextFloat() * -0.5F;
			this.world.addParticle(Particles.BLOOD.get(), this.posX, this.posY, this.posZ, particleMotionX, 0.0D, particleMotionZ);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new EviscerateSkillParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}
}
