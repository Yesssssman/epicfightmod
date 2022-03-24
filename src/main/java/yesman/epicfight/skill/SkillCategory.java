package yesman.epicfight.skill;

public enum SkillCategory {
	BASIC_ATTACK(false, false, false),
	AIR_ATTACK(false, false, false),
	DODGE(true, true, true),
	PASSIVE(true, true, true),
	WEAPON_PASSIVE(false, false, false),
	WEAPON_SPECIAL_ATTACK(false, true, false),
	GUARD(true, true, true),
	KNOCKDOWN_WAKEUP(false, false, false);
	
	boolean shouldSaved;
	boolean shouldSyncronized;
	boolean modifiable;
	
	SkillCategory(boolean shouldSave, boolean shouldSyncronized, boolean shownInEditor) {
		this.shouldSaved = shouldSave;
		this.shouldSyncronized = shouldSyncronized;
		this.modifiable = shownInEditor;
	}
	
	public int getIndex() {
		return this.ordinal();
	}
	
	public boolean shouldSaved() {
		return this.shouldSaved;
	}
	
	public boolean shouldSyncronized() {
		return this.shouldSyncronized;
	}
	
	public boolean modifiable() {
		return this.modifiable;
	}
}