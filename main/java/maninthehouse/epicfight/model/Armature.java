package maninthehouse.epicfight.model;

import java.util.Map;

import maninthehouse.epicfight.animation.Joint;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;

public class Armature {
	private final Map<Integer, Joint> jointTable;
	private final Joint jointHierarcy;
	private final int jointNumber;

	public Armature(int jointNumber, Joint rootJoint, Map<Integer, Joint> jointTable) {
		this.jointNumber = jointNumber;
		this.jointHierarcy = rootJoint;
		this.jointTable = jointTable;
	}

	public VisibleMatrix4f[] getJointTransforms() {
		VisibleMatrix4f[] jointMatrices = new VisibleMatrix4f[jointNumber];
		jointToTransformMatrixArray(jointHierarcy, jointMatrices);
		return jointMatrices;
	}

	public Joint findJointById(int id) {
		return this.jointTable.get(id);
	}

	public Joint findJointByName(String name) {
		for (int i : jointTable.keySet()) {
			if (jointTable.get(i).getName().equals(name)) {
				return jointTable.get(i);
			}
		}

		return null;
	}

	public void initializeTransform() {
		this.jointHierarcy.initializeAnimationTransform();
	}

	public int getJointNumber() {
		return jointNumber;
	}

	public Joint getJointHierarcy() {
		return jointHierarcy;
	}

	private void jointToTransformMatrixArray(Joint joint, VisibleMatrix4f[] jointMatrices) {
		VisibleMatrix4f result = new VisibleMatrix4f();
		VisibleMatrix4f.mul(joint.getAnimatedTransform(), joint.getInversedModelTransform(), result);
		jointMatrices[joint.getId()] = result;

		for (Joint childJoint : joint.getSubJoints()) {
			jointToTransformMatrixArray(childJoint, jointMatrices);
		}
	}
}