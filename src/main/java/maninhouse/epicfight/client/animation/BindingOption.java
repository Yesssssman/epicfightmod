package maninhouse.epicfight.client.animation;

import java.util.Map;

import maninhouse.epicfight.animation.Joint;
import maninhouse.epicfight.animation.JointTransform;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BindingOption {
	private static Pose getOrMakePose(AnimatorClient clientAnimator, Map<Layer.Priority, Pose> poses, Layer.Priority priority, float partialTicks) {
		if (poses.containsKey(priority)) {
			return poses.get(priority);
		} else {
			Pose pose = clientAnimator.getLayerPose(clientAnimator.getLayer(priority), partialTicks);
			poses.put(priority, pose);
			return pose;
		}
	}
	
	public static final BindingOperation DEFAULT = (clientAnimator, priority, joint, parentTransform, poses, partialTicks) -> {
		Pose pose = getOrMakePose(clientAnimator, poses, priority, partialTicks);
		OpenMatrix4f currentLocalTransform = pose.getTransformByName(joint.getName()).toTransformMatrix();
		OpenMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransform, currentLocalTransform);
		OpenMatrix4f bindTransform = parentTransform.pop();
		
		if (bindTransform != null) {
			OpenMatrix4f.mul(bindTransform, currentLocalTransform, bindTransform);
			OpenMatrix4f bindTransform2 = OpenMatrix4f.mul(parentTransform, currentLocalTransform, null);
			bindTransform.m31 = bindTransform2.m31;
			bindTransform.m32 = bindTransform2.m32;
		} else {
			bindTransform = OpenMatrix4f.mul(parentTransform, currentLocalTransform, null);
			OpenMatrix4f.mul(bindTransform, joint.getAnimatedTransform(), bindTransform);
		}
		joint.setAnimatedTransform(bindTransform);
		for (Joint joints : joint.getSubJoints()) {
			clientAnimator.applyPoseToJoint(joints, bindTransform, poses, partialTicks);
		}
	};
	
	public static final BindingOperation DYNAMIC_TRANSFORM = (clientAnimator, priority, joint, parentTransform, poses, partialTicks) -> {
		Pose pose = getOrMakePose(clientAnimator, poses, priority, partialTicks);
		JointTransform jt = pose.getTransformByName(joint.getName());
		OpenMatrix4f currentLocalTransform = jt.toTransformMatrix();
		OpenMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransform, currentLocalTransform);
		OpenMatrix4f bindTransform = OpenMatrix4f.mul(parentTransform, currentLocalTransform, null);
		OpenMatrix4f.mul(bindTransform, joint.getAnimatedTransform(), bindTransform);
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
		joint.setAnimatedTransform(bindTransform);
		for (Joint joints : joint.getSubJoints()) {
			clientAnimator.applyPoseToJoint(joints, bindTransform, poses, partialTicks);
		}
	};
	
	public static final BindingOperation ROOT_MIX = (clientAnimator, priority, joint, parentTransform, poses, partialTicks) -> {
		Pose basePose = getOrMakePose(clientAnimator, poses, Layer.Priority.LOWEST, partialTicks);
		Pose mixPose = getOrMakePose(clientAnimator, poses, priority, partialTicks);
		OpenMatrix4f currentLocalTransformBase = basePose.getTransformByName(joint.getName()).toTransformMatrix();
		OpenMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformBase, currentLocalTransformBase);
		OpenMatrix4f bindTransformBase = OpenMatrix4f.mul(parentTransform, currentLocalTransformBase, null);
		OpenMatrix4f currentLocalTransformMix = mixPose.getTransformByName(joint.getName()).toTransformMatrix();
		OpenMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformMix, currentLocalTransformMix);
		OpenMatrix4f bindTransformMix = OpenMatrix4f.mul(parentTransform, currentLocalTransformMix, null);
		bindTransformMix.m31 = bindTransformBase.m31;
		joint.setAnimatedTransform(bindTransformMix);
		bindTransformBase.push();
		bindTransformBase.load(bindTransformMix);
		for (Joint subJoint : joint.getSubJoints()) {
			if (clientAnimator.getLayer(priority).animationPlayer.getPlay().isEnabledJoint(subJoint.getName())) {
				clientAnimator.applyPoseToJoint(subJoint, bindTransformMix, poses, partialTicks);
			} else {
				clientAnimator.applyPoseToJoint(subJoint, bindTransformBase, poses, partialTicks);
			}
		}
	};
	
	public static BindingOption of(String jointName, BindingOperation bindingOperation) {
		return new BindingOption(jointName, bindingOperation);
	}
	
	private String jointName;
	private BindingOperation bindingOperation;
	
	private BindingOption(String jointName, BindingOperation bindingOperation) {
		this.jointName = jointName;
		this.bindingOperation = bindingOperation;
	}
	
	public boolean isEqualTo(String jointName) {
		return jointName.equals(this.jointName);
	}
	
	public BindingOperation getOperation() {
		return this.bindingOperation;
	}
}