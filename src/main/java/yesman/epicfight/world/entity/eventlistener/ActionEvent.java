package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class ActionEvent<T extends PlayerPatch<?>> extends PlayerEvent<T> {
	private StaticAnimation actionAnimation;
	
	@SuppressWarnings("unchecked")
	public ActionEvent(PlayerPatch<?> playerdata, StaticAnimation actionAnimation) {
		super((T)playerdata, false);
		this.actionAnimation = actionAnimation;
	}
	
	public StaticAnimation getAnimation() {
		return this.actionAnimation;
	}
}