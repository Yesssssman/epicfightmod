package yesman.epicfight.api.animation;

public enum LivingMotions implements LivingMotion {
	INACTION, IDLE, ANGRY, FLOAT, WALK, RUN, SWIM, FLY, SNEAK, KNEEL, FALL, SIT, MOUNT, DEATH, CHASE, SPELLCAST, JUMP, CELEBRATE, LANDING_RECOVERY, CREATIVE_FLY, CREATIVE_IDLE, // Base
	DIGGING, ADMIRE, CLIMB, SLEEP, DRINK, EAT, NONE, AIM, BLOCK, BLOCK_SHIELD, RELOAD, SHOT, SPECTATE;	// Mix
	
	final int id;
	
	LivingMotions() {
		this.id = LivingMotion.ENUM_MANAGER.assign(this);
	}
	
	public int universalOrdinal() {
		return this.id;
	}
}