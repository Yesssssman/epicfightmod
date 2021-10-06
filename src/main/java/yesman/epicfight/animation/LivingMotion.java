package yesman.epicfight.animation;

public enum LivingMotion {
	IDLE, ANGRY, FLOAT, WALK, RUN, SWIM, FLY, SNEAK, KNEEL, FALL, MOUNT, DEATH, CHASE, SPELLCAST, JUMP, CELEBRATE, INACTION,
	DIGGING, ADMIRE, CLIMB, SLEEP, DRINK, NONE, AIM, BLOCK, BLOCK_SHIELD, RELOAD, SHOT;
	
	final int id;
	
	LivingMotion() {
		this.id = Count.LAST_ID++;
	}
	
	public int getId() {
		return id;
	}
	
	static class Count {
		static int LAST_ID = 0;
	}
}