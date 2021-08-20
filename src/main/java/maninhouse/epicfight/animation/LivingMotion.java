package maninhouse.epicfight.animation;

public enum LivingMotion {
	IDLE, ANGRY, FLOAT, WALK, RUN, SWIM, FLY, SNEAK, KNEEL, FALL, MOUNT, DEATH, CHASE, SPELLCAST, JUMP, CELEBRATE,
	ADMIRE, CLIMB, SLEEP, DRINK, NONE, AIM, BLOCK, RELOAD, SHOT;
	
	final int id;
	
	LivingMotion() {
		this.id = Cls.LAST_ID++;
	}
	
	public int getId() {
		return id;
	}
	
	static class Cls {
		static int LAST_ID = 0;
	}
}