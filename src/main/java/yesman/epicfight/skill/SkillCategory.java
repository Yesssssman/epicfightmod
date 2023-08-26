package yesman.epicfight.skill;

import yesman.epicfight.api.utils.ExtendableEnum;
import yesman.epicfight.api.utils.ExtendableEnumManager;

public interface SkillCategory extends ExtendableEnum {
	public static final ExtendableEnumManager<SkillCategory> ENUM_MANAGER = new ExtendableEnumManager<> ("skill_category");
	
	public boolean shouldSave();
	
	public boolean shouldSynchronize();
	
	public boolean learnable();
}