package yesman.epicfight.particle;

import java.util.Random;
import java.util.function.BiFunction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class HitParticleType extends BasicParticleType {
	public static final BiFunction<Entity, Entity, Vector3d> POSITION_MIDDLE_OF_TARGET = (e1, e2) -> {
		EntitySize size = e1.getSize(e1.getPose());
		double x = e1.getPosX();
		double y = e1.getPosY() + size.width * 0.5D;
		double z = e1.getPosZ();
		return new Vector3d(x, y, z);
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> POSITION_RANDOM_IN_TARGET_SIZE = (e1, e2) -> {
		EntitySize size = e1.getSize(e1.getPose());
		Random random = new Random();
		double x = e1.getPosX() + (random.nextDouble() - 0.5D) * size.width;
		double y = e1.getPosY() + (random.nextDouble() + size.height) * 0.5;
		double z = e1.getPosZ() + (random.nextDouble() - 0.5D) * size.width;
		return new Vector3d(x, y, z);
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> POSITION_MIDDLE_OF_EACH_ENTITY = (e1, e2) -> {
		double x = MathHelper.lerp(0.5D, e1.getPosX(), e2.getPosX());
		double y = MathHelper.lerp(0.5D, e1.getPosY() + e1.getEyeHeight() * 0.5D, e2.getPosY() + e2.getEyeHeight() * 0.5D);
		double z = MathHelper.lerp(0.5D, e1.getPosZ(), e2.getPosZ());
		return new Vector3d(x, y, z);
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> ARGUMENT_ZERO = (e1, e2) -> {
		return new Vector3d(0.0D,  0.0D, 0.0D);
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> ARGUMENT_ATTACKER_DIRECTION = (e1, e2) -> {
		return new Vector3d(e2.getPitch(0.5F), e2.getYaw(0.5F), 0.0D);
	};
	
	public BiFunction<Entity, Entity, Vector3d> positionGetter;
	public BiFunction<Entity, Entity, Vector3d> argumentGetter;

	public HitParticleType(boolean p_i50791_1_) {
		this(p_i50791_1_, POSITION_MIDDLE_OF_TARGET, ARGUMENT_ZERO);
	}

	public HitParticleType(boolean p_i50791_1_, BiFunction<Entity, Entity, Vector3d> positionGetter, BiFunction<Entity, Entity, Vector3d> argumentGetter) {
		super(p_i50791_1_);
		this.positionGetter = positionGetter;
		this.argumentGetter = argumentGetter;
	}

	public void spawnParticleWithArgument(ServerWorld world, BiFunction<Entity, Entity, Vector3d> positionGetter, BiFunction<Entity, Entity, Vector3d> argumentGetter, Entity e1, Entity e2) {
		Vector3d position = positionGetter == null ? this.positionGetter.apply(e1, e2) : positionGetter.apply(e1, e2);
		Vector3d arguments = argumentGetter == null ? this.argumentGetter.apply(e1, e2) : argumentGetter.apply(e1, e2);
		
		world.spawnParticle(this, position.x, position.y, position.z, 0, arguments.x, arguments.y, arguments.z, 1.0D);
	}
}