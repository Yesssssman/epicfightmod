package maninhouse.epicfight.animation;

import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.OpenMatrix4f;

public class JointTransform {
	public static final JointTransform NONE_TRANSFORM = new JointTransform(new Vec3f(0.0F, 0.0F, 0.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), new Vec3f(1.0F, 1.0F, 1.0F));
	
	private Vec3f position;
	private Vec3f scale;
	private Quaternion rotation;
	private Quaternion dynamicRotation;

	public JointTransform(Vec3f position, Quaternion rotation, Vec3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public JointTransform(Vec3f position, Quaternion rotation, Quaternion customRotation, Vec3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.dynamicRotation = customRotation;
	}
	
	public Vec3f getPosition() {
		return this.position;
	}

	public Quaternion getRotation() {
		return this.rotation;
	}
	
	public Quaternion getDynamicRotation() {
		return this.dynamicRotation;
	}
	
	public Vec3f getScale() {
		return this.scale;
	}

	public void setRotation(Quaternion quat) {
		this.rotation = quat;
	}
	
	public void setCustomRotation(Quaternion quat) {
		this.dynamicRotation = quat;
	}
	
	public OpenMatrix4f toTransformMatrix() {
		OpenMatrix4f matrix = new OpenMatrix4f();
		matrix.translate(position);
		OpenMatrix4f.mul(matrix, rotation.toRotationMatrix(), matrix);
		matrix.scale(this.scale);
		return matrix;
	}
	
	public static JointTransform interpolate(JointTransform prev, JointTransform next, float progression) {
		if (prev == null || next == null) {
			return JointTransform.NONE_TRANSFORM;
		}
		
		Vec3f pos = interpolate(prev.position, next.position, progression);
		Quaternion rot = Quaternion.interpolate(prev.rotation, next.rotation, progression);
		Vec3f scale = interpolate(prev.scale, next.scale, progression);
		if (prev.dynamicRotation != null || next.dynamicRotation != null) {
			if (prev.dynamicRotation == null) {
				prev.dynamicRotation = new Quaternion(0, 0, 0, 1);
			}
			if (next.dynamicRotation == null) {
				next.dynamicRotation = new Quaternion(0, 0, 0, 1);
			}
			return new JointTransform(pos, rot, Quaternion.interpolate(prev.dynamicRotation, next.dynamicRotation, progression), scale);
		} else {
			return new JointTransform(pos, rot, scale);
		}
	}
	
	private static Vec3f interpolate(Vec3f start, Vec3f end, float progression) {
		float x = start.x + (end.x - start.x) * progression;
		float y = start.y + (end.y - start.y) * progression;
		float z = start.z + (end.z - start.z) * progression;
		return new Vec3f(x, y, z);
	}
}