package yesman.epicfight.world.entity.eventlistener;

import net.minecraft.world.damagesource.DamageSource;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class DodgeSuccessEvent extends PlayerEvent<ServerPlayerPatch> {
	private final DamageSource damageSource;
	
	public DodgeSuccessEvent(ServerPlayerPatch playerpatch, DamageSource damageSource) {
		super(playerpatch, false);
		
		this.damageSource = damageSource;
	}
	
	public DamageSource getDamageSource() {
		return this.damageSource;
	}
}