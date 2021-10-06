package yesman.epicfight.animation.types;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class EntityState {
	public static final EntityState FREE = new EntityState(false, false, false, false, true, true, false, 0);
	public static final EntityState FREE_CAMERA = new EntityState(false, true, false, false, false, false, true, 1);
	public static final EntityState ROTATABLE_PRE_DELAY = new EntityState(false, true, false, false, false, false, true, 1);
	public static final EntityState PRE_DELAY = new EntityState(true, true, false, false, false, false, true, 1);
	public static final EntityState ROTATABLE_CONTACT = new EntityState(false, true, true, false, false, false, true, 2);
	public static final EntityState CONTACT = new EntityState(true, true, true, false, false, false, true, 2);
	public static final EntityState ROTATABLE_POST_DELAY = new EntityState(false, true, false, false, false, true, true, 3);
	public static final EntityState POST_DELAY = new EntityState(true, true, false, false, false, true, true, 3);
	public static final EntityState CANCELABLE_POST_DELAY = new EntityState(false, false, false, false,  true, true, false, 3);
	public static final EntityState HIT = new EntityState(true, true, false, false, false, false, true, 3);
	public static final EntityState DODGE = new EntityState(true, true, false, true, false, false, true, 3);
	
	static final Map<EntityState, Map<Translation, EntityState>> TRANSLATION_MAP = Maps.<EntityState, Map<Translation, EntityState>>newHashMap();
	
	static {
		TRANSLATION_MAP.put(PRE_DELAY, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_ROTATABLE, ROTATABLE_PRE_DELAY));
		TRANSLATION_MAP.put(ROTATABLE_PRE_DELAY, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_LOCKED, PRE_DELAY));
		TRANSLATION_MAP.put(CONTACT, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_ROTATABLE, ROTATABLE_CONTACT));
		TRANSLATION_MAP.put(ROTATABLE_CONTACT, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_LOCKED, CONTACT));
		TRANSLATION_MAP.put(POST_DELAY, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_ROTATABLE, ROTATABLE_POST_DELAY));
		TRANSLATION_MAP.put(ROTATABLE_POST_DELAY, ImmutableMap.<EntityState.Translation, EntityState>of(Translation.TO_LOCKED, POST_DELAY));
	}
	
	public static EntityState translation(EntityState state, Translation translation) {
		return TRANSLATION_MAP.getOrDefault(state, ImmutableMap.<EntityState.Translation, EntityState>of()).getOrDefault(translation, state);
	}
	
	public static enum Translation {
		TO_LOCKED, TO_ROTATABLE
	}
	
	final boolean cameraLock;
	final boolean movementLock;
	final boolean collideDetection;
	final boolean invincible;
	final boolean canExecuteSkill;
	final boolean canBasicAttack;
	final boolean inaction;
	// free : 0, preDelay : 1, contact : 2, recovery : 3
	final int phaseLevel;
	
	private EntityState(boolean cameraLock, boolean movementLock, boolean collideDetection, boolean invincible, boolean canBasicAttack, boolean canExecuteSkill,
			boolean overrideLivingMotion, int phaseLevel) {
		this.cameraLock = cameraLock;
		this.movementLock = movementLock;
		this.collideDetection = collideDetection;
		this.invincible = invincible;
		this.canBasicAttack = canBasicAttack;
		this.canExecuteSkill = canExecuteSkill;
		this.phaseLevel = phaseLevel;
		this.inaction = overrideLivingMotion;
	}
	
	public boolean isCameraRotationLocked() {
		return this.cameraLock;
	}
	
	public boolean isMovementLocked() {
		return this.movementLock;
	}
	
	public boolean shouldDetectCollision() {
		return this.collideDetection;
	}
	
	public boolean isInvincible() {
		return this.invincible;
	}
	
	public boolean canBasicAttack() {
		return this.canBasicAttack;
	}
	
	public boolean canExecuteSkill() {
		return this.canExecuteSkill;
	}
	
	public boolean isInaction() {
		return this.inaction;
	}
	
	public int getLevel() {
		return this.phaseLevel;
	}
}