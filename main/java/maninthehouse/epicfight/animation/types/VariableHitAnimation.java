package maninthehouse.epicfight.animation.types;

import java.util.Map;

import maninthehouse.epicfight.animation.JointKeyframe;
import maninthehouse.epicfight.animation.JointTransform;
import maninthehouse.epicfight.animation.Pose;
import maninthehouse.epicfight.animation.TransformSheet;
import maninthehouse.epicfight.capabilities.entity.LivingData;

public class VariableHitAnimation extends HitAnimation
{
	public VariableHitAnimation(int id, float convertTime, String path)
	{
		super(id, convertTime, path);
	}
	
	@Override
	public void getLinkAnimation(Pose pose1, float timeModifier, LivingData<?> entitydata, LinkAnimation dest)
	{
		dest.getTransfroms().clear();
		dest.setTotalTime(timeModifier + convertTime);
		dest.setNextAnimation(this);
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = super.getPoseByTime(entitydata, 0.0F).getJointTransformData();
		Map<String, JointTransform> data3 = super.getPoseByTime(entitydata, this.totalTime - 0.00001F).getJointTransformData();
		
		for(String jointName : data1.keySet())
		{
			JointKeyframe[] keyframes = new JointKeyframe[4];
			keyframes[0] = new JointKeyframe(0, data1.get(jointName));
			keyframes[1] = new JointKeyframe(convertTime, data2.get(jointName));
			keyframes[2] = new JointKeyframe(convertTime + 0.033F, data3.get(jointName));
			keyframes[3] = new JointKeyframe(timeModifier + convertTime, data3.get(jointName));
			TransformSheet sheet = new TransformSheet(keyframes);
			dest.addSheet(jointName, sheet);
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time)
	{
		return super.getPoseByTime(entitydata, this.getTotalTime() - 0.000001F);
	}
}