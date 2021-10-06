package yesman.epicfight.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BladeRushParticle extends HitParticle {
	public BladeRushParticle(ClientWorld world, double x, double y, double z, IAnimatedSprite animatedSprite) {
		super(world, x, y, z, animatedSprite);
	    this.particleRed = 1.0F;
	    this.particleGreen = 1.0F;
	    this.particleBlue = 1.0F;
	    this.particleScale = 1.0F;
		this.maxAge = 4;
		this.particleScale = 2.0F;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite spriteSet) {
	         this.spriteSet = spriteSet;
	    }
	    
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			BladeRushParticle particle = new BladeRushParticle(worldIn, x, y, z, this.spriteSet);
			return particle;
		}
	}
}