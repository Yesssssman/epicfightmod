package yesman.epicfight.skill;

import yesman.epicfight.api.utils.ExtendableEnum;
import yesman.epicfight.api.utils.ExtendableEnumManager;

public interface SkillSlot extends ExtendableEnum {
	public static final ExtendableEnumManager<SkillSlot> ENUM_MANAGER = new ExtendableEnumManager<> ("skill_slot");
	
	public SkillCategory category();
}