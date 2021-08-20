package maninhouse.epicfight.client.particle;

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
public class PortalStraightParticle extends SpriteTexturedParticle
{
	public PortalStraightParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
	{
		super(worldIn, xCoordIn, yCoordIn, zCoordIn);
	    this.motionX = xSpeedIn;
	    this.motionY = ySpeedIn;
	    this.motionZ = zSpeedIn;
	    this.posX = xCoordIn;
	    this.posY = yCoordIn;
	    this.posZ = zCoordIn;
	    this.particleScale = 0.1F * (this.rand.nextFloat() * 0.2F + 0.5F);
	    float f = this.rand.nextFloat() * 0.6F + 0.4F;
	    this.particleRed = f * 0.9F;
	    this.particleGreen = f * 0.3F;
	    this.particleBlue = f;
	    this.maxAge = (int)(Math.random() * 10.0D) + 40;
	}

	@Override
	public IParticleRenderType getRenderType()
	{
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType>
	{
		private final IAnimatedSprite spriteSet;

	    public Factory(IAnimatedSprite p_i50607_1_)
	    {
	         this.spriteSet = p_i50607_1_;
	    }

		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
		{
			PortalStraightParticle particle = new PortalStraightParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.selectSpriteRandomly(this.spriteSet);
			return particle;
		}
	}
}