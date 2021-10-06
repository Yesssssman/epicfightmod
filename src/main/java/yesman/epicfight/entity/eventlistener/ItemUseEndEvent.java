package yesman.epicfight.entity.eventlistener;

import yesman.epicfight.capabilities.entity.player.ServerPlayerData;

public class ItemUseEndEvent extends PlayerEvent<ServerPlayerData> {
	public ItemUseEndEvent(ServerPlayerData playerdata) {
		super(playerdata);
	}
}