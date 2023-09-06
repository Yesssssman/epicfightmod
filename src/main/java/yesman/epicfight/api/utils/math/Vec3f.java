package yesman.epicfight.api.utils.math;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.world.phys.Vec3;
import yesman.epicfight.main.EpicFightMod;

public class Vec3f extends Vec2f {
	public static final Vec3f X_AXIS = new Vec3f(1.0F, 0.0F, 0.0F);
	public static final Vec3f Y_AXIS = new Vec3f(0.0F, 1.0F, 0.0F);
	public static final Vec3f Z_AXIS = new Vec3f(0.0F, 0.0F, 1.0F);
	
	public float z;
	
	public Vec3f() {
		super();
		this.z = 0;
	}
	
	public Vec3f(float x, float y, float z) {
		super(x, y);
		this.z = z;
	}
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vec3f vec3f) {
		this.x = vec3f.x;
		this.y = vec3f.y;
		this.z = vec3f.z;
	}
	
	public Vec3f add(float x, float y, float z) {
		return add(new Vec3f(x, y, z));
	}
	
	public Vec3f add(Vec3f vec) {
		return Vec3f.add(vec, this, this);
	}
	
	public static Vec3f add(Vec3f left, Vec3f right, Vec3f dest) {
		if (dest == null) {
			return new Vec3f(left.x + right.x, left.y + right.y, left.z + right.z);
		} else {
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
			return dest;
		}
	}
	
	public Vec3f sub(Vec3f vec) {
		return sub(this, vec, this);
	}
	
	public static Vec3f sub(Vec3f left, Vec3f right, Vec3f dest) {
		if (dest == null) {
			return new Vec3f(left.x - right.x, left.y - right.y, left.z - right.z);
		} else {
			dest.set(left.x - right.x, left.y - right.y, left.z - right.z);
			return dest;
		}
	}
	
	public Vec3f multiply(Vec3f vec) {
		return multiply(this, this, vec.x, vec.y, vec.z);
	}
	
	public Vec3f multiply(float x, float y, float z) {
		return multiply(this, this, x, y, z);
	}
	
	public static Vec3f multiply(Vec3f src, Vec3f dest, float x, float y, float z) {
		if (dest == null) {
			dest = new Vec3f();
		}
		
		dest.x = src.x * x;
		dest.y = src.y * y;
		dest.z = src.z * z;
		
		return dest;
	}
	
	@Override
	public Vec3f scale(float f) {
		return scale(this, this, f);
	}
	
	public static Vec3f scale(Vec3f src, Vec3f dest, float f) {
		if (dest == null) {
			dest = new Vec3f();
		}
		
		dest.x = src.x * f;
		dest.y = src.y * f;
		dest.z = src.z * f;
		
		return dest;
	}
	
	public Vec3f copy() {
		return new Vec3f(this.x, this.y, this.z);
	}
	
	public float length() {
		return (float) Math.sqrt(this.lengthSqr());
	}
	
	public float lengthSqr() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}
	
	public float distanceSqr(Vec3f opponent) {
		return new Vec3f(this.x - opponent.x, this.y - opponent.y, this.z - opponent.z).lengthSqr();
	}
	
	public void rotate(float degree, Vec3f axis) {
		rotate(degree, axis, this, this);
	}
	
	public static Vec3f rotate(float degree, Vec3f axis, Vec3f src, Vec3f dest) {
		if (dest == null) {
			dest = new Vec3f();
		}
		
		return OpenMatrix4f.transform3v(OpenMatrix4f.createRotatorDeg(degree, axis), src, dest);
	}
	
	public static float dot(Vec3f left, Vec3f right) {
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}
	
	public static Vec3f cross(Vec3f left, Vec3f right, Vec3f dest) {
		if (dest == null) {
			dest = new Vec3f();
		}
		
		dest.set(left.y * right.z - left.z * right.y, right.x * left.z - right.z * left.x, left.x * right.y - left.y * right.x);
		
		return dest;
	}
	
	public static float getAngleBetween(Vec3f a, Vec3f b) {
		return (float) Math.acos(Math.min(1.0F, Vec3f.dot(a, b) / (a.length() * b.length())));
	}
	
	public static Quaternion getRotatorBetween(Vec3f a, Vec3f b) {
		Vec3f axis = Vec3f.cross(a, b, null).normalise();
		float dotDivLength = Vec3f.dot(a, b) / (a.length() * b.length());
		
		if (!Float.isFinite(dotDivLength)) {
			EpicFightMod.LOGGER.info("Warning : given vector's length is zero");
			(new IllegalArgumentException()).printStackTrace();
			dotDivLength = 1.0F;
		}
		
		float radian = (float)Math.acos(Math.min(1.0F, dotDivLength));
		return new Quaternion(axis.toMojangVector(), radian, false);
	}
	
	public Vec3f normalise() {
		float norm = (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		if (norm != 0) {
			this.x /= norm;
			this.y /= norm;
			this.z /= norm;
		} else {
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}
		
		return this;
	}
	
	@Override
	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + "]";
	}
	
	public Vector3f toMojangVector() {
		return new Vector3f(this.x, this.y, this.z);
	}
	
	public Vec3 toDoubleVector() {
		return new Vec3(this.x, this.y, this.z);
	}
	
	public static Vec3f fromMojangVector(Vector3f vec3) {
		return new Vec3f(vec3.x(), vec3.y(), vec3.z());
	}
	
	public static Vec3f fromDoubleVector(Vec3 vec3) {
		return new Vec3f((float)vec3.x(), (float)vec3.y(), (float)vec3.z());
	}
}