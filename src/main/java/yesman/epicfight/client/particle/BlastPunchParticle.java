package yesman.epicfight.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.utils.math.MathUtils;

@OnlyIn(Dist.CLIENT)
public class BlastPunchParticle extends AnimatedMeshParticle {
	protected BlastPunchParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
			double ySpeedIn, double zSpeedIn, IBakedModel mesh) {
		super(worldIn, xCoordIn, yCoordIn+1.5F, zCoordIn, 0, 0, 0, mesh);
		
		Vector3d rot = MathUtils.getVectorForRotation(0, (float)ySpeedIn);
		this.motionX = rot.x;
		this.motionY = 0.0D;
		this.motionZ = rot.z;
		this.rotationY = (float)ySpeedIn;
		this.canCollide = false;
		this.maxAge = 3;
		//this.world.addParticle(Particles.FLASH.get(), this.posX, this.posY, this.posZ, xSpeedIn, ySpeedIn, zSpeedIn);
		
		for (int i = 0; i < 20; i++) {
			this.world.addParticle(Particles.DUST.get(), this.posX, this.posY, this.posZ, xSpeedIn, ySpeedIn, zSpeedIn);
		}
	}
	
	@Override
	public void tick() {
		this.prevPosX = this.posX;
	    this.prevPosY = this.posY;
	    this.prevPosZ = this.posZ;
	    this.prevScale = this.scale;
	    this.scale += 2.0F;
		if (this.age++ >= this.maxAge) {
			this.setExpired();
		} else {
	        this.move(this.motionX, this.motionY, this.motionZ);
	        this.motionX *= (double)0.98F;
	        this.motionY *= (double)0.98F;
	        this.motionZ *= (double)0.98F;
	    }
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return ParticleRenderTypes.DISABLE_LIGHTMAP_PARTICLE;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		protected IBakedModel mesh;
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			//if(mesh == null) mesh = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(
					//ForgeRegistries.PARTICLE_TYPES.getKey(Particles.BLAST_PUNCH.get()), "particle"));
			
			return new BlastPunchParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, mesh);
		}
	}
}