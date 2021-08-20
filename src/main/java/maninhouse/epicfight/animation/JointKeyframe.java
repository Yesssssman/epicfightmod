package maninhouse.epicfight.animation;

public class JointKeyframe {
	private final float timeStamp;
	private final JointTransform transform;

	public JointKeyframe(float timeStamp, JointTransform trasnform) {
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