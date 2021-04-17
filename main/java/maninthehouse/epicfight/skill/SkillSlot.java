package maninthehouse.epicfight.skill;

public enum SkillSlot
{
	DODGE(0), WEAPON_GIMMICK(1), WEAPON_SPECIAL_ATTACK(2);
	
	int index;
	
	SkillSlot(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return this.index;
	}
}