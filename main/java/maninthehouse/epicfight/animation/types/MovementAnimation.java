package maninthehouse.epicfight.animation.types;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.main.GameConstants;

public class MovementAnimation extends StaticAnimation
{	
	public MovementAnimation(int id, float convertTime, boolean isRepeat, String path)
	{
		super(id, convertTime, isRepeat, path);
	}
	
	public MovementAnimation(String path)
	{
		super(path);
	}
	
	public MovementAnimation(float convertTime, boolean repeatPlay, String path)
	{
		super(convertTime, repeatPlay, path);
	}
	
	public MovementAnimation(int id, boolean repeatPlay, String path)
	{
		this(id, GameConstants.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path);
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata)
	{
		float movementSpeed = 1.0F;
		
		if(Math.abs(entitydata.getOriginalEntity().limbSwingAmount - entitydata.getOriginalEntity().prevLimbSwingAmount) < 0.007F)
		{
			movementSpeed *= (entitydata.getOriginalEntity().limbSwingAmount * 1.16F);
		}
		
		return movementSpeed;
	}
}