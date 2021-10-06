package yesman.epicfight.skill;

public enum SkillCategory {
	BASIC_ATTACK(false, false),
	AIR_ATTACK(false, false),
	DODGE(true, true),
	PASSIVE(true, true),
	WEAPON_PASSIVE(false, false),
	WEAPON_SPECIAL_ATTACK(false, false),
	GUARD(true, true);
	
	int index;
	boolean shouldSave;
	boolean shouldSyncronized;
	
	SkillCategory(boolean shouldSave, boolean shouldSyncronized) {
		this.index = Count.LAST_ID++;
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
	
	static class Count {
		static int LAST_ID = 0;
	}
}