package maninhouse.epicfight.animation;

import maninhouse.epicfight.animation.types.DynamicAnimation;
import maninhouse.epicfight.animation.types.EntityState;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.utils.math.OpenMatrix4f;

public abstract class Animator {
	protected LivingData<?> entitydata;
	
	public abstract void playAnimation(int id, float modifyTime);
	public abstract void playAnimation(StaticAnimation nextAnimation, float modifyTime);
	public abstract void update();
	public abstract void reserveAnimation(StaticAnimation nextAnimation);
	public abstract EntityState getEntityState();
	public abstract AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation);
	
	public boolean isReverse() {
		return false;
	}
	
	public void playDeathAnimation() {
		this.playAnimation(Animations.BIPED_DEATH, 0);
	}
	
	public OpenMatrix4f getJointTransformByIndex(Pose pose, Joint joint, OpenMatrix4f parentTransform, int indexer) {
		JointTransform jt = pose.getTransformByName(joint.getName());
		OpenMatrix4f currentLocalTransform = jt.toTransformMatrix();
		OpenMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransform, currentLocalTransform);
		OpenMatrix4f bindTransform = OpenMatrix4f.mul(parentTransform, currentLocalTransform, null);
		OpenMatrix4f.mul(bindTransform, joint.getAnimatedTransform(), bindTransform);
		indexer = indexer >> 5;
		
		if (jt.getDynamicRotation() != null) {
			float x = bindTransform.m30;
			float y = bindTransform.m31;
			float z = bindTransform.m32;
			bindTransform.m30 = 0;
			bindTransform.m31 = 0;
			bindTransform.m32 = 0;
			OpenMatrix4f.mul(jt.getDynamicRotation().toRotationMatrix(), bindTransform, bindTransform);
			bindTransform.m30 = x;
			bindTransform.m31 = y;
			bindTransform.m32 = z;
		}
		
		if(indexer == 0) {
			return bindTransform;
		} else {
			return this.getJointTransformByIndex(pose, joint.getSubJoints().get((indexer & 31) - 1), bindTransform, indexer);
		}
	}
}