package yesman.epicfight.api.model;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

public class Armature {
	private final Map<Integer, Joint> jointById;
	private final Map<String, Joint> jointByName;
	private final Map<String, Integer> pathIndexMap;
	private final Joint jointHierarcy;
	private final int jointNumber;

	public Armature(int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
		this.jointNumber = jointNumber;
		this.jointHierarcy = rootJoint;
		this.jointByName = jointMap;
		this.jointById = Maps.newHashMap();
		this.pathIndexMap = Maps.newHashMap();
		this.jointByName.values().forEach((joint) -> {
			this.jointById.put(joint.getId(), joint);
		});
	}

	public OpenMatrix4f[] getJointTransforms() {
		OpenMatrix4f[] jointMatrices = new OpenMatrix4f[this.jointNumber];
		this.jointToTransformMatrixArray(this.jointHierarcy, jointMatrices);
		return jointMatrices;
	}

	public Joint searchJointById(int id) {
		return this.jointById.get(id);
	}

	public Joint searchJointByName(String name) {
		return this.jointByName.get(name);
	}
	
	public Collection<Joint> getJoints() {
		return this.jointByName.values();
	}
	
	public int searchPathIndex(String joint) {
		if (this.pathIndexMap.containsKey(joint)) {
			return this.pathIndexMap.get(joint);
		} else {
			String pathIndex = this.jointHierarcy.searchPath(new String(""), joint);
			int pathIndex2Int = 0;
			if (pathIndex == null) {
				throw new IllegalArgumentException("failed to get joint path index for " + joint);
			} else {
				pathIndex2Int = (pathIndex.length() == 0) ? -1 : Integer.parseInt(pathIndex);
				this.pathIndexMap.put(joint, pathIndex2Int);
			}
			return pathIndex2Int;
		}
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
		OpenMatrix4f result = OpenMatrix4f.mul(joint.getAnimatedTransform(), joint.getInversedModelTransform(), null);
		jointMatrices[joint.getId()] = result;
		
		for (Joint childJoint : joint.getSubJoints()) {
			this.jointToTransformMatrixArray(childJoint, jointMatrices);
		}
	}
}