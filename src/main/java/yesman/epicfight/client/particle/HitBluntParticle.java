package yesman.epicfight.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.particle.EpicFightParticles;

@OnlyIn(Dist.CLIENT)
public class HitBluntParticle extends HitParticle {
	public HitBluntParticle(ClientWorld world, double x, double y, double z, double argX, double argY, double argZ, IAnimatedSprite animatedSprite) {
		super(world, x, y, z, animatedSprite);
	    this.rCol = 1.0F;
	    this.gCol = 1.0F;
	    this.bCol = 1.0F;
	    this.quadSize = 1.0F;
		this.lifetime = 2;
		double d = 1.0F;
		
		for (int i = 0; i < 7; i++) {
			double particleMotionX = this.random.nextDouble() * d;
			d = d * (this.random.nextBoolean() ? 1.0D : -1.0D);
			double particleMotionZ = this.random.nextDouble() * d;
			d = d * (this.random.nextBoolean() ? 1.0D : -1.0D);
			this.level.addParticle(EpicFightParticles.DUST_EXPANSIVE.get(), this.x, this.y, this.z, particleMotionX, this.random.nextDouble() * 0.5D, particleMotionZ);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;
		public Provider(IAnimatedSprite spriteSet) {
	         this.spriteSet = spriteSet;
	    }
	    
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			HitBluntParticle particle = new HitBluntParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
			return particle;
		}
	}
}