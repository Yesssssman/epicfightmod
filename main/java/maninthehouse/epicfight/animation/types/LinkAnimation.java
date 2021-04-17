package maninthehouse.epicfight.animation.types;

import maninthehouse.epicfight.capabilities.entity.LivingData;

public class LinkAnimation extends DynamicAnimation
{
	protected DynamicAnimation nextAnimation;
	protected float startsAt;
	
	@Override
	public void onFinish(LivingData<?> entity, boolean isEnd)
	{
		if(!isEnd)
			nextAnimation.onFinish(entity, isEnd);
		else
		{
			if(startsAt > 0)
			{
				entity.getAnimator().getPlayer().setElapsedTime(startsAt);
				entity.getAnimator().getPlayer().checkNoResetMark();
				startsAt = 0;
			}
		}
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		return nextAnimation.getState(0.0F);
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata)
	{
		return this.nextAnimation.getPlaySpeed(entitydata);
	}
	
	public void setNextAnimation(DynamicAnimation animation)
	{
		this.nextAnimation = animation;
	}
	
	public DynamicAnimation getNextAnimation()
	{
		return this.nextAnimation;
	}
	
	@Override
	public String toString()
	{
		return "NextAnimation " + this.nextAnimation;
	}
}