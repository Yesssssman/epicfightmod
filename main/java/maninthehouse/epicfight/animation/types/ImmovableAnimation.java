package maninthehouse.epicfight.animation.types;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.client.CTSRotatePlayerYaw;

public class ImmovableAnimation extends StaticAnimation
{
	public ImmovableAnimation(int id, float convertTime, String path)
	{
		super(id, convertTime, false, path);
	}
	
	public ImmovableAnimation(int id, float convertTime, boolean isRepeat, String path)
	{
		super(id, convertTime, isRepeat, path);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata)
	{
		super.onActivate(entitydata);
		
		if(entitydata.isRemote())
		{
			entitydata.getClientAnimator().resetMotion();
			entitydata.getClientAnimator().resetMixMotion();
			entitydata.getClientAnimator().offMixLayer(true);
			entitydata.currentMotion = LivingMotion.IDLE;
			entitydata.currentMixMotion = LivingMotion.NONE;
		}
		
		entitydata.cancelUsingItem();
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd)
	{
		super.onFinish(entitydata, isEnd);
		
		if(entitydata.isRemote() && entitydata instanceof ClientPlayerData)
	    {
			((ClientPlayerData)entitydata).changeYaw(0);
			ModNetworkManager.sendToServer(new CTSRotatePlayerYaw(0));
	    }
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		return LivingData.EntityState.PRE_DELAY;
	}
}