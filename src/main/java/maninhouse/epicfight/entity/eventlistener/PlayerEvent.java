package maninhouse.epicfight.entity.eventlistener;

import maninhouse.epicfight.capabilities.entity.player.PlayerData;

public class PlayerEvent<T extends PlayerData<?>> {
	private T playerdata;
	
	public PlayerEvent(T playerdata) {
		this.playerdata = playerdata;
	}
	
	public T getPlayerData() {
		return this.playerdata;
	}
}