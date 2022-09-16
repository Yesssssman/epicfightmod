package yesman.epicfight.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.MetaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.particle.EpicFightParticles;

@OnlyIn(Dist.CLIENT)
public class DustParticle extends SpriteTexturedParticle {
	private DustParticle.PhysicsType physicsType;
	
	public DustParticle(ClientWorld level, double x, double y, double z, double xd, double yd, double zd, DustParticle.PhysicsType physicsType) {
		super(level, x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
	    this.rCol = 1.0F;
	    this.gCol = 1.0F;
	    this.bCol = 1.0F;
	    this.quadSize = (physicsType == DustParticle.PhysicsType.NORMAL ? this.random.nextFloat() * 0.01F + 0.01F : this.random.nextFloat() * 0.02F + 0.02F);
		this.lifetime = (physicsType == DustParticle.PhysicsType.NORMAL ? 12 : 2) + this.random.nextInt(6);
		this.hasPhysics = physicsType == DustParticle.PhysicsType.NORMAL ? true : false;
		this.gravity = physicsType == DustParticle.PhysicsType.NORMAL ? 0.68F : 0.0F;
		float angle = this.random.nextFloat() * 360.0F;
		this.roll = angle;
		this.oRoll = angle;
		Vector3d deltaMovement = physicsType.function.getDeltaMovement(xd, yd, zd);
		this.xd = deltaMovement.x;
		this.yd = deltaMovement.y;
		this.zd = deltaMovement.z;
		this.physicsType = physicsType;
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return EpicFightParticleRenderTypes.BLEND_LIGHTMAP_PARTICLE;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (this.physicsType == DustParticle.PhysicsType.EXPANSIVE) {
			this.xd *= 0.48D;
			this.yd *= 0.48D;
			this.zd *= 0.48D;
		} else if (this.physicsType == DustParticle.PhysicsType.CONTRACTIVE) {
			this.xd *= 1.35D;
			this.yd *= 1.35D;
			this.zd *= 1.35D;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ExpansiveMetaParticle extends MetaParticle {
		public ExpansiveMetaParticle(ClientWorld level, double x, double y, double z, double radius, int density) {
			super(level, x, y, z);
			
			for (int vx = -1; vx <= 1; vx+=2) {
				for (int vz = -1; vz <= 1; vz += 2) {
					for (int i = 0; i < density; i++) {
						Vector3d rand = new Vector3d(Math.random() * vx, Math.random(), Math.random() * vz).normalize().scale(radius);
						level.addParticle(EpicFightParticles.DUST_EXPANSIVE.get(), x, y, z, rand.x, rand.y, rand.z);
					}
				}
			}
		}
		
		@OnlyIn(Dist.CLIENT)
		public static class Provider implements IParticleFactory<BasicParticleType> {
			@Override
			public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
				ExpansiveMetaParticle particle = new ExpansiveMetaParticle(worldIn, x, y, z, xSpeed, (int)Double.doubleToLongBits(ySpeed));
				return particle;
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ContractiveMetaParticle extends MetaParticle {
		private double radius;
		private int density;
		
		public ContractiveMetaParticle(ClientWorld level, double x, double y, double z, double radius, int lifetime, int density) {
			super(level, x, y, z);
			this.radius = radius;
			this.lifetime = lifetime;
			this.density = density;
		}
		
		@Override
		public void tick() {
			super.tick();
			
			for (int x = -1; x <= 1; x+=2) {
				for (int y = -1; y <= 1; y +=2) {
					for (int z = -1; z <= 1; z += 2) {
						for (int i = 0; i < this.density; i++) {
							Vector3d rand = new Vector3d(Math.random() * x, Math.random() * y, Math.random() * z).normalize().scale(this.radius);
							this.level.addParticle(EpicFightParticles.DUST_CONTRACTIVE.get(), this.x + rand.x, this.y + rand.y, this.z + rand.z, -rand.x, -rand.y, -rand.z);
						}
					}
				}
			}
		}
		
		@OnlyIn(Dist.CLIENT)
		public static class Provider implements IParticleFactory<BasicParticleType> {
			@Override
			public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
				ContractiveMetaParticle particle = new ContractiveMetaParticle(worldIn, x, y, z, xSpeed, (int)Double.doubleToLongBits(ySpeed), (int)Double.doubleToLongBits(zSpeed));
				return particle;
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ExpansiveDustProvider implements IParticleFactory<BasicParticleType> {
		protected IAnimatedSprite sprite;
		
		public ExpansiveDustProvider(IAnimatedSprite sprite) {
			this.sprite = sprite;
		}
		
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			DustParticle dustParticle = new DustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, DustParticle.PhysicsType.EXPANSIVE);
			dustParticle.pickSprite(this.sprite);
			return dustParticle;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ContractiveDustProvider implements IParticleFactory<BasicParticleType> {
		protected IAnimatedSprite sprite;
		
		public ContractiveDustProvider(IAnimatedSprite sprite) {
			this.sprite = sprite;
		}
		
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			DustParticle dustParticle = new DustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, DustParticle.PhysicsType.CONTRACTIVE);
			dustParticle.pickSprite(this.sprite);
			return dustParticle;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class NormalDustProvider implements IParticleFactory<BasicParticleType> {
		protected IAnimatedSprite sprite;
		
		public NormalDustProvider(IAnimatedSprite sprite) {
			this.sprite = sprite;
		}
		
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			DustParticle dustParticle = new DustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, DustParticle.PhysicsType.NORMAL);
			dustParticle.pickSprite(this.sprite);
			return dustParticle;
		}
	}
	
	@FunctionalInterface
	interface DeltaMovementFunction {
		Vector3d getDeltaMovement(double x, double y, double z);
	}
	
	private static enum PhysicsType {
		EXPANSIVE((dx, dy, dz) -> {
			return new Vector3d(dx, dy, dz);
		}), CONTRACTIVE((dx, dy, dz) -> {
			return new Vector3d(dx * 0.02D, dy * 0.02D, dz * 0.02D);
		}), NORMAL((dx, dy, dz) -> {
			return new Vector3d(dx, dy, dz);
		});
		
		DeltaMovementFunction function;
		
		PhysicsType (DeltaMovementFunction function) {
			this.function = function;
		}
	}
}