package yesman.epicfight.api.client.forgeevent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@OnlyIn(Dist.CLIENT)
public abstract class UpdatePlayerMotionEvent extends Event {
	private final AbstractClientPlayerPatch<?> playerpatch;
	private final LivingMotion motion;
	
	public UpdatePlayerMotionEvent(AbstractClientPlayerPatch<?> playerpatch, LivingMotion motion) {
		this.playerpatch = playerpatch;
		this.motion = motion;
	}
	
	public AbstractClientPlayerPatch<?> getPlayerPatch() {
		return this.playerpatch;
	}
	
	public LivingMotion getMotion() {
		return this.motion;
	}
	
	public static class BaseLayer extends UpdatePlayerMotionEvent {
		public BaseLayer(AbstractClientPlayerPatch<?> playerpatch, LivingMotion motion) {
			super(playerpatch, motion);
		}
	}
	
	public static class CompositeLayer extends UpdatePlayerMotionEvent {
		public CompositeLayer(AbstractClientPlayerPatch<?> playerpatch, LivingMotion motion) {
			super(playerpatch, motion);
		}
	}
}