package maninhouse.epicfight.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlastParticle extends HitParticle
{
	public BlastParticle(ClientWorld world, double x, double y, double z, IAnimatedSprite animatedSprite)
	{
		super(world, x, y, z, animatedSprite);
		
		this.particleRed = 1.0F;
	    this.particleGreen = 1.0F;
	    this.particleBlue = 1.0F;
	    this.particleScale = 1.5F;
		this.maxAge = 2;
	}
	
	@Override
	public void tick()
	{
		this.prevPosX = this.posX;
	    this.prevPosY = this.posY;
	    this.prevPosZ = this.posZ;
	}
	
	@Override
	public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks)
	{
		if(!this.isExpired)
		{
			super.renderParticle(buffer, renderInfo, partialTicks);
			
			if (this.age++ >= this.maxAge)
		       this.setExpired();
		}
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
			BlastParticle particle = new BlastParticle(worldIn, x, y, z, spriteSet);
			return particle;
		}
	}
}