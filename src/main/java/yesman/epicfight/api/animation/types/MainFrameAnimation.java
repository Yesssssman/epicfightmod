package yesman.epicfight.api.animation.types;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.animation.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.ActionEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class MainFrameAnimation extends StaticAnimation {
	public MainFrameAnimation(float convertTime, String path, Model model) {
		super(convertTime, false, path, model);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		
		if (entitypatch.isLogicalClient()) {
			entitypatch.getClientAnimator().startInaction();
			entitypatch.getClientAnimator().resetCompositeMotion();
		}
		
		if (entitypatch instanceof ServerPlayerPatch) {
			ServerPlayerPatch playerdata = ((ServerPlayerPatch)entitypatch);
			playerdata.getEventListener().triggerEvents(EventType.ACTION_EVENT, new ActionEvent(playerdata, this));
		}
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		super.tick(entitypatch);
		entitypatch.getOriginal().animationSpeed = 0;
	}
	
	@Override
	public boolean isMainFrameAnimation() {
		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Layer.Priority getPriority() {
		return this.getProperty(ClientAnimationProperties.PRIORITY).orElse(Layer.Priority.HIGHEST);
	}
}