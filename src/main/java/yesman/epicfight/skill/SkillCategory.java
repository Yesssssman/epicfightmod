package yesman.epicfight.skill;

import yesman.epicfight.api.utils.ExtendableEnum;
import yesman.epicfight.api.utils.ExtendableEnumManager;

public interface SkillCategory extends ExtendableEnum {
	public static final ExtendableEnumManager<SkillCategory> ENUM_MANAGER = new ExtendableEnumManager<> ();
	
	public boolean shouldSaved();
	
	public boolean shouldSynchronized();
	
	public boolean learnable();
}