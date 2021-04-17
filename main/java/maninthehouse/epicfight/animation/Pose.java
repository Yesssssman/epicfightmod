package maninthehouse.epicfight.animation;

import java.util.HashMap;
import java.util.Map;

public class Pose
{
	private final Map<String, JointTransform> jointTransformData;
	
	public Pose()
	{
		jointTransformData = new HashMap<String, JointTransform>();
	}
	
	public void putJointData(String name, JointTransform transform)
	{
		this.jointTransformData.put(name, transform);
	}
	
	public Map<String, JointTransform> getJointTransformData()
	{
		return jointTransformData;
	}
	
	public JointTransform getTransformByName(String jointName)
	{
		JointTransform jt = jointTransformData.get(jointName);
		
		if(jt == null)
		{
			return JointTransform.defaultTransform;
		}
		return jt;
	}
	
	public static Pose interpolatePose(Pose pose1, Pose pose2, float pregression)
	{
		Pose pose = new Pose();
		
		for(String jointName : pose1.jointTransformData.keySet())
		{
			pose.putJointData(jointName, JointTransform.interpolate(pose1.jointTransformData.get(jointName), pose2.jointTransformData.get(jointName), pregression));
		}
		
		return pose;
	}
}