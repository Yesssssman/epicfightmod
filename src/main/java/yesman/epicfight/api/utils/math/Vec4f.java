package yesman.epicfight.api.utils.math;

public class Vec4f extends Vec3f {
	public float w;
	
	public Vec4f() {
		super();
		this.w = 0;
	}
	
	public Vec4f(float x, float y, float z, float w) {
		super(x, y, z);
		this.w = w;
	}
	
	public Vec4f(Vec3f vec3f) {
		super(vec3f.x, vec3f.y, vec3f.z);
		this.w = 1.0F;
	}
	
	@Override
	public Vec4f scale(float f) {
		super.scale(f);
		this.w *= f;
		return this;
	}
	
	public Vec4f add(float x, float y, float z, float w) {
		return add(this, new Vec4f(x, y, z, w), this);
	}
	
	public static Vec4f add(Vec4f left, Vec4f right, Vec4f dest) {
		if (dest == null) {
			dest = new Vec4f();
		}
		
		dest.x = left.x + right.x;
		dest.y = left.y + right.y;
		dest.z = left.z + right.z;
		dest.w = left.w + right.w;
		
		return dest;
	}
	
	public Vec4f transform(OpenMatrix4f matrix) {
		return OpenMatrix4f.transform(matrix, this, this);
	}
	
	@Override
	public String toString() {
		return "Vec4f[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
	}
}