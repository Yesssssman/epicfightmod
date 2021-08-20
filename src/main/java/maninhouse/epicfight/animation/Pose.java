package maninhouse.epicfight.animation;

import java.util.HashMap;
import java.util.Map;

public class Pose {
	private final Map<String, JointTransform> jointTransformData;

	public Pose() {
		this.jointTransformData = new HashMap<String, JointTransform>();
	}
	
	public void putJointData(String name, JointTransform transform) {
		this.jointTransformData.put(name, transform);
	}
	
	public Map<String, JointTransform> getJointTransformData() {
		return this.jointTransformData;
	}
	
	public JointTransform getTransformByName(String jointName) {
		JointTransform jt = this.jointTransformData.get(jointName);
		if (jt == null) {
			return JointTransform.NONE_TRANSFORM;
		}
		
		return jt;
	}
	
	public static Pose interpolatePose(Pose pose1, Pose pose2, float pregression) {
		Pose pose = new Pose();
		
		for (String jointName : pose1.jointTransformData.keySet()) {
			pose.putJointData(jointName, JointTransform.interpolate(pose1.jointTransformData.get(jointName), pose2.jointTransformData.get(jointName), pregression));
		}
		
		return pose;
	}
	
	public String toString() {
		return this.jointTransformData.keySet().toString();
	}
}