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
	    this.rCol = 1.0F;
	    this.gCol = 1.0F;
	    this.bCol = 1.0F;
	    this.quadSize = 1.0F;
		this.lifetime = 4;
		
		Random rand = new Random();
		float angle = (float)Math.toRadians(rand.nextFloat() * 90.0F);
		this.oRoll = angle;
		this.roll = angle;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public Provider(IAnimatedSprite spriteSet) {
	         this.spriteSet = spriteSet;
	    }
	    
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			if (EpicFightMod.CLIENT_INGAME_CONFIG.offBloodEffects.getValue()) {
				return null;
			}
			CutParticle particle = new CutParticle(worldIn, x, y, z, spriteSet);
			return particle;
		}
	}
}