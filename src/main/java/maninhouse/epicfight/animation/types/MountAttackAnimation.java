package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.utils.math.Vec3f;

public class MountAttackAnimation extends AttackAnimation
{
	public MountAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, String index, String path)
	{
		super(id, convertTime, antic, preDelay, contact, recovery, false, collider, index, path);
	}
	
	protected Vec3f getCoordVector(LivingData<?> entitydata)
	{
		return new Vec3f(0, 0, 0);
	}
}
