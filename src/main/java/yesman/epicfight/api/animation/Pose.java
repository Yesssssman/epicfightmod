package yesman.epicfight.api.animation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import yesman.epicfight.api.client.animation.property.JointMask;

public class Pose {
	private final Map<String, JointTransform> jointTransformData = Maps.newHashMap();
	
	public void putJointData(String name, JointTransform transform) {
		this.jointTransformData.put(name, transform);
	}
	
	public void putJointData(Pose pose) {
		this.jointTransformData.putAll(pose.jointTransformData);
	}
	
	public Map<String, JointTransform> getJointTransformData() {
		return this.jointTransformData;
	}
	
	public JointTransform getOrDefaultTransform(String jointName) {
		return this.jointTransformData.getOrDefault(jointName, JointTransform.empty());
	}
	
	public void removeJointIf(Predicate<? super Map.Entry<String, JointTransform>> predicate) {
		this.jointTransformData.entrySet().removeIf(predicate);
	}
	
	public void removeJointIf(List<JointMask> jointsToRemove) {
		Set<String> joints = Sets.newHashSet(this.jointTransformData.keySet());
		
		for (JointMask mask : jointsToRemove) {
			joints.remove(mask.getJointName());
		}
		
		joints.forEach(this.jointTransformData::remove);
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