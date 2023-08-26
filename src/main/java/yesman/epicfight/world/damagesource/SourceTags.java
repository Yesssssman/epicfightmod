package yesman.epicfight.world.damagesource;

public enum SourceTags implements SourceTag {
	/**
	 * Decides if damage source can hurt the entity that is lying on ground
	 */
	FINISHER,
	
	/**
	 * Decides if damage source can neutralize the entity that is invulnerable
	 */
	COUNTER,

	/**
	 * Decides if damage source type is execution
	 */
	EXECUTION,
	
	/**
	 * Decides if damage source is weapon innate attack
	 */
	WEAPON_INNATE,
	
	/**
	 * Decides if damage source can ignore guard
	 */
	GUARD_PUNCTURE;
	
	final int id;
	
	SourceTags() {
		this.id = SourceTag.ENUM_MANAGER.assign(this);
	}
	
	public int universalOrdinal() {
		return id;
	}
}