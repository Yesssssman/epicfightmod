package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class ActionEvent extends PlayerEvent<ServerPlayerPatch> {
	private StaticAnimation actionAnimation;
	
	public ActionEvent(ServerPlayerPatch playerdata, StaticAnimation actionAnimation) {
		super(playerdata, false);
		this.actionAnimation = actionAnimation;
	}
	
	public StaticAnimation getAnimation() {
		return this.actionAnimation;
	}
}