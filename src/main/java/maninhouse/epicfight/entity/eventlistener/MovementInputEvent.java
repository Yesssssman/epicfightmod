package maninhouse.epicfight.entity.eventlistener;

import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import net.minecraft.util.MovementInput;

public class MovementInputEvent extends PlayerEvent<ClientPlayerData> {
	private MovementInput movementInput;
	
	public MovementInputEvent(ClientPlayerData playerdata, MovementInput movementInput) {
		super(playerdata);
	}
	
	public MovementInput getMovementInput() {
		return this.movementInput;
	}
}