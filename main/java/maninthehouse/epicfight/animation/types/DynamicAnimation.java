package maninthehouse.epicfight.animation.types;

import java.util.HashMap;
import java.util.Map;

import maninthehouse.epicfight.animation.AnimationPlayer;
import maninthehouse.epicfight.animation.JointKeyframe;
import maninthehouse.epicfight.animation.JointTransform;
import maninthehouse.epicfight.animation.Pose;
import maninthehouse.epicfight.animation.TransformSheet;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.main.GameConstants;

public class DynamicAnimation
{
	protected final Map<String, TransformSheet> jointTransforms;
	protected final boolean isRepeat;
	protected final float convertTime;
	protected float totalTime;
	
	public DynamicAnimation()
	{
		jointTransforms = new HashMap<String, TransformSheet> ();
		this.totalTime = 0;
		this.isRepeat = false;
		this.convertTime = GameConstants.GENERAL_ANIMATION_CONVERT_TIME;
	}
	
	public DynamicAnimation(float convertTime, boolean isRepeat)
	{
		this(0, convertTime, isRepeat);
	}
	
	public DynamicAnimation(float totalTime, float convertTime, boolean isRepeat)
	{
		jointTransforms = new HashMap<String, TransformSheet> ();
		this.totalTime = totalTime;
		this.isRepeat = isRepeat;
		this.convertTime = convertTime;
	}
	
	public void addSheet(String jointName, TransformSheet sheet)
	{
		jointTransforms.put(jointName, sheet);
	}
	
	public Pose getPoseByTime(LivingData<?> entitydata, float time)
	{
		Pose pose = new Pose();
		
		for(String jointName : jointTransforms.keySet())
		{
			pose.putJointData(jointName, jointTransforms.get(jointName).getInterpolatedTransform(time));
		}
		
		return pose;
	}
	
	public void getLinkAnimation(Pose pose1, float timeModifier, LivingData<?> entitydata, LinkAnimation dest)
	{
		float totalTime = timeModifier >= 0 ? timeModifier + convertTime : convertTime;
		boolean isNeg = timeModifier < 0;
		float nextStart = isNeg ? -timeModifier : 0;
		
		if(isNeg)
			dest.startsAt = nextStart;
		
		dest.getTransfroms().clear();
		dest.setTotalTime(totalTime);
		dest.setNextAnimation(this);
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = getPoseByTime(entitydata, nextStart).getJointTransformData();
		
		for(String jointName : data1.keySet())
		{
			JointKeyframe[] keyframes = new JointKeyframe[2];
			keyframes[0] = new JointKeyframe(0, data1.get(jointName));
			keyframes[1] = new JointKeyframe(totalTime, data2.get(jointName));
			
			TransformSheet sheet = new TransformSheet(keyframes);
			dest.addSheet(jointName, sheet);
		}
	}
	
	public void putOnPlayer(AnimationPlayer player)
	{
		player.setPlayAnimation(this);
	}
	
	public void onActivate(LivingData<?> entitydata) {}
	public void onUpdate(LivingData<?> entitydata) {}
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {}
	
	public LivingData.EntityState getState(float time)
	{
		return LivingData.EntityState.FREE;
	}
	
	public Map<String, TransformSheet> getTransfroms()
	{
		return jointTransforms;
	}
	
	public float getPlaySpeed(LivingData<?> entitydata)
	{
		return 1.0F;
	}
	
	public void setTotalTime(float totalTime)
	{
		this.totalTime = totalTime;
	}
	
	public float getTotalTime()
	{
		return totalTime - 0.000001F;
	}
	
	public float getConvertTime()
	{
		return convertTime;
	}
	
	public boolean isRepeat()
	{
		return isRepeat;
	}
}