package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class PlayerEvent<T extends PlayerPatch<?>> {
	private T playerpatch;
	private final boolean cancelable;
	private boolean canceled;
	
	public PlayerEvent(T playerpatch, boolean cancelable) {
		this.playerpatch = playerpatch;
		this.cancelable = cancelable;
	}
	
	public T getPlayerPatch() {
		return this.playerpatch;
	}
	
	public void setCanceled(boolean canceled) {
		if (!this.cancelable) {
			throw new UnsupportedOperationException(String.format("Event %s is not cancelable.", this.toString()));
		}
		
		this.canceled = canceled;
	}
	
	public boolean isCanceled() {
		return this.cancelable ? this.canceled : false;
	}
	
	@Override
	public String toString() {
		return this.getClass().toString();
	}
}