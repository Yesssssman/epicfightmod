package yesman.epicfight.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.particle.Particles;

@OnlyIn(Dist.CLIENT)
public class HitBluntParticle extends HitParticle {
	public HitBluntParticle(ClientWorld world, double x, double y, double z, double argX, double argY, double argZ, IAnimatedSprite animatedSprite) {
		super(world, x, y, z, animatedSprite);
	    this.particleRed = 1.0F;
	    this.particleGreen = 1.0F;
	    this.particleBlue = 1.0F;
	    this.particleScale = 1.0F;
		this.maxAge = 2;
		double d = 1.0F;
		
		for(int i = 0; i < 7; i++) {
			double particleMotionX = this.rand.nextDouble() * d;
			d = d * (this.rand.nextBoolean() ? 1.0D : -1.0D);
			double particleMotionZ = this.rand.nextDouble() * d;
			d = d * (this.rand.nextBoolean() ? 1.0D : -1.0D);
			this.world.addParticle(Particles.DUST.get(), this.posX, this.posY, this.posZ, particleMotionX, 0.0D, particleMotionZ);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;
		public Factory(IAnimatedSprite spriteSet) {
	         this.spriteSet = spriteSet;
	    }
	    
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			HitBluntParticle particle = new HitBluntParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
			return particle;
		}
	}
}