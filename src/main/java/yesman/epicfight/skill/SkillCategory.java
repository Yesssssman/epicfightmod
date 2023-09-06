package yesman.epicfight.skill;

import yesman.epicfight.api.utils.ExtendableEnum;
import yesman.epicfight.api.utils.ExtendableEnumManager;

public interface SkillCategory extends ExtendableEnum {
	ExtendableEnumManager<SkillCategory> ENUM_MANAGER = new ExtendableEnumManager<> ("skill_category");
	
	boolean shouldSave();
	
	boolean shouldSynchronize();
	
	boolean learnable();
}