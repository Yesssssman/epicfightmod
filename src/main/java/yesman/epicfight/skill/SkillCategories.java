package yesman.epicfight.skill;

public enum SkillCategories implements SkillCategory {
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
	int id;
	
	SkillCategories(boolean shouldSave, boolean shouldSyncronized, boolean modifiable) {
		this.shouldSaved = shouldSave;
		this.shouldSyncronized = shouldSyncronized;
		this.modifiable = modifiable;
		this.id = SkillCategory.ASSIGNMENT_MANAGER.assign(this);
	}
	
	public boolean shouldSaved() {
		return this.shouldSaved;
	}
	
	public boolean shouldSynchronized() {
		return this.shouldSyncronized;
	}
	
	public boolean learnable() {
		return this.modifiable;
	}

	@Override
	public int universalOrdinal() {
		return this.id;
	}
}