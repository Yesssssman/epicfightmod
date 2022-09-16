package yesman.epicfight.api.animation;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Pose {
	private final Map<String, JointTransform> jointTransformData = Maps.newHashMap();
	
	public void putJointData(String name, JointTransform transform) {
		this.jointTransformData.put(name, transform);
	}
	
	public Map<String, JointTransform> getJointTransformData() {
		return this.jointTransformData;
	}
	
	public JointTransform getOrDefaultTransform(String jointName) {
		return this.jointTransformData.getOrDefault(jointName, JointTransform.empty());
	}
	
	public static Pose interpolatePose(Pose pose1, Pose pose2, float pregression) {
		Pose pose = new Pose();
		
		Set<String> mergedSet = Sets.newHashSet();
		mergedSet.addAll(pose1.jointTransformData.keySet());
		mergedSet.addAll(pose2.jointTransformData.keySet());
		
		for (String jointName : mergedSet) {
			pose.putJointData(jointName, JointTransform.interpolate(pose1.getOrDefaultTransform(jointName), pose2.getOrDefaultTransform(jointName), pregression));
		}
		
		return pose;
	}
	
	public String toString() {
		String str = "[";
		
		for (Map.Entry<String, JointTransform> entry : this.jointTransformData.entrySet()) {
			str += String.format("%s{ %s, %s }, ", entry.getKey(), entry.getValue().translation().toString(), entry.getValue().rotation().toString());
		}
		
		str += "]";
		
		return str;
	}
}