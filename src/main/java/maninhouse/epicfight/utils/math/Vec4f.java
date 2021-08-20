package maninhouse.epicfight.utils.math;

public class Vec4f extends Vec3f
{
	public float w;
	
	public Vec4f()
	{
		super();
		this.w = 0;
	}
	
	public Vec4f(float x, float y, float z, float w)
	{
		super(x, y, z);
		this.w = w;
	}
	
	public void set(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	@Override
	public Vec4f scale(float f)
	{
		super.scale(f);
		this.w *= f;
		return this;
	}
	
	public static Vec4f add(Vec4f left, Vec4f right, Vec4f dest)
	{
		if (dest == null)
			dest = new Vec4f();
		
		dest.set(left.x + right.x, left.y + right.y, left.z + right.z, left.w + right.w);
		return dest;
	}
	
	@Override
	public String toString()
	{
		return "Vec4f[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
	}
}