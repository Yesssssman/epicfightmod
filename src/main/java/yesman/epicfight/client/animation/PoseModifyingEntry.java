package yesman.epicfight.client.animation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.Joint;
import yesman.epicfight.animation.JointTransform;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class PoseModifyingEntry {
	public static final PoseModifyingFunction POSE_ROOT_MIX = (clientAnimator, result, priority, joint, parentTransform, poses) -> {
		Pose lowestPose = poses.get(Layer.Priority.LOWEST).getSecond();
		Pose currentPose = poses.get(priority).getSecond();
		JointTransform lowestTransform = lowestPose.getJointTransformData().get(joint.getName());
		JointTransform currentTransform = currentPose.getJointTransformData().get(joint.getName());
		result.getJointTransformData().get(joint.getName()).getPosition().y = lowestTransform.getPosition().y;
		OpenMatrix4f lowestMatrix = lowestTransform.toMatrix();
		OpenMatrix4f currentMatrix = currentTransform.toMatrix();
		OpenMatrix4f currentToLowest = OpenMatrix4f.mul(OpenMatrix4f.invert(currentMatrix, null), lowestMatrix, null);
		for (Joint subJoint : joint.getSubJoints()) {
			if (!poses.get(priority).getFirst().isEnabledJoint(clientAnimator.getOwner(), subJoint.getName())) {
				OpenMatrix4f lowestLocalTransform = OpenMatrix4f.mul(joint.getLocalTrasnform(), lowestMatrix, null);
				OpenMatrix4f currentLocalTransform = OpenMatrix4f.mul(joint.getLocalTrasnform(), currentMatrix, null);
				OpenMatrix4f childTransform = OpenMatrix4f.mul(subJoint.getLocalTrasnform(), lowestPose.getTransformByName(subJoint.getName()).toMatrix(), null);
				OpenMatrix4f lowestFinal = OpenMatrix4f.mul(lowestLocalTransform, childTransform, null);
				OpenMatrix4f currentFinal = OpenMatrix4f.mul(currentLocalTransform, childTransform, null);
				Vec3f vec = new Vec3f(0, currentFinal.m31 - lowestFinal.m31, currentFinal.m32 - lowestFinal.m32);
				result.getJointTransformData().get(subJoint.getName()).push("parent_correction", OpenMatrix4f::mul, JointTransform.of(vec));
				result.getJointTransformData().get(subJoint.getName()).push(JointTransform.PARENT, OpenMatrix4f::mul, JointTransform.fromMatrix(currentToLowest));
			}
		}
	};
	
	public static final PoseModifyingFunction NONE = (clientAnimator, result, priority, joint, parentTransform, poses) -> {
		
	};
	
	public static PoseModifyingEntry of(String jointName, PoseModifyingFunction bindingOperation) {
		return new PoseModifyingEntry(jointName, bindingOperation);
	}
	
	public static PoseModifyingEntry compareUtil(String jointName) {
		return new PoseModifyingEntry(jointName, null);
	}
	
	private final String jointName;
	private final PoseModifyingFunction poseModifyingFunction;
	
	private PoseModifyingEntry(String jointName, PoseModifyingFunction poseModifyingFunction) {
		this.jointName = jointName;
		this.poseModifyingFunction = poseModifyingFunction;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof PoseModifyingEntry) {
			return ((PoseModifyingEntry)object).jointName.equals(this.jointName);
		}
		return super.equals(object);
	}
	
	public PoseModifyingFunction getPoseModifyingFunction() {
		return this.poseModifyingFunction;
	}
}