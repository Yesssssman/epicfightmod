package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class ComboCounterHandleEvent extends PlayerEvent<ServerPlayerPatch> {
	private final ComboCounterHandleEvent.Causal causal;
	private final StaticAnimation animation;
	private final int prevValue;
	private int nextValue;
	
	public ComboCounterHandleEvent(ComboCounterHandleEvent.Causal causal, ServerPlayerPatch playerpatch, StaticAnimation animation, int prevValue, int nextValue) {
		super(playerpatch, true);
		
		this.causal = causal;
		this.animation = animation;
		this.prevValue = prevValue;
		this.nextValue = nextValue;
	}
	
	public ComboCounterHandleEvent.Causal getCausal() {
		return this.causal;
	}
	
	public StaticAnimation getAnimation() {
		return this.animation;
	}
	
	public int getPrevValue() {
		return this.prevValue;
	}
	
	public int getNextValue() {
		return this.nextValue;
	}
	
	public void setNextValue(int nextValue) {
		this.nextValue = nextValue;
	}
	
	public static enum Causal {
		BASIC_ATTACK_COUNT, ACTION_ANIMATION_RESET, TIME_EXPIRED_RESET
	}
}