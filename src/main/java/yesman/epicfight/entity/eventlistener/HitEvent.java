package yesman.epicfight.entity.eventlistener;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;

public class HitEvent extends PlayerEvent<ServerPlayerData> {
	private final LivingAttackEvent forgeEvent;
	
	public HitEvent(ServerPlayerData playerdata, LivingAttackEvent event) {
		super(playerdata);
		this.forgeEvent = event;
	}
	
	public LivingAttackEvent getForgeEvent() {
		return this.forgeEvent;
	}
}