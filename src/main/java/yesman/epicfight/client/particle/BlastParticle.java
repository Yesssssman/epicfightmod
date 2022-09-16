package yesman.epicfight.client.particle;

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
public class BlastParticle extends HitParticle {
	public BlastParticle(ClientWorld world, double x, double y, double z, IAnimatedSprite animatedSprite) {
		super(world, x, y, z, animatedSprite);
		this.rCol = 1.0F;
	    this.gCol = 1.0F;
	    this.bCol = 1.0F;
	    this.quadSize = 1.5F;
		this.lifetime = 2;
	}
	
	@Override
	public void tick() {
		this.xo = this.x;
	    this.yo = this.y;
	    this.zo = this.z;
	}
	
	@Override
	public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
		if (!this.removed) {
			super.render(buffer, renderInfo, partialTicks);
			
			if (this.age++ >= this.lifetime) {
		       this.remove();
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public Provider(IAnimatedSprite p_i50607_1_) {
	         this.spriteSet = p_i50607_1_;
	    }
	    
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			BlastParticle particle = new BlastParticle(worldIn, x, y, z, spriteSet);
			return particle;
		}
	}
}