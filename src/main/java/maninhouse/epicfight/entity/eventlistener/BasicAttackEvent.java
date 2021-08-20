package maninhouse.epicfight.entity.eventlistener;

import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;

public class BasicAttackEvent extends PlayerEvent<ServerPlayerData> {
	public BasicAttackEvent(ServerPlayerData playerdata) {
		super(playerdata);
	}
}