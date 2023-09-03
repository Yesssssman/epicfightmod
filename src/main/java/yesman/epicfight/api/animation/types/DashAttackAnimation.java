package yesman.epicfight.api.animation.types;

import javax.annotation.Nullable;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;

public class DashAttackAnimation extends AttackAnimation {
	public DashAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		this(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature, false);
	}
	
	public DashAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature, boolean directional) {
		super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
		
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
		this.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.333F));
		
		if (directional) {
			this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
		}
	}
	
	public DashAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
		super(convertTime, path, armature, phases);
		
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
		this.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.333F));
	}
	
	@Override
	protected void bindPhaseState(Phase phase) {
		float preDelay = phase.preDelay;
		
		if (preDelay == 0.0F) {
			preDelay += 0.01F;
		}
		
		this.stateSpectrumBlueprint
			.newTimePair(phase.start, preDelay)
			.addState(EntityState.PHASE_LEVEL, 1)
			.newTimePair(phase.start, phase.contact + 0.01F)
			.addState(EntityState.CAN_SKILL_EXECUTION, false)
			.addState(EntityState.CAN_BASIC_ATTACK, false)
			.newTimePair(phase.start, phase.recovery)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.newTimePair(phase.start, phase.end)
			.addState(EntityState.INACTION, true)
			.newTimePair(phase.antic, phase.end)
			.addState(EntityState.TURNING_LOCKED, true)
			.newTimePair(preDelay, phase.contact + 0.01F)
			.addState(EntityState.ATTACKING, true)
			.addState(EntityState.PHASE_LEVEL, 2)
			.newConditionalTimePair((entitypatch) -> (entitypatch.isLastAttackSuccess() ? 1 : 0 ), phase.contact + 0.01F, phase.recovery)
			.addConditionalState(0, EntityState.CAN_BASIC_ATTACK, false)
			.addConditionalState(1, EntityState.CAN_BASIC_ATTACK, true)
			.newTimePair(phase.contact + 0.01F, phase.end)
			.addState(EntityState.PHASE_LEVEL, 3)
			;
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return true;
	}
}