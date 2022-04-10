package yesman.epicfight.api.animation.types;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import yesman.epicfight.api.utils.game.ExtendedDamageSource;

public class EntityState {
	public static final EntityState FREE = new EntityState(false, false, false, true, true, false, false, 0, (damagesource) -> false);
	public static final EntityState FREE_CAMERA = new EntityState(false, true, false, false, false, true, false, 1, (damagesource) -> false);
	public static final EntityState ROTATABLE_PRE_DELAY = new EntityState(false, true, false, false, false, true, false, 1, (damagesource) -> false);
	public static final EntityState PRE_DELAY = new EntityState(true, true, false, false, false, true, false, 1, (damagesource) -> false);
	public static final EntityState ROTATABLE_CONTACT = new EntityState(false, true, true, false, false, true, false, 2, (damagesource) -> false);
	public static final EntityState CONTACT = new EntityState(true, true, true, false, false, true, false, 2, (damagesource) -> false);
	public static final EntityState ROTATABLE_RECOVERY = new EntityState(false, true, false, false, true, true, false, 3, (damagesource) -> false);
	public static final EntityState RECOVERY = new EntityState(true, true, false, false, true, true, false, 3, (damagesource) -> false);
	public static final EntityState CANCELABLE_RECOVERY = new EntityState(false, false, false, true, true, true, false, 3, (damagesource) -> false);
	public static final EntityState HIT = new EntityState(true, true, false, false, false, true, true, 3, (damagesource) -> false);
	public static final EntityState KNOCKDOWN = new EntityState(true, true, false, false, false, true, true, 3, (damagesource) -> {
		if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassInvul()) {
			if (damagesource instanceof ExtendedDamageSource) {
				return !((ExtendedDamageSource)damagesource).isFinisher();
			} else {
				return true;
			}
		}
		return false;
	});
	public static final EntityState DODGE = new EntityState(true, true, false, false, false, true, false, 3, (damagesource) -> {
		if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassInvul()) {
			return true;
		}
		return false;
	});
	
	public static final EntityState INVINCIBLE = new EntityState(true, true, false, false, false, true, false, 0, (damagesource) -> !damagesource.isBypassInvul());
	
	static final Map<EntityState, Map<Translation, EntityState>> TRANSLATION_MAP = Maps.<EntityState, Map<Translation, EntityState>>newHashMap();
	
	static {
		TRANSLATION_MAP.put(PRE_DELAY, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_ROTATABLE, ROTATABLE_PRE_DELAY));
		TRANSLATION_MAP.put(ROTATABLE_PRE_DELAY, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_LOCKED, PRE_DELAY));
		TRANSLATION_MAP.put(CONTACT, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_ROTATABLE, ROTATABLE_CONTACT));
		TRANSLATION_MAP.put(ROTATABLE_CONTACT, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_LOCKED, CONTACT));
		TRANSLATION_MAP.put(RECOVERY, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_ROTATABLE, ROTATABLE_RECOVERY));
		TRANSLATION_MAP.put(ROTATABLE_RECOVERY, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_LOCKED, RECOVERY));
	}
	
	public static EntityState translation(EntityState state, Translation translation) {
		return TRANSLATION_MAP.getOrDefault(state, ImmutableMap.<EntityState.Translation, EntityState>of()).getOrDefault(translation, state);
	}
	
	public static enum Translation {
		TO_LOCKED, TO_ROTATABLE
	}
	
	final boolean cameraLock;
	final boolean movementLock;
	final boolean attacking;
	final boolean canBasicAttack;
	final boolean canSkillExecution;
	final boolean inaction;
	final boolean hurt;
	// free : 0, preDelay : 1, contact : 2, recovery : 3
	final int phaseLevel;
	final Function<DamageSource, Boolean> invulnerabilityChecker;
	
	private EntityState(boolean cameraLock, boolean movementLock, boolean attacking, boolean basicAttackPossible, boolean skillExecutionPossible, boolean inaction, boolean hurt, int phaseLevel, Function<DamageSource, Boolean> invulnerabilityChecker) {
		this.cameraLock = cameraLock;
		this.movementLock = movementLock;
		this.attacking = attacking;
		this.canBasicAttack = basicAttackPossible;
		this.canSkillExecution = skillExecutionPossible;
		this.inaction = inaction;
		this.hurt = hurt;
		this.phaseLevel = phaseLevel;
		this.invulnerabilityChecker = invulnerabilityChecker;
	}
	
	public boolean cameraRotationLocked() {
		return this.cameraLock;
	}
	
	public boolean movementLocked() {
		return this.movementLock;
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
	
	public int getLevel() {
		return this.phaseLevel;
	}
}