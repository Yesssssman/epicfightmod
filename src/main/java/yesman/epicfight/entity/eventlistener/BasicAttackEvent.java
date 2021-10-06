package yesman.epicfight.entity.eventlistener;

import yesman.epicfight.capabilities.entity.player.ServerPlayerData;

public class BasicAttackEvent extends PlayerEvent<ServerPlayerData> {
	public BasicAttackEvent(ServerPlayerData playerdata) {
		super(playerdata);
	}
}