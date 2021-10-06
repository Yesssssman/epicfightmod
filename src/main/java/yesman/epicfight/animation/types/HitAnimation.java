package yesman.epicfight.animation.types;

import java.util.Map;

import yesman.epicfight.animation.JointTransform;
import yesman.epicfight.animation.Keyframe;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.TransformSheet;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.model.Model;

public class HitAnimation extends MainFrameAnimation {
	public HitAnimation(float convertTime, String path, Model model) {
		super(convertTime, path, model);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata) {
		super.onActivate(entitydata);
		entitydata.cancelUsingItem();
	}
	
	@Override
	public void setLinkAnimation(Pose pose1, float timeModifier, LivingData<?> entitydata, LinkAnimation dest) {
		dest.getTransfroms().clear();
		dest.setTotalTime(timeModifier + this.convertTime);
		dest.setNextAnimation(this);
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = super.getPoseByTime(entitydata, 0.0F).getJointTransformData();
		Map<String, JointTransform> data3 = super.getPoseByTime(entitydata, this.totalTime - 0.00001F).getJointTransformData();
		
		for (String jointName : data1.keySet()) {
			if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
				Keyframe[] keyframes = new Keyframe[4];
				keyframes[0] = new Keyframe(0, data1.get(jointName));
				keyframes[1] = new Keyframe(this.convertTime, data2.get(jointName));
				keyframes[2] = new Keyframe(this.convertTime + 0.033F, data3.get(jointName));
				keyframes[3] = new Keyframe(timeModifier + this.convertTime, data3.get(jointName));
				TransformSheet sheet = new TransformSheet(keyframes);
				dest.addSheet(jointName, sheet);
			}
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		return super.getPoseByTime(entitydata, this.getTotalTime() - 0.000001F);
	}
	
	@Override
	public EntityState getState(float time) {
		return EntityState.HIT;
	}
}