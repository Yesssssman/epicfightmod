package yesman.epicfight.api.animation;

import java.util.Map;

import com.google.common.collect.Maps;

public class AnimationClip {
	protected Map<String, TransformSheet> jointTransforms = Maps.newHashMap();
	
	public void addJointTransform(String jointName, TransformSheet sheet) {
		this.jointTransforms.put(jointName, sheet);
	}
	
	public boolean hasJointTransform(String jointName) {
		return this.jointTransforms.containsKey(jointName);
	}
	
	public TransformSheet getJointTransform(String jointName) {
		return this.jointTransforms.get(jointName);
	}
	
	public final Pose getPoseInTime(float time) {
		Pose pose = new Pose();
		
		for (String jointName : this.jointTransforms.keySet()) {
			pose.putJointData(jointName, this.jointTransforms.get(jointName).getInterpolatedTransform(time));
		}
		
		return pose;
	}
	
	public Map<String, TransformSheet> getJointTransforms() {
		return this.jointTransforms;
	}
}