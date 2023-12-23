package yesman.epicfight.api.animation.types;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.JointMask;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

public class BasicAttackAnimation extends AttackAnimation {
	public BasicAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		this(convertTime, antic, antic, contact, recovery, collider, colliderJoint, path, armature);
	}
	
	public BasicAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
		this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false);
		this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
	}
	
	public BasicAttackAnimation(float convertTime, float antic, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		super(convertTime, antic, antic, contact, recovery, hand, collider, colliderJoint, path, armature);
		this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false);
		this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
	}
	
	public BasicAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
		super(convertTime, path, armature, phases);
		this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false);
		this.addProperty(StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
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
			.newTimePair(phase.start, phase.recovery)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.UPDATE_LIVING_MOTION, false)
			.addState(EntityState.CAN_BASIC_ATTACK, false)
			.newTimePair(phase.start, phase.end)
			.addState(EntityState.INACTION, true)
			.newTimePair(preDelay, phase.contact + 0.01F)
			.addState(EntityState.ATTACKING, true)
			.addState(EntityState.PHASE_LEVEL, 2)
			.newTimePair(phase.contact + 0.01F, phase.end)
			.addState(EntityState.PHASE_LEVEL, 3)
			.addState(EntityState.TURNING_LOCKED, true);
	}
	
	@Override
	protected void onLoaded() {
		super.onLoaded();
		
		if (!this.properties.containsKey(AttackAnimationProperty.BASIS_ATTACK_SPEED)) {
			float basisSpeed = Float.parseFloat(String.format(Locale.US, "%.2f", (1.0F / this.totalTime)));
			this.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, basisSpeed);
		}
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		super.end(entitypatch, nextAnimation, isEnd);
		
		boolean stiffAttack = entitypatch.getOriginal().level().getGameRules().getRule(EpicFightGamerules.STIFF_COMBO_ATTACKS).get();
		
		if (!isEnd && !nextAnimation.isMainFrameAnimation() && entitypatch.isLogicalClient() && !stiffAttack) {
			float playbackSpeed = EpicFightOptions.A_TICK * this.getPlaySpeed(entitypatch);
			entitypatch.getClientAnimator().baseLayer.copyLayerTo(entitypatch.getClientAnimator().baseLayer.getLayer(Layer.Priority.HIGHEST), playbackSpeed);
		}
	}
	
	@Override
	public TypeFlexibleHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
		TypeFlexibleHashMap<StateFactor<?>> stateMap = super.getStatesMap(entitypatch, time);
		
		if (!entitypatch.getOriginal().level().getGameRules().getRule(EpicFightGamerules.STIFF_COMBO_ATTACKS).get()) {
			stateMap.put(EntityState.MOVEMENT_LOCKED, (Object)false);
			stateMap.put(EntityState.UPDATE_LIVING_MOTION, (Object)true);
		}
		
		return stateMap;
	}
	
	@Override
	protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, DynamicAnimation dynamicAnimation) {
		Vec3 vec3 = super.getCoordVector(entitypatch, dynamicAnimation);
		
		if (entitypatch.shouldBlockMoving() && this.getProperty(ActionAnimationProperty.CANCELABLE_MOVE).orElse(false)) {
			vec3 = vec3.scale(0.0F);
		}
		
		return vec3;
	}
	
	@Override
	public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		if (layer == Layer.Priority.HIGHEST) {
			return !JointMaskEntry.BASIC_ATTACK_MASK.isMasked(entitypatch.getCurrentLivingMotion(), joint);
		} else {
			return super.isJointEnabled(entitypatch, layer, joint);
		}
	}
	
	@Override
	public BindModifier getBindModifier(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		if (layer == Layer.Priority.HIGHEST) {
			List<JointMask> list = JointMaskEntry.BIPED_UPPER_JOINTS_WITH_ROOT;
			int position = list.indexOf(JointMask.of(joint));
			
			if (position >= 0) {
				return list.get(position).getBindModifier();
			} else {
				return null;
			}
		} else {
			return super.getBindModifier(entitypatch, layer, joint);
		}
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return true;
	}
	
	@Override
	public boolean shouldPlayerMove(LocalPlayerPatch playerpatch) {
		if (playerpatch.isLogicalClient()) {
			if (!playerpatch.getOriginal().level().getGameRules().getRule(EpicFightGamerules.STIFF_COMBO_ATTACKS).get()) {
				if (playerpatch.getOriginal().input.forwardImpulse != 0.0F || playerpatch.getOriginal().input.leftImpulse != 0.0F) {
					return false;
				}
			}
		}
		
		return true;
	}
}