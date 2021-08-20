package maninhouse.epicfight.entity.eventlistener;

import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;

public class ItemUseEndEvent extends PlayerEvent<ServerPlayerData> {
	public ItemUseEndEvent(ServerPlayerData playerdata) {
		super(playerdata);
	}
}