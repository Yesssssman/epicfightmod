package yesman.epicfight.api.animation;

public class Keyframe {
	private float timeStamp;
	private JointTransform transform;

	public Keyframe(float timeStamp, JointTransform trasnform) {
		this.timeStamp = timeStamp;
		this.transform = trasnform;
	}
	
	public Keyframe(Keyframe original) {
		this.transform = JointTransform.empty();
		this.copyFrom(original);
	}
	
	public void copyFrom(Keyframe target) {
		this.timeStamp = target.timeStamp;
		this.transform.copyFrom(target.transform);
	}
	
	public float time() {
		return this.timeStamp;
	}

	public JointTransform transform() {
		return this.transform;
	}
	
	public String toString() {
		return "Keyframe[Time: " + this.timeStamp + ", " + this.transform.toString() + "]";
	}
}