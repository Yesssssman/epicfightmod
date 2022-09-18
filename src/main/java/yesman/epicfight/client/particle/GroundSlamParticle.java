package yesman.epicfight.client.particle;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.MetaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class GroundSlamParticle extends MetaParticle {
	protected GroundSlamParticle(ClientWorld level, double x, double y, double z, double dx, double dy, double dz) {
		super(level, x, y, z, dx, dy, dz);
		
		BlockPos blockpos = new BlockPos(x, y, z);
		BlockState blockstate = level.getBlockState(blockpos.below());
		Minecraft mc = Minecraft.getInstance();
		
		for (int i = 0; i < 100; i ++) {
			OpenMatrix4f mat = OpenMatrix4f.createRotatorDeg((float)Math.random() * 360.0F, Vec3f.Y_AXIS);
			Vec3f positionVec = OpenMatrix4f.transform3v(mat, Vec3f.Z_AXIS, null).scale((float)dx);
			Vec3f moveVec = OpenMatrix4f.transform3v(mat, Vec3f.Z_AXIS, null).scale((float)dz);
			
			DiggingParticle blockParticle = new DiggingParticle(level, x + positionVec.x, y, z + positionVec.z, 
					(moveVec.x + Math.random()) * 0.3D, Math.random() * 0.5D, (moveVec.z + Math.random()) * 0.3D, blockstate);
			
			blockParticle.init();
			
			blockParticle.setLifetime(60 + (new Random().nextInt(20)));
			
			Particle smokeParticle = mc.particleEngine.createParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x + positionVec.x * 0.5D, y + 1.5D, z + positionVec.z * 0.5D, 
					moveVec.x * 0.1D, Math.random() * 0.05D, moveVec.z * 0.1D);
			
			smokeParticle.scale(3.0F);
			smokeParticle.setAlpha(0.33F);
			mc.particleEngine.add(blockParticle);
			mc.particleEngine.add(smokeParticle);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements IParticleFactory<BasicParticleType> {
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new GroundSlamParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}
}