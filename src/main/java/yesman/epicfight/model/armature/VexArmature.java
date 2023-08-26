package yesman.epicfight.model.armature;

import java.util.Map;

import yesman.epicfight.api.animation.Joint;

public class VexArmature extends HumanoidArmature {
	public final Joint wingL;
	public final Joint wingR;
	
	public VexArmature(int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
		super(jointNumber, rootJoint, jointMap);
		
		this.wingL = this.getOrLogException(jointMap, "Wing_L");
		this.wingR = this.getOrLogException(jointMap, "Wing_R");
	}
}