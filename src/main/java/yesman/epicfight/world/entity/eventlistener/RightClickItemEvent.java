package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class RightClickItemEvent<T extends PlayerPatch<?>> extends PlayerEvent<T> {
	public RightClickItemEvent(T playerpatch) {
		super(playerpatch, true);
	}
}