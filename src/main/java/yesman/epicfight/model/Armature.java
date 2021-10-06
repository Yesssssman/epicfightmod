package yesman.epicfight.model;

import java.util.Map;

import com.google.common.collect.Maps;

import yesman.epicfight.animation.Joint;
import yesman.epicfight.utils.math.OpenMatrix4f;

public class Armature {
	private final Map<String, Joint> jointMap;
	private final Map<Integer, Joint> jointById;
	private final Joint jointHierarcy;
	private final int jointNumber;

	public Armature(int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
		this.jointNumber = jointNumber;
		this.jointHierarcy = rootJoint;
		this.jointMap = jointMap;
		this.jointById = Maps.newHashMap();
		this.jointMap.values().forEach((joint) -> {
			this.jointById.put(joint.getId(), joint);
		});
	}

	public OpenMatrix4f[] getJointTransforms() {
		OpenMatrix4f[] jointMatrices = new OpenMatrix4f[this.jointNumber];
		this.jointToTransformMatrixArray(this.jointHierarcy, jointMatrices);
		return jointMatrices;
	}

	public Joint findJointById(int id) {
		return this.jointById.get(id);
	}

	public Joint findJointByName(String name) {
		return this.jointMap.get(name);
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
			this.jointToTransformMatrixArray(childJoint, jointMatrices);
		}
	}
}