package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class AttackEndEvent extends PlayerEvent<ServerPlayerPatch> {
	private AttackAnimation animation;
	
	public AttackEndEvent(ServerPlayerPatch playerpatch, AttackAnimation animation) {
		super(playerpatch, false);
		this.animation = animation;
	}
	
	public AttackAnimation getAnimation() {
		return this.animation;
	}
}
