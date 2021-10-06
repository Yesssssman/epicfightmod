package yesman.epicfight.animation;

import yesman.epicfight.animation.types.DynamicAnimation;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.utils.math.OpenMatrix4f;

public abstract class Animator {
	protected LivingData<?> entitydata;
	public abstract void playAnimation(int namespaceId, int id, float modifyTime);
	public abstract void playAnimation(StaticAnimation nextAnimation, float modifyTime);
	public abstract void update();
	public abstract void reserveAnimation(StaticAnimation nextAnimation);
	public abstract EntityState getEntityState();
	public abstract AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation);
	public abstract Pose getNextStartingPose(float startAt);
	
	public boolean isReverse() {
		return false;
	}
	
	public void playDeathAnimation() {
		this.playAnimation(Animations.BIPED_DEATH, 0);
	}
	
	public OpenMatrix4f getJointTransformByIndex(Pose pose, Joint joint, OpenMatrix4f parentTransform, int indexer) {
		JointTransform jt = pose.getTransformByName(joint.getName());
		OpenMatrix4f transform = jt.toMatrix();
		OpenMatrix4f.mul(joint.getLocalTrasnform(), transform, transform);
		OpenMatrix4f.mul(parentTransform, transform, transform);
		OpenMatrix4f.mul(transform, joint.getAnimatedTransform(), transform);
		OpenMatrix4f finalMatrix = transform.getResult();
		indexer = indexer >> 5;
		if (indexer == 0) {
			return finalMatrix;
		} else {
			return this.getJointTransformByIndex(pose, joint.getSubJoints().get((indexer & 31) - 1), finalMatrix, indexer);
		}
	}
}