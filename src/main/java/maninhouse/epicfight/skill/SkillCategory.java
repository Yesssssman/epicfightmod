package maninhouse.epicfight.skill;

public enum SkillCategory {
	BASIC_ATTACK(0, false, false),
	AIR_ATTACK(1, false, false),
	DODGE(2, true, true),
	PASSIVE(3, true, true),
	WEAPON_PASSIVE(4, false, false),
	WEAPON_SPECIAL_ATTACK(5, false, false),
	GUARD(6, true, true);
	
	int index;
	boolean shouldSave;
	boolean shouldSyncronized;
	
	SkillCategory(int index, boolean shouldSave, boolean shouldSyncronized) {
		this.index = index;
		this.shouldSave = shouldSave;
		this.shouldSyncronized = shouldSyncronized;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public boolean shouldSave() {
		return this.shouldSave;
	}
	
	public boolean shouldSyncronized() {
		return this.shouldSyncronized;
	}
}