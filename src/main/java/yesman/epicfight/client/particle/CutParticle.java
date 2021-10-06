package yesman.epicfight.client.particle;

import java.util.Random;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class CutParticle extends HitParticle {
	public CutParticle(ClientWorld world, double x, double y, double z, IAnimatedSprite animatedSprite) {
		super(world, x, y, z, animatedSprite);
	    this.particleRed = 1.0F;
	    this.particleGreen = 1.0F;
	    this.particleBlue = 1.0F;
	    this.particleScale = 1.0F;
		this.maxAge = 4;
		
		Random rand = new Random();
		float angle = (float)Math.toRadians(rand.nextFloat() * 90.0F);
		this.prevParticleAngle = angle;
		this.particleAngle = angle;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite spriteSet) {
	         this.spriteSet = spriteSet;
	    }
	    
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			if (EpicFightMod.CLIENT_INGAME_CONFIG.offGoreEffect.getValue()) {
				return null;
			}
			CutParticle particle = new CutParticle(worldIn, x, y, z, spriteSet);
			return particle;
		}
	}
}