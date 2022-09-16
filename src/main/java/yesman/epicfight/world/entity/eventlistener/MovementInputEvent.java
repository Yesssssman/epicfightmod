package yesman.epicfight.world.entity.eventlistener;

import net.minecraft.util.MovementInput;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

public class MovementInputEvent extends PlayerEvent<LocalPlayerPatch> {
	private MovementInput movementInput;
	
	public MovementInputEvent(LocalPlayerPatch playerpatch, MovementInput movementInput) {
		super(playerpatch, false);
		this.movementInput = movementInput;
	}
	
	public MovementInput getMovementInput() {
		return this.movementInput;
	}
}