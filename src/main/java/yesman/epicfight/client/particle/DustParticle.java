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
public class DustParticle extends SpriteTexturedParticle {
	public DustParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn);
		this.posX = xCoordIn;
		this.posY = yCoordIn;
		this.posZ = zCoordIn;
	    this.particleRed = 1.0F;
	    this.particleGreen = 1.0F;
	    this.particleBlue = 1.0F;
	    this.particleScale = this.rand.nextFloat() * 0.03F + 0.02F;
		this.maxAge = 2 + this.rand.nextInt(6);
		this.canCollide = false;
		this.particleGravity = 0.98F;
		
		float angle = this.rand.nextFloat() * 360.0F;
		this.particleAngle = angle;
		this.prevParticleAngle = angle;
		this.motionX = xSpeedIn;
		this.motionY = ySpeedIn + (this.rand.nextFloat() * 0.5D);
		this.motionZ = zSpeedIn;
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return ParticleRenderTypes.DISABLE_LIGHTMAP_PARTICLE;
	}
	
	@Override
	public void tick() {
		super.tick();
		this.motionX *= 0.48D;
		this.motionY *= 0.48D;
		this.motionZ *= 0.48D;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		protected IAnimatedSprite sprite;

		public Factory(IAnimatedSprite sprite) {
			this.sprite = sprite;
		}
		
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			DustParticle dustParticle = new DustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			dustParticle.selectSpriteRandomly(this.sprite);
			return dustParticle;
		}
	}
}