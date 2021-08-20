package maninhouse.epicfight.entity.eventlistener;

import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

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