package yesman.epicfight.animation;

public class Keyframe {
	private final float timeStamp;
	private final JointTransform transform;

	public Keyframe(float timeStamp, JointTransform trasnform) {
		this.timeStamp = timeStamp;
		this.transform = trasnform;
	}

	public float getTimeStamp() {
		return timeStamp;
	}

	public JointTransform getTransform() {
		return transform;
	}
}