package yesman.epicfight.entity.eventlistener;

import net.minecraftforge.event.entity.living.LivingDamageEvent;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;

public class TakeDamageEvent extends PlayerEvent<ServerPlayerData> {
	private final LivingDamageEvent forgeEvent;
	
	public TakeDamageEvent(ServerPlayerData playerdata, LivingDamageEvent forgeEvent) {
		super(playerdata);
		this.forgeEvent = forgeEvent;
	}
	
	public LivingDamageEvent getForgeEvent() {
		return this.forgeEvent;
	}
}