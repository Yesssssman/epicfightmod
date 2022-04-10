package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class ItemUseEndEvent extends PlayerEvent<ServerPlayerPatch> {
	public ItemUseEndEvent(ServerPlayerPatch playerpatch) {
		super(playerpatch, true);
	}
}