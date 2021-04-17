package maninthehouse.epicfight.animation;

import java.util.List;

public class TransformSheet
{
	private final JointKeyframe[] keyframes;
	
	public TransformSheet(List<JointKeyframe> keyframeList)
	{
		JointKeyframe[] keyframes = new JointKeyframe[keyframeList.size()];
		for(int i = 0; i < keyframeList.size(); i++)
		{
			keyframes[i] = keyframeList.get(i);
		}
		
		this.keyframes = keyframes;
	}
	
	public TransformSheet(JointKeyframe[] keyframes)
	{
		
		this.keyframes = keyframes;
	}
	
	public JointTransform getStartTransform()
	{
		return keyframes[0].getTransform();
	}
	
	public JointTransform getInterpolatedTransform(float currentTime)
	{
		int prev = 0, next = 1;
		
		for(int i = 1; i < keyframes.length; i++)
		{
			if(currentTime <= keyframes[i].getTimeStamp())
				break;
			
			if(keyframes.length > next + 1)
			{
				prev++;
				next++;
			}
			else
				System.err.println("[ModError] : time exceeded keyframe length");
		}
		
		float progression = (currentTime - keyframes[prev].getTimeStamp()) / (keyframes[next].getTimeStamp() - keyframes[prev].getTimeStamp());
		JointTransform trasnform = JointTransform.interpolate(keyframes[prev].getTransform(), keyframes[next].getTransform(), progression);
		
		return trasnform;
	}
}
