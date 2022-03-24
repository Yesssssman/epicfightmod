package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class BasicAttackEvent extends PlayerEvent<ServerPlayerPatch> {
	public BasicAttackEvent(ServerPlayerPatch playerpatch) {
		super(playerpatch, true);
	}
}