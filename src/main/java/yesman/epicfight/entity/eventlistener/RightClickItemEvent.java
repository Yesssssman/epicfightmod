package yesman.epicfight.entity.eventlistener;

import yesman.epicfight.capabilities.entity.player.PlayerData;

public class RightClickItemEvent<T extends PlayerData<?>> extends PlayerEvent<T> {
	public RightClickItemEvent(T playerdata) {
		super(playerdata);
	}
}