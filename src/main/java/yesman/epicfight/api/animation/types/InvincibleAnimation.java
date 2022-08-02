package yesman.epicfight.api.animation.types;

import yesman.epicfight.api.model.Model;

public class InvincibleAnimation extends ActionAnimation {
	public InvincibleAnimation(float convertTime, String path, Model model) {
		super(convertTime, path, model);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.TURNING_LOCKED, true)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.CAN_BASIC_ATTACK, false)
			.addState(EntityState.CAN_SKILL_EXECUTION, true)
			.addState(EntityState.INACTION, true)
			.addState(EntityState.INVULNERABILITY_PREDICATE, (damagesource) -> !damagesource.isBypassInvul());
	}
}