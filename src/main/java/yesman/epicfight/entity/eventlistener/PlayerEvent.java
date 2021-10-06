package yesman.epicfight.entity.eventlistener;

import yesman.epicfight.capabilities.entity.player.PlayerData;

public class PlayerEvent<T extends PlayerData<?>> {
	private T playerdata;
	
	public PlayerEvent(T playerdata) {
		this.playerdata = playerdata;
	}
	
	public T getPlayerData() {
		return this.playerdata;
	}
}