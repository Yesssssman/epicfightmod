package maninhouse.epicfight.animation.types;

import java.util.Map;

import maninhouse.epicfight.animation.JointKeyframe;
import maninhouse.epicfight.animation.JointTransform;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.TransformSheet;
import maninhouse.epicfight.capabilities.entity.LivingData;

public class HitAnimation extends MainFrameAnimation {
	public HitAnimation(int id, float convertTime, String path) {
		super(id, convertTime, path);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata) {
		super.onActivate(entitydata);
		entitydata.cancelUsingItem();
	}
	
	@Override
	public void getLinkAnimation(Pose pose1, float timeModifier, LivingData<?> entitydata, LinkAnimation dest) {
		dest.getTransfroms().clear();
		dest.setTotalTime(timeModifier + this.convertTime);
		dest.setNextAnimation(this);
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = super.getPoseByTime(entitydata, 0.0F).getJointTransformData();
		Map<String, JointTransform> data3 = super.getPoseByTime(entitydata, this.totalTime - 0.00001F).getJointTransformData();
		
		for (String jointName : data1.keySet()) {
			JointKeyframe[] keyframes = new JointKeyframe[4];
			keyframes[0] = new JointKeyframe(0, data1.get(jointName));
			keyframes[1] = new JointKeyframe(this.convertTime, data2.get(jointName));
			keyframes[2] = new JointKeyframe(this.convertTime + 0.033F, data3.get(jointName));
			keyframes[3] = new JointKeyframe(timeModifier + this.convertTime, data3.get(jointName));
			TransformSheet sheet = new TransformSheet(keyframes);
			dest.addSheet(jointName, sheet);
		}
	}
	
	@Override
	public Pose getPoseByTime(DynamicAnimation animation, LivingData<?> entitydata, float time) {
		return (animation instanceof LinkAnimation) ? super.getPoseByTime(animation, entitydata, time) : super.getPoseByTime(animation, entitydata, this.getTotalTime() - 0.000001F);
	}
	
	@Override
	public EntityState getState(float time) {
		return EntityState.HIT;
	}
}