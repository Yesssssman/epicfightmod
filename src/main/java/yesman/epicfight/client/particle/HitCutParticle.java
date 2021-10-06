package yesman.epicfight.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.MetaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.particle.Particles;

@OnlyIn(Dist.CLIENT)
public class HitCutParticle extends MetaParticle {
	public HitCutParticle(ClientWorld world, double x, double y, double z, double width, double height, double _null) {
		super(world, x, y, z);
		this.posX = x + (this.rand.nextDouble() - 0.5D) * width;
		this.posY = y + (this.rand.nextDouble() + height) * 0.5;
		this.posZ = z + (this.rand.nextDouble() - 0.5D) * width;
		this.world.addParticle(Particles.CUT.get(), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		double d = 0.2F;
		
		for(int i = 0; i < 6; i++) {
			double particleMotionX = this.rand.nextDouble() * d;
			d = d * (this.rand.nextBoolean() ? 1.0D : -1.0D);
			double particleMotionZ = this.rand.nextDouble() * d;
			d = d * (this.rand.nextBoolean() ? 1.0D : -1.0D);
			this.world.addParticle(Particles.BLOOD.get(), this.posX, this.posY, this.posZ, particleMotionX, 0.0D, particleMotionZ);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			HitCutParticle particle = new HitCutParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			return particle;
		}
	}
}