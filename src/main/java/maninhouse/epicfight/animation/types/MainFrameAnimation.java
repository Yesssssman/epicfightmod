package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.client.animation.ClientAnimationProperty;
import maninhouse.epicfight.client.animation.Layer;
import maninhouse.epicfight.entity.eventlistener.ActionEvent;
import maninhouse.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MainFrameAnimation extends StaticAnimation {
	public MainFrameAnimation(int id, float convertTime, String path) {
		super(id, convertTime, false, path);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata) {
		super.onActivate(entitydata);
		
		if (entitydata.isRemote()) {
			entitydata.getClientAnimator().getMainFrameLayer().off(entitydata);
			entitydata.getClientAnimator().resetMotion();
			entitydata.getClientAnimator().resetOverridenMotion();
		}
		
		if (entitydata instanceof ServerPlayerData) {
			ServerPlayerData playerdata = ((ServerPlayerData)entitydata);
			playerdata.getEventListener().activateEvents(EventType.ACTION_EVENT, new ActionEvent(playerdata, this));
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		super.onUpdate(entitydata);
		entitydata.getOriginalEntity().limbSwingAmount = 0;
	}
	
	@Override
	public boolean isMainFrameAnimation() {
		return true;
	}
	
	@Override
	public EntityState getState(float time) {
		return EntityState.PRE_DELAY;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Layer.Priority getPriority() {
		return this.getProperty(ClientAnimationProperty.PRIORITY).orElse(Layer.Priority.HIGHEST);
	}
}