package yesman.epicfight.animation.types;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.client.animation.ClientAnimationProperties;
import yesman.epicfight.client.animation.Layer;
import yesman.epicfight.entity.eventlistener.ActionEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.model.Model;

public class MainFrameAnimation extends StaticAnimation {
	public MainFrameAnimation(float convertTime, String path, Model model) {
		super(convertTime, false, path, model);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata) {
		super.onActivate(entitydata);
		
		if (entitydata.isRemote()) {
			entitydata.getClientAnimator().getMainFrameLayer().off(entitydata);
			entitydata.getClientAnimator().switchToInaction();
			entitydata.getClientAnimator().resetOverwritingMotion();
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
		return this.getProperty(ClientAnimationProperties.PRIORITY).orElse(Layer.Priority.HIGHEST);
	}
}