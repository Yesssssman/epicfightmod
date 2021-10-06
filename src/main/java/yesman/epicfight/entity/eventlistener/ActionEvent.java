package yesman.epicfight.entity.eventlistener;

import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;

public class ActionEvent extends PlayerEvent<ServerPlayerData> {
	private StaticAnimation actionAnimation;
	
	public ActionEvent(ServerPlayerData playerdata, StaticAnimation actionAnimation) {
		super(playerdata);
		this.actionAnimation = actionAnimation;
	}
	
	public StaticAnimation getAnimation() {
		return this.actionAnimation;
	}
}