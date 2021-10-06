package yesman.epicfight.animation.types;

import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.model.Model;
import yesman.epicfight.physics.Collider;
import yesman.epicfight.utils.math.Vec3f;

public class MountAttackAnimation extends AttackAnimation {
	public MountAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, String index, String path, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, false, collider, index, path, model);
	}
	
	protected Vec3f getCoordVector(LivingData<?> entitydata) {
		return new Vec3f(0, 0, 0);
	}
}
