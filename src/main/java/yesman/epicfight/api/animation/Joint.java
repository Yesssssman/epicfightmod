package yesman.epicfight.api.animation;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import yesman.epicfight.api.utils.math.OpenMatrix4f;

public class Joint {
	public static final Joint EMPTY = new Joint("empty", -1, new OpenMatrix4f());
	
	private final List<Joint> subJoints = new ArrayList<Joint> ();
	private final int jointId;
	private final String jointName;
	private final OpenMatrix4f localTransform;
	private OpenMatrix4f toOrigin = new OpenMatrix4f();
	private OpenMatrix4f poseTransform = new OpenMatrix4f();
	
	public Joint(String name, int jointID, OpenMatrix4f localTransform) {
		this.jointId = jointID;
		this.jointName = name;
		this.localTransform = localTransform;
	}

	public void addSubJoint(Joint... joints) {
		for (Joint joint : joints) {
			this.subJoints.add(joint);
		}
	}

	public void resetPoseTransforms() {
		this.poseTransform.setIdentity();
		
		for (Joint joint : this.subJoints) {
			joint.resetPoseTransforms();
		}
	}
	
	public List<Joint> getAllJoints() {
		List<Joint> list = Lists.newArrayList();
		this.getAllJoints(list);
		
		return list;
	}
	
	private void getAllJoints(List<Joint> list) {
		list.add(this);
		
		for (Joint joint : this.subJoints) {
			joint.getAllJoints(list);
		}
	}
	
	public void initOriginTransform(OpenMatrix4f parentTransform) {
		OpenMatrix4f modelTransform = OpenMatrix4f.mul(parentTransform, this.localTransform, null);
		OpenMatrix4f.invert(modelTransform, this.toOrigin);
		
		for (Joint joint : this.subJoints) {
			joint.initOriginTransform(modelTransform);
		}
	}
	
	public OpenMatrix4f getLocalTrasnform() {
		return this.localTransform;
	}

	public OpenMatrix4f getPoseTransform() {
		return this.poseTransform;
	}

	public OpenMatrix4f getToOrigin() {
		return this.toOrigin;
	}
	
	public List<Joint> getSubJoints() {
		return this.subJoints;
	}

	public String getName() {
		return this.jointName;
	}

	public int getId() {
		return this.jointId;
	}
	
	public String searchPath(String path, String joint) {
		if (joint.equals(this.getName())) {
			return path;
		} else {
			int i = 1;
			for (Joint subJoint : this.subJoints) {
				String str = subJoint.searchPath(String.valueOf(i) + path, joint);
				i++;
				if (str != null) {
					return str;
				}
			}
			return null;
		}
	}
	
	/**
	public void showInfo() {
		System.out.println("id = " + this.jointId);
		System.out.println("name = " + this.jointName);
		System.out.println("local = " + this.localTransform);
		System.out.print("children = ");
		for (Joint joint : subJoints) {
			System.out.print(joint.jointName + " ");
		}
		System.out.println();
		for (Joint joint : subJoints) {
			joint.showInfo();
		}
	}
	**/
}
