package yesman.epicfight.particle;

import java.util.Random;
import java.util.function.BiFunction;

import com.mojang.math.Vector3d;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;

public class HitParticleType extends SimpleParticleType {
	public static final BiFunction<Entity, Entity, Vector3d> POSITION_MIDDLE_OF_TARGET = (target, attacker) -> {
		EntityDimensions size = target.getDimensions(target.getPose());
		double x = target.getX();
		double y = target.getY() + size.width * 0.5D;
		double z = target.getZ();
		return new Vector3d(x, y, z);
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> POSITION_RANDOM_WITHIN_BOUNDING_BOX = (target, attacker) -> {
		EntityDimensions size = target.getDimensions(target.getPose());
		Random random = new Random();
		double x = target.getX() + (random.nextDouble() - 0.5D) * size.width;
		double y = target.getY() + (random.nextDouble() + size.height) * 0.5;
		double z = target.getZ() + (random.nextDouble() - 0.5D) * size.width;
		return new Vector3d(x, y, z);
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> POSITION_FRONT_OF_EYE_POSITION = (target, attacker) -> {
		Vec3 eyePosition = target.getEyePosition();
		Vec3 viewVec = target.getLookAngle().scale(2.0D);
		return new Vector3d(eyePosition.x + viewVec.x, eyePosition.y + viewVec.y, eyePosition.z + viewVec.z);
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> ARGUMENT_ZERO = (target, attacker) -> {
		return new Vector3d(0.0D,  0.0D, 0.0D);
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> ARGUMENT_ATTACKER_DIRECTION = (target, attacker) -> {
		return new Vector3d(attacker.getViewXRot(0.5F), attacker.getViewYRot(0.5F), 0.0D);
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

	public void spawnParticleWithArgument(ServerLevel world, BiFunction<Entity, Entity, Vector3d> positionGetter, BiFunction<Entity, Entity, Vector3d> argumentGetter, Entity e1, Entity e2) {
		Vector3d position = positionGetter == null ? this.positionGetter.apply(e1, e2) : positionGetter.apply(e1, e2);
		Vector3d arguments = argumentGetter == null ? this.argumentGetter.apply(e1, e2) : argumentGetter.apply(e1, e2);
		
		world.sendParticles(this, position.x, position.y, position.z, 0, arguments.x, arguments.y, arguments.z, 1.0D);
	}
}