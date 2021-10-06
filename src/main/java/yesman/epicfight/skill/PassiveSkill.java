package yesman.epicfight.skill;

public abstract class PassiveSkill extends Skill {
	public PassiveSkill(String skillName) {
		super(SkillCategory.PASSIVE, 0, 0, 0, false, ActivateType.PASSIVE, Resource.NONE, skillName);
	}
}