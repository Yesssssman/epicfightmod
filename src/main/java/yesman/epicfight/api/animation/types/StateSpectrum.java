package yesman.epicfight.api.animation.types;

import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.damagesource.DamageSource;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;

public class StateSpectrum {
	private final Set<StatesInTime> timePairs = Sets.newHashSet();
	
	void readFrom(StateSpectrum.Blueprint blueprint) {
		this.timePairs.clear();
		this.timePairs.addAll(blueprint.timePairs);
	}
	
	public EntityState bindStates(float time) {
		TypeFlexibleHashMap<StateFactor<?>> stateMap = this.getStateMap(time);
		
		boolean turningLocked = stateMap.getOrDefault(EntityState.TURNING_LOCKED, EntityState.TURNING_LOCKED.getDefaultVal());
		boolean movementLocked = stateMap.getOrDefault(EntityState.MOVEMENT_LOCKED, EntityState.MOVEMENT_LOCKED.getDefaultVal());
		boolean attacking = stateMap.getOrDefault(EntityState.ATTACKING, EntityState.ATTACKING.getDefaultVal());
		boolean canBasicAttack = stateMap.getOrDefault(EntityState.CAN_BASIC_ATTACK, EntityState.CAN_BASIC_ATTACK.getDefaultVal());
		boolean canSkillExecution = stateMap.getOrDefault(EntityState.CAN_SKILL_EXECUTION, EntityState.CAN_SKILL_EXECUTION.getDefaultVal());
		boolean inaction = stateMap.getOrDefault(EntityState.INACTION, EntityState.INACTION.getDefaultVal());
		boolean hurt = stateMap.getOrDefault(EntityState.HURT, EntityState.HURT.getDefaultVal());
		boolean knockdown = stateMap.getOrDefault(EntityState.KNOCKDOWN, EntityState.KNOCKDOWN.getDefaultVal());
		boolean counterAttackable = stateMap.getOrDefault(EntityState.COUNTER_ATTACKABLE, EntityState.COUNTER_ATTACKABLE.getDefaultVal());
		int phaseLevel = stateMap.getOrDefault(EntityState.PHASE_LEVEL, EntityState.PHASE_LEVEL.getDefaultVal());
		Function<DamageSource, Boolean> invulnerabilityPredicate = stateMap.getOrDefault(EntityState.INVULNERABILITY_PREDICATE, EntityState.INVULNERABILITY_PREDICATE.getDefaultVal());
		
		return new EntityState(turningLocked, movementLocked, attacking, canBasicAttack, canSkillExecution, inaction, hurt, knockdown, counterAttackable, phaseLevel, invulnerabilityPredicate);
	}
	
	private TypeFlexibleHashMap<StateFactor<?>> getStateMap(float time) {
		TypeFlexibleHashMap<StateFactor<?>> stateMap = new TypeFlexibleHashMap<>();
		
		for (StatesInTime state : this.timePairs) {
			if (state.start <= time && state.end > time) {
				
				for (Pair<StateFactor<?>, ?> timePair : state.states) {
					stateMap.put(timePair.getFirst(), timePair.getSecond());
				}
			}
		}
		
		return stateMap;
	}
	
	static class StatesInTime {
		float start;
		float end;
		Set<Pair<StateFactor<?>, ?>> states;
		
		public StatesInTime(float start, float end) {
			this.start = start;
			this.end = end;
			this.states = Sets.newHashSet();
		}
		
		public <T> StatesInTime addState(StateFactor<T> factor, T val) {
			this.states.add(Pair.of(factor, val));
			return this;
		}
	}
	
	static class Blueprint {
		StatesInTime currentState;
		Set<StatesInTime> timePairs = Sets.newHashSet();
		
		public Blueprint newTimePair(float start, float end) {
			this.currentState = new StatesInTime(start, end);
			this.timePairs.add(this.currentState);
			return this;
		}
		
		public <T> Blueprint addState(StateFactor<T> factor, T val) {
			this.currentState.states.add(Pair.of(factor, val));
			return this;
		}
		
		public <T> Blueprint addStateRemoveOld(StateFactor<T> factor, T val) {
			for (StatesInTime timePair : this.timePairs) {
				timePair.states.removeIf((pair) -> pair.getFirst().equals(factor));
			}
			
			return this.addState(factor, val);
		}
		
		public Blueprint clear() {
			this.currentState = null;
			this.timePairs.clear();
			return this;
		}
	}
}