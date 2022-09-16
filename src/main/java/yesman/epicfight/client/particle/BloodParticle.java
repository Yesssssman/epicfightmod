package yesman.epicfight.client.particle;

import java.util.Random;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class BloodParticle extends SpriteTexturedParticle {
	protected BloodParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		super(world, x, y, z, motionX, motionY, motionZ);
		this.x = x + (this.random.nextDouble() - 0.5D) * this.bbWidth;
		this.y = y + (this.random.nextDouble() + this.bbHeight) * 0.5D;
		this.z = z + (this.random.nextDouble() - 0.5D) * this.bbWidth;
		this.xd = motionX;
		this.yd = motionY;
		this.zd = motionZ;
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;
		
		public Provider(IAnimatedSprite spriteSet) {
			this.spriteSet = spriteSet;
		}
		
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			BloodParticle particle = new BloodParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			Random random = new Random();
			particle.move(0, 0.5F, 0);
			float mass = random.nextFloat() + 0.2F;
			particle.lifetime = 10 + (int)(mass * 10.0F);
			particle.gravity = mass * 4.0F;
			particle.yd = (0.9F - mass) * 0.4F;
			particle.setSize(mass, mass);
			particle.pickSprite(this.spriteSet);
			float yellow = EpicFightMod.CLIENT_INGAME_CONFIG.offBloodEffects.getValue() ? MathHelper.clamp(random.nextFloat(), 0.6F, 0.4F) : 0.0F;
			particle.setColor(MathHelper.clamp(random.nextFloat(), 0.6F, 0.4F), yellow, 0.0F);
			return particle;
		}
	}
}