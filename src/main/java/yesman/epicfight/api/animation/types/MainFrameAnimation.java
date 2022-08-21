package yesman.epicfight.api.animation.types;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.animation.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.ActionEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class MainFrameAnimation extends StaticAnimation {
	public MainFrameAnimation(float convertTime, String path, Model model) {
		super(convertTime, false, path, model);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		
		entitypatch.updateEntityState();
		
		if (entitypatch.isLogicalClient()) {
			entitypatch.getClientAnimator().resetMotion();
			entitypatch.getClientAnimator().resetCompositeMotion();
			entitypatch.getClientAnimator().getPlayerFor(this).setReversed(false);
		}
		
		if (entitypatch.getOriginal() instanceof Player) {
			PlayerPatch<?> playerpatch = (PlayerPatch<?>)entitypatch;
			
			if (playerpatch.isLogicalClient()) {
				if (playerpatch.getOriginal().isLocalPlayer()) {
					playerpatch.getEventListener().triggerEvents(EventType.ACTION_EVENT_CLIENT, new ActionEvent<>(playerpatch, this));
				}
			} else {
				playerpatch.getEventListener().triggerEvents(EventType.ACTION_EVENT_SERVER, new ActionEvent<>(playerpatch, this));
			}
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