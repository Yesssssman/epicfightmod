package yesman.epicfight.api.animation.types;

import java.util.function.Function;

import net.minecraft.world.damagesource.DamageSource;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;

public class EntityState {
	public static class StateFactor<T> implements TypeFlexibleHashMap.TypeKey<T> {
		String name;
		T defaultValue;
		
		public StateFactor(String name, T defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
		}
		
		public T getDefaultVal() {
			return this.defaultValue;
		}
	}
	
	public static final StateFactor<Boolean> TURNING_LOCKED = new StateFactor<>("turningLocked", false);
	public static final StateFactor<Boolean> MOVEMENT_LOCKED = new StateFactor<>("movementLocked", false);
	public static final StateFactor<Boolean> ATTACKING = new StateFactor<>("attacking", false);
	public static final StateFactor<Boolean> CAN_BASIC_ATTACK = new StateFactor<>("canBasicAttack", true);
	public static final StateFactor<Boolean> CAN_SKILL_EXECUTION = new StateFactor<>("canExecuteSkill", true);
	public static final StateFactor<Boolean> INACTION = new StateFactor<>("inaction", false);
	public static final StateFactor<Boolean> HURT = new StateFactor<>("hurt", false);
	public static final StateFactor<Boolean> KNOCKDOWN = new StateFactor<>("knockdown", false);
	public static final StateFactor<Boolean> COUNTER_ATTACKABLE = new StateFactor<>("counterAttackable", false);
	public static final StateFactor<Integer> PHASE_LEVEL = new StateFactor<>("phaseLevel", 0);
	public static final StateFactor<Function<DamageSource, Boolean>> INVULNERABILITY_PREDICATE = new StateFactor<>("invulnerabilityPredicate", (damagesource) -> false);
	
	/**
	 * DEFAULT 			   = new EntityState(false, false, false, true,  true,  false, false, false, false, 0, (damagesource) -> false);
	 * 
	 * PRE_DELAY 		   = new EntityState(true,  true,  false, false, false, true,  false, false, false, 1, (damagesource) -> false);
	 * CONTACT 			   = new EntityState(true,  true,  true,  false, false, true,  false, false, false, 2, (damagesource) -> false);
	 * RECOVERY 		   = new EntityState(true,  true,  false, false, true,  true,  false, false, false, 3, (damagesource) -> false);
	 * CANCELABLE_RECOVERY = new EntityState(false, false, false, true,  true,  true,  false, false, false, 3, (damagesource) -> false);
	 * HIT 				   = new EntityState(true,  true,  false, false, false, true,  true,  false, false, 3, (damagesource) -> false);
	 * KNOCKDOWNED 		   = new EntityState(true,  true,  false, false, false, true,  true,  true,  false, 3, (damagesource) -> {
	 *	if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassInvul()) {
	 *		if (damagesource instanceof ExtendedDamageSource) {
	 *			return !((ExtendedDamageSource)damagesource).isFinisher();
	 *		} else {
	 *			return true;
	 *		}
	 *	}
	 *	return false;
	 * });
	 * DODGE 			   = new EntityState(true, true, false, false, false, true, false, false, false, 3, (damagesource) -> {
	 *	if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassInvul()) {
	 *		return true;
	 *	}
	 *	return false;
	 * });
	 * INVINCIBLE 		   = new EntityState(true, true, false, false, false, true, false, false, false, 0, (damagesource) -> !damagesource.isBypassInvul());
	 *
	 */
	final boolean turningLocked;
	final boolean movementLocked;
	final boolean attacking;
	final boolean canBasicAttack;
	final boolean canSkillExecution;
	final boolean inaction;
	final boolean hurt;
	final boolean knockDown;
	final boolean counterAttackable;
	// free : 0, preDelay : 1, contact : 2, recovery : 3
	final int phaseLevel;
	final Function<DamageSource, Boolean> invulnerabilityChecker;
	
	public static final EntityState DEFAULT = new EntityState(false, false, false, true, true, false, false, false, false, 0, (damagesource) -> false);
	
	EntityState(boolean turningLocked, boolean movementLocked, boolean attacking, boolean basicAttackPossible, boolean skillExecutionPossible,
			boolean inaction, boolean hurt, boolean knockDown, boolean counterAttackable, int phaseLevel, Function<DamageSource, Boolean> invulnerabilityChecker) {
		this.turningLocked = turningLocked;
		this.movementLocked = movementLocked;
		this.attacking = attacking;
		this.canBasicAttack = basicAttackPossible;
		this.canSkillExecution = skillExecutionPossible;
		this.inaction = inaction;
		this.hurt = hurt;
		this.knockDown = knockDown;
		this.counterAttackable = counterAttackable;
		this.phaseLevel = phaseLevel;
		this.invulnerabilityChecker = invulnerabilityChecker;
	}

	public boolean turningLocked() {
		return this.turningLocked;
	}
	
	public boolean movementLocked() {
		return this.movementLocked;
	}
	
	public boolean attacking() {
		return this.attacking;
	}
	
	public boolean invulnerableTo(DamageSource damagesource) {
		return this.invulnerabilityChecker.apply(damagesource);
	}
	
	public boolean canBasicAttack() {
		return this.canBasicAttack;
	}
	
	public boolean canUseSkill() {
		return this.canSkillExecution;
	}
	
	public boolean inaction() {
		return this.inaction;
	}
	
	public boolean hurt() {
		return this.hurt;
	}
	
	public boolean knockDown() {
		return this.knockDown;
	}
	
	public boolean counterAttackable() {
		return this.counterAttackable;
	}
	
	public int getLevel() {
		return this.phaseLevel;
	}
}