package yesman.epicfight.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class HitParticle extends SpriteTexturedParticle {
	protected final IAnimatedSprite animatedSprite;
	
	public HitParticle(ClientWorld world, double x, double y, double z, IAnimatedSprite animatedSprite) {
		super(world, x, y, z);
	    this.particleRed = 1.0F;
	    this.particleGreen = 1.0F;
	    this.particleBlue = 1.0F;
		this.animatedSprite = animatedSprite;
		this.selectSpriteWithAge(animatedSprite);
	}
	
	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		
		if (this.age++ >= this.maxAge) {
			this.setExpired();
		} else {
			this.selectSpriteWithAge(this.animatedSprite);
		}
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return ParticleRenderTypes.DISABLE_LIGHTMAP_PARTICLE;
	}
	
	@Override
	public int getBrightnessForRender(float partialTick) {
		return 15728880;
	}
}