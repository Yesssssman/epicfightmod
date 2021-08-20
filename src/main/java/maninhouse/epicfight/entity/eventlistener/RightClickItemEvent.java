package maninhouse.epicfight.entity.eventlistener;

import maninhouse.epicfight.capabilities.entity.player.PlayerData;

public class RightClickItemEvent<T extends PlayerData<?>> extends PlayerEvent<T> {
	public RightClickItemEvent(T playerdata) {
		super(playerdata);
	}
}