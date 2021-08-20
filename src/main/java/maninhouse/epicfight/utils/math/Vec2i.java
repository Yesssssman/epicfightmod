package maninhouse.epicfight.utils.math;

public class Vec2i
{
	public int x;
	public int y;
	
	public Vec2i()
	{
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2i(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString()
	{
		return "Vecif[" + this.x + ", " + this.y + ", " + "]";
	}
}