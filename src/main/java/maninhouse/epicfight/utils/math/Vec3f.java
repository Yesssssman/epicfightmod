package maninhouse.epicfight.utils.math;

public class Vec3f extends Vec2f
{
	public float z;
	
	public Vec3f()
	{
		super();
		this.z = 0;
	}
	
	public Vec3f(float x, float y, float z)
	{
		super(x, y);
		this.z = z;
	}
	
	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Vec3f add(Vec3f left, Vec3f right, Vec3f dest)
	{
		if (dest == null)
			return new Vec3f(left.x + right.x, left.y + right.y, left.z + right.z);
		else
		{
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
			return dest;
		}
	}
	
	public static Vec3f sub(Vec3f left, Vec3f right, Vec3f dest)
	{
		if (dest == null)
			return new Vec3f(left.x - right.x, left.y - right.y, left.z - right.z);
		else
		{
			dest.set(left.x - right.x, left.y - right.y, left.z - right.z);
			return dest;
		}
	}
	
	@Override
	public Vec3f scale(float f)
	{
		super.scale(f);
		this.z *= f;
		return this;
	}
	
	public float length()
	{
		return (float) Math.sqrt(this.lengthSqr());
	}
	
	public float lengthSqr()
	{
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}
	
	public static float dot(Vec3f left, Vec3f right)
	{
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}
	
	public static Vec3f cross(Vec3f left, Vec3f right, Vec3f dest)
	{
		if (dest == null)
			dest = new Vec3f();

		dest.set(left.y * right.z - left.z * right.y, right.x * left.z - right.z * left.x, left.x * right.y - left.y * right.x);

		return dest;
	}

	public void normalise()
	{
		float norm = (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		if(norm != 0)
		{
			this.x /= norm;
			this.y /= norm;
			this.z /= norm;
		}
		else
		{
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}
	}
	
	@Override
	public String toString()
	{
		return "Vec3f[" + this.x + ", " + this.y + ", " + this.z + "]";
	}
}