package yesman.epicfight.api.animation.types.procedural;

import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.utils.math.Vec3f;

public class TipPointAnimation {
	public final IKInfo ikSetter;
	private final TransformSheet animation;
	private Vec3f targetpos;
	private float time;
	private float startTime;
	private float totalTime;
	private float dt;
	private boolean isWorking;
	private boolean isTouchingGround;
	
	public TipPointAnimation(TransformSheet animation, Vec3f initpos, IKInfo ikSetter) {
		this.animation = animation;
		this.targetpos = initpos;
		this.ikSetter = ikSetter;
		this.time = 0.0F;
	}
	
	public boolean isOnWorking() {
		return this.isWorking;
	}
	
	public float getTime(float partialTicks) {
		float curTime = this.time - this.dt * (1.0F - partialTicks);
		return curTime * (this.totalTime - this.startTime) + this.startTime;
	}
	
	public void start(Vec3f targetpos, TransformSheet animation, float speed) {
		this.isWorking = true;
		this.time = 0.0F;
		this.targetpos = targetpos;
		Keyframe[] keyframes = animation.getKeyframes();
		this.startTime = keyframes[0].time();
		this.totalTime = keyframes[keyframes.length - 1].time();
		this.dt = (1.0F / (this.totalTime - this.startTime) * 0.05F) * speed;// * 0.05F;
		
		if (this.animation != animation) { 
			this.animation.readFrom(animation);
		}
	}
	
	public void newTargetPosition(Vec3f targetpos) {
		Vec3f dv = targetpos.copy().sub(this.targetpos);
		this.targetpos = targetpos;
		Keyframe[] keyframes = this.animation.getKeyframes();
		float curTime = this.getTime(1.0F);
		int startFrame = 0;
		
		while (keyframes[startFrame].time() < curTime) {
			startFrame++;
		}
		
		for (int i = startFrame; i < keyframes.length; i++) {
			keyframes[i].transform().translation().add(dv.copy());
		}
	}
	
	public void tick() {
		this.time += this.dt;
		
		if (this.time >= 1.0F) {
			this.isWorking = false;
			this.time = 1.0F;
		}
		
		Keyframe[] keyframes = this.animation.getKeyframes();
		float curTime = this.getTime(1.0F);
		int startFrame = 0;
		
		while (keyframes[startFrame].time() < curTime) {
			startFrame++;
		}
		
		boolean[] touchGround = this.ikSetter.touchingGround;
		
		if (startFrame >= touchGround.length) {
			this.isTouchingGround = touchGround[touchGround.length - 1];
		} else if (startFrame == 0) {
			this.isTouchingGround = touchGround[0];
		} else {
			this.isTouchingGround = touchGround[startFrame - 1] && touchGround[startFrame];
		}
	}
	
	public Vec3f getTipPosition(float partialTicks) {
		return this.animation.getInterpolatedTranslation(this.getTime(partialTicks));
	}
	
	public JointTransform getTipTransform(float partialTicks) {
		return this.animation.getInterpolatedTransform(this.getTime(partialTicks));
	}
	
	public Vec3f getTargetPosition() {
		return this.targetpos;
	}
	
	public TransformSheet getAnimation() {
		return this.animation;
	}
	
	public boolean isTouchingGround() {
		return this.isTouchingGround;
	}
}
