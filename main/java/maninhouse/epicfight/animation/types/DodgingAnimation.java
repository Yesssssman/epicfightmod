package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.utils.game.Formulars;
import net.minecraft.entity.EntitySize;

public class DodgingAnimation extends ActionAnimation
{
	private final EntitySize size;
	
	public DodgingAnimation(int id, float convertTime, boolean affectVelocity, String path, float width, float height)
	{
		this(id, convertTime, 0.0F, affectVelocity, path, width, height);
	}
	
	public DodgingAnimation(int id, float convertTime, float delayTime, boolean affectVelocity, String path, float width, float height)
	{
		super(id, convertTime, delayTime, affectVelocity, false, path);
		
		if(width > 0.0F || height > 0.0F)
			this.size = EntitySize.flexible(width, height);
		else
			this.size = null;
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata)
	{
		super.onUpdate(entitydata);
		if(this.size != null)
			entitydata.resetSize(size);
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd)
	{
		super.onFinish(entitydata, isEnd);
		if(this.size != null)
			entitydata.getOriginalEntity().recalculateSize();
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata)
	{
		return Formulars.getRollAnimationSpeedPanelty((float)entitydata.getWeight());
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		if(time < this.delayTime)
			return LivingData.EntityState.PRE_DELAY;
		else
			return LivingData.EntityState.DODGE;
	}
}