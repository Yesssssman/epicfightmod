package yesman.epicfight.api.animation;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.math.Quaternion;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TransformSheet {
	private Keyframe[] keyframes;
	
	public TransformSheet(List<Keyframe> keyframeList) {
		this(keyframeList.toArray(new Keyframe[0]));
	}
	
	public TransformSheet(Keyframe[] keyframes) {
		this.keyframes = keyframes;
	}
	
	public TransformSheet() {
		this(new Keyframe[0]);
	}
	
	public JointTransform getStartTransform() {
		return this.keyframes[0].transform();
	}
	
	public Keyframe[] getKeyframes() {
		return this.keyframes;
	}
	
	public TransformSheet copyAll() {
		return this.copy(0, this.keyframes.length);
	}
	
	public TransformSheet copy(int start, int end) {
		int len = end - start;
		Keyframe[] newKeyframes = new Keyframe[len];
		
		for (int i = 0; i < len; i++) {
			Keyframe kf = this.keyframes[i + start];
			newKeyframes[i] = new Keyframe(kf);
		}
		
		return new TransformSheet(newKeyframes);
	}
	
	public TransformSheet readFrom(TransformSheet opponent) {
		if (opponent.keyframes.length != this.keyframes.length) {
			this.keyframes = new Keyframe[opponent.keyframes.length];
			
			for (int i = 0; i < this.keyframes.length; i++) {
				this.keyframes[i] = new Keyframe(0.0F, JointTransform.empty());
			}
		}
		
		for (int i = 0; i < this.keyframes.length; i++) {
			this.keyframes[i].copyFrom(opponent.keyframes[i]);
		}
		
		return this;
	}
	
	public void transform(Consumer<JointTransform> transformFunc) {
		this.transform(transformFunc, 0, this.keyframes.length);
	}
	
	public void transform(Consumer<JointTransform> transformFunc, int start, int end) {
		end = Math.min(end, this.keyframes.length);
		
		for (int i = start; i < end; i++) {
			transformFunc.accept(this.keyframes[i].transform());
		}
	}
	
	public Vec3f getInterpolatedTranslation(float currentTime) {
		InterpolationInfo interpolInfo = this.getInterpolationInfo(currentTime);
		Vec3f vec3f = MathUtils.lerpVector(this.keyframes[interpolInfo.prev].transform().translation(), this.keyframes[interpolInfo.next].transform().translation(), interpolInfo.zero2One);
		return vec3f;
	}
	
	public Quaternion getInterpolatedRotation(float currentTime) {
		InterpolationInfo interpolInfo = this.getInterpolationInfo(currentTime);
		Quaternion quat = MathUtils.lerpQuaternion(this.keyframes[interpolInfo.prev].transform().rotation(), this.keyframes[interpolInfo.next].transform().rotation(), interpolInfo.zero2One);
		return quat;
	}
	
	public JointTransform getInterpolatedTransform(float currentTime) {
		InterpolationInfo interpolInfo = this.getInterpolationInfo(currentTime);
		JointTransform trasnform = JointTransform.interpolate(this.keyframes[interpolInfo.prev].transform(), this.keyframes[interpolInfo.next].transform(), interpolInfo.zero2One);
		return trasnform;
	}
	
	public void correctAnimationByNewPosition(Vec3f startpos, Vec3f startToEnd, Vec3f modifiedStart, Vec3f modifiedStartToEnd) {
		Keyframe[] keyframes = this.getKeyframes();
		Keyframe startKeyframe = keyframes[0];
		Keyframe endKeyframe = keyframes[keyframes.length - 1];
		float pitchDeg = (float) Math.toDegrees(Mth.atan2(modifiedStartToEnd.y - startToEnd.y, modifiedStartToEnd.length()));
		float yawDeg = (float) Math.toDegrees(MathUtils.getAngleBetween(modifiedStartToEnd.copy().multiply(1.0F, 0.0F, 1.0F).normalise(), startToEnd.copy().multiply(1.0F, 0.0F, 1.0F).normalise()));
		
		for (Keyframe kf : keyframes) {
			float lerp = (kf.time() - startKeyframe.time()) / (endKeyframe.time() - startKeyframe.time());
			Vec3f line = MathUtils.lerpVector(new Vec3f(0F, 0F, 0F), startToEnd, lerp);
			Vec3f modifiedLine = MathUtils.lerpVector(new Vec3f(0F, 0F, 0F), modifiedStartToEnd, lerp);
			Vec3f keyTransform = kf.transform().translation();
			Vec3f startToKeyTransform = keyTransform.copy().sub(startpos).multiply(-1.0F, 1.0F, -1.0F);
			Vec3f animOnLine = startToKeyTransform.copy().sub(line);
			OpenMatrix4f rotator = OpenMatrix4f.createRotatorDeg(pitchDeg, Vec3f.X_AXIS).mulFront(OpenMatrix4f.createRotatorDeg(yawDeg, Vec3f.Y_AXIS));
			Vec3f toNewKeyTransform = modifiedLine.add(OpenMatrix4f.transform3v(rotator, animOnLine, null));
			keyTransform.set(modifiedStart.copy().add((toNewKeyTransform)));
		}
	}
	
	public TransformSheet getCorrectedModelCoord(LivingEntityPatch<?> entitypatch, Vec3 start, Vec3 dest, int startFrame, int endFrame) {
		TransformSheet transform = this.copyAll();
		float horizontalDistance = (float) dest.subtract(start).horizontalDistance();
		float verticalDistance = (float) Math.abs(dest.y - start.y);
		JointTransform startJt = transform.getKeyframes()[startFrame].transform();
		JointTransform endJt = transform.getKeyframes()[endFrame].transform();
		Vec3f jointCoord = new Vec3f(startJt.translation().x, verticalDistance, horizontalDistance);
		
		startJt.translation().set(jointCoord);
		
		for (int i = startFrame + 1; i < endFrame; i++) {
			JointTransform middleJt = transform.getKeyframes()[i].transform();
			middleJt.translation().set(MathUtils.lerpVector(startJt.translation(), endJt.translation(), transform.getKeyframes()[i].time() / transform.getKeyframes()[endFrame].time()));
		}
		
		return transform;
	}
	
	public TransformSheet extendsZCoord(float multiplier, int startFrame, int endFrame) {
		TransformSheet transform = this.copyAll();
		float extend = 0.0F;
		
		for (int i = 0; i < endFrame + 1; i++) {
			Keyframe kf = transform.getKeyframes()[i];
			float prevZ = kf.transform().translation().z;
			kf.transform().translation().multiply(1.0F, 1.0F, multiplier);
			float extendedZ = kf.transform().translation().z;
			extend = extendedZ - prevZ;
		}
		
		for (int i = endFrame + 1; i < transform.getKeyframes().length; i++) {
			Keyframe kf = transform.getKeyframes()[i];
			kf.transform().translation().add(0.0F, 0.0F, extend);
		}
		
		return transform;
	}
	
	public TransformSheet getCorrectedWorldCoord(LivingEntityPatch<?> entitypatch, Vec3 start, Vec3 dest, float xRot, float yRot, int startFrame, int endFrame) {
		TransformSheet newTransformSheet = this.copyAll();
		Vec3f firstPos = newTransformSheet.keyframes[0].transform().translation().copy();
		
		newTransformSheet.transform((jt) -> {
			jt.translation().sub(firstPos);
		});
		
		Vec3f fromCoord = newTransformSheet.keyframes[startFrame].transform().translation();
		Vec3f toCoord = newTransformSheet.keyframes[endFrame - 1].transform().translation();
		float originalDistance = (float)Math.sqrt(fromCoord.distanceSqr(toCoord));
		float worldDistance = (float)Math.sqrt(dest.distanceToSqr(start));
		float ratio = worldDistance / originalDistance;
		
		newTransformSheet.transform((jt) -> {
			Vec3f kfTranslation = jt.translation();
			kfTranslation.set(-kfTranslation.x, kfTranslation.y, kfTranslation.z > 0.0F ? kfTranslation.z : kfTranslation.z * ratio);
			Vec3f relativeCoord = Vec3f.rotate(xRot, Vec3f.X_AXIS, Vec3f.sub(kfTranslation, fromCoord, null), null);
			kfTranslation.set(Vec3f.add(fromCoord, relativeCoord, null));
		}, startFrame, endFrame);
		
		newTransformSheet.transform((jt) -> {
			jt.translation().rotate(yRot, Vec3f.Y_AXIS);
			jt.translation().multiply(1.0F, 1.0F, -1.0F);
			jt.translation().add((float)start.x, (float)start.y, (float)start.z);
		});
		
		return newTransformSheet;
	}
	
	private InterpolationInfo getInterpolationInfo(float currentTime) {
		int prev = 0, next = 1;
		
		for (int i = 1; i < this.keyframes.length; i++) {
			if (currentTime <= this.keyframes[i].time()) {
				break;
			}
			
			if (this.keyframes.length > next + 1) {
				prev++;
				next++;
			} else {
				//EpicFightMod.LOGGER.error("time exceeded keyframe length. current : " + currentTime + " max : " + this.keyframes[this.keyframes.length - 1].time());
				//(new IllegalArgumentException()).printStackTrace();
			}
		}
		
		float progression = (currentTime - this.keyframes[prev].time()) / (this.keyframes[next].time() - this.keyframes[prev].time());
		
		return new InterpolationInfo(prev, next, progression);
	}
	
	@Override
	public String toString() {
		return "total " + this.keyframes.length + " frames";
	}
	
	private static class InterpolationInfo {
		private int prev;
		private int next;
		private float zero2One;
		
		private InterpolationInfo(int prev, int next, float zero2One) {
			this.prev = prev;
			this.next = next;
			this.zero2One = zero2One;
		}
	}
}