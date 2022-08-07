package yesman.epicfight.api.animation;

public enum LivingMotions implements LivingMotion {
	IDLE, ANGRY, FLOAT, WALK, RUN, SWIM, FLY, SNEAK, KNEEL, FALL, MOUNT, DEATH, CHASE, SPELLCAST, JUMP, CELEBRATE,
	DIGGING, ADMIRE, CLIMB, SLEEP, DRINK, NONE, AIM, BLOCK, BLOCK_SHIELD, RELOAD, SHOT;
	
	final int id;
	
	LivingMotions() {
		this.id = LivingMotion.ENUM_MANAGER.assign(this);
	}
	
	public int universalOrdinal() {
		return id;
	}
}