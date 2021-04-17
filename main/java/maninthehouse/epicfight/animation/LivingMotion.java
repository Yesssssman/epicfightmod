package maninthehouse.epicfight.animation;

public enum LivingMotion
{
	IDLE(0), ANGRY(1), FLOATING(2), BENDING(3), WALKING(4), RUNNING(5), SWIMMING(6), FLYING(7), SNEAKING(8), KNEELING(9),
	FALL(10), MOUNT(11), DEATH(12), CHASING(13), SPELLCASTING(14), JUMPING(15), CELEBRATE(16), ADMIRE(17),
	NONE(18), AIMING(19), BLOCKING(20), RELOADING(21), SHOTING(22), THROW_JAVELIN(23);
	
	final int id;
	
	LivingMotion(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
}