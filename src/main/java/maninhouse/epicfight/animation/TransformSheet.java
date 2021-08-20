package maninhouse.epicfight.animation;

import java.util.List;

public class TransformSheet {
	private final JointKeyframe[] keyframes;

	public TransformSheet(List<JointKeyframe> keyframeList) {
		JointKeyframe[] keyframes = new JointKeyframe[keyframeList.size()];
		for (int i = 0; i < keyframeList.size(); i++) {
			keyframes[i] = keyframeList.get(i);
		}
		
		this.keyframes = keyframes;
	}

	public TransformSheet(JointKeyframe[] keyframes) {
		this.keyframes = keyframes;
	}
	
	public JointTransform getStartTransform() {
		return this.keyframes[0].getTransform();
	}
	
	public JointTransform getInterpolatedTransform(float currentTime) {
		int prev = 0, next = 1;
		for (int i = 1; i < this.keyframes.length; i++) {
			if (currentTime <= this.keyframes[i].getTimeStamp()) {
				break;
			}
			if (this.keyframes.length > next + 1) {
				prev++;
				next++;
			} else {
				System.err.println("[ModError] : time exceeded keyframe length " + currentTime + " " + this.keyframes[this.keyframes.length - 1].getTimeStamp());
			}
		}
		
		float progression = getBezierInterpolation((currentTime - this.keyframes[prev].getTimeStamp()) / (this.keyframes[next].getTimeStamp() - this.keyframes[prev].getTimeStamp()));
		JointTransform trasnform = JointTransform.interpolate(this.keyframes[prev].getTransform(), this.keyframes[next].getTransform(), progression);
		
		return trasnform;
	}
	
	private static float getBezierInterpolation(float t) {
		float start = 0.0F;
		float end = 1.0F;
		float p0 = -t*t*t + 3.0F*t*t - 3.0F*t + 1.0F;
		float p1 = 3.0F*t*t*t - 6.0F*t*t + 3.0F*t;
		float p2 = -3.0F*t*t*t + 3.0F*t*t;
		float p3 = t*t*t;
		return (start * p0) + (start * p1) + (end * p2) + (end * p3);
	}
}