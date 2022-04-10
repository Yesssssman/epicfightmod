package yesman.epicfight.api.animation.types;

import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MountAttackAnimation extends AttackAnimation {
	public MountAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, String index, String path, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, collider, index, path, model);
	}
	
	protected Vec3f getCoordVector(LivingEntityPatch<?> entitypatch) {
		return new Vec3f(0, 0, 0);
	}
}
