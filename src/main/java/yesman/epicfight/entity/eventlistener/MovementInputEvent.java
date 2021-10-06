package yesman.epicfight.entity.eventlistener;

import net.minecraft.util.MovementInput;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;

public class MovementInputEvent extends PlayerEvent<ClientPlayerData> {
	private MovementInput movementInput;
	
	public MovementInputEvent(ClientPlayerData playerdata, MovementInput movementInput) {
		super(playerdata);
	}
	
	public MovementInput getMovementInput() {
		return this.movementInput;
	}
}