package yesman.epicfight.world.entity.eventlistener;

import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class FallEvent extends PlayerEvent<PlayerPatch<?>> {
	private final PlayerFlyableFallEvent forgeEvent;
	
	public FallEvent(PlayerPatch<?> playerpatch, PlayerFlyableFallEvent forgeEvent) {
		super(playerpatch, false);
		this.forgeEvent = forgeEvent;
	}

	public PlayerFlyableFallEvent getForgeEvent() {
		return forgeEvent;
	}
}