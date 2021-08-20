package maninhouse.epicfight.model;

import java.util.Map;

import maninhouse.epicfight.animation.Joint;
import maninhouse.epicfight.utils.math.OpenMatrix4f;

public class Armature {
	private final Map<Integer, Joint> jointTable;
	private final Joint jointHierarcy;
	private final int jointNumber;

	public Armature(int jointNumber, Joint rootJoint, Map<Integer, Joint> jointTable) {
		this.jointNumber = jointNumber;
		this.jointHierarcy = rootJoint;
		this.jointTable = jointTable;
	}

	public OpenMatrix4f[] getJointTransforms() {
		OpenMatrix4f[] jointMatrices = new OpenMatrix4f[jointNumber];
		jointToTransformMatrixArray(jointHierarcy, jointMatrices);
		return jointMatrices;
	}

	public Joint findJointById(int id) {
		return this.jointTable.get(id);
	}

	public Joint findJointByName(String name) {
		for (int i : this.jointTable.keySet()) {
			if (this.jointTable.get(i).getName().equals(name)) {
				return this.jointTable.get(i);
			}
		}

		return null;
	}

	public void initializeTransform() {
		this.jointHierarcy.initializeAnimationTransform();
	}

	public int getJointNumber() {
		return this.jointNumber;
	}

	public Joint getJointHierarcy() {
		return this.jointHierarcy;
	}

	private void jointToTransformMatrixArray(Joint joint, OpenMatrix4f[] jointMatrices) {
		OpenMatrix4f result = new OpenMatrix4f();
		OpenMatrix4f.mul(joint.getAnimatedTransform(), joint.getInversedModelTransform(), result);
		jointMatrices[joint.getId()] = result;

		for (Joint childJoint : joint.getSubJoints()) {
			jointToTransformMatrixArray(childJoint, jointMatrices);
		}
	}
}