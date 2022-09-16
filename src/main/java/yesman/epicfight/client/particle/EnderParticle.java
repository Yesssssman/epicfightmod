package yesman.epicfight.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderParticle extends SpriteTexturedParticle {
	private EnderParticle.Usage usage;
	
	public EnderParticle(ClientWorld worldIn, double x, double y, double z, double xd, double yd, double dz) {
		super(worldIn, x, y, z);
	    this.xd = xd;
	    this.yd = yd;
	    this.zd = dz;
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
	    float f = this.random.nextFloat() * 0.6F + 0.4F;
	    this.rCol = f * 0.9F;
	    this.gCol = f * 0.3F;
	    this.bCol = f;
	    this.lifetime = (int)(Math.random() * 10.0D) + 40;
	    this.hasPhysics = false;
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	
	@Override
	public int getLightColor(float p_107086_) {
		if (this.usage == EnderParticle.Usage.DRAGON_BREATH_FLAME) {
			int i = super.getLightColor(p_107086_);
			int k = i >> 16 & 255;
			return 240 | k << 16;
		} else {
			return super.getLightColor(p_107086_);
		}
	}
	
	public void setUsage(EnderParticle.Usage usage) {
		this.usage = usage;
	}
	
	private enum Usage {
		DRAGON_BREATH_FLAME, ENDERMAN_DEATH;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class EndermanDeathEmitProvider implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public EndermanDeathEmitProvider(IAnimatedSprite p_i50607_1_) {
	         this.spriteSet = p_i50607_1_;
	    }
		
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			EnderParticle particle = new EnderParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.pickSprite(this.spriteSet);
			particle.setUsage(EnderParticle.Usage.ENDERMAN_DEATH);
			return particle;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class BreathFlameProvider implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public BreathFlameProvider(IAnimatedSprite p_i50607_1_) {
	         this.spriteSet = p_i50607_1_;
	    }
		
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			EnderParticle particle = new EnderParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.pickSprite(this.spriteSet);
			particle.quadSize *= 2.0F;
			particle.setUsage(EnderParticle.Usage.DRAGON_BREATH_FLAME);
			return particle;
		}
	}
}