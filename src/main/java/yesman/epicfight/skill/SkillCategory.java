package yesman.epicfight.skill;

import yesman.epicfight.api.utils.game.ExtendableEnumManager;
import yesman.epicfight.api.utils.game.ExtendableEnum;

public interface SkillCategory extends ExtendableEnum {
	public static final ExtendableEnumManager<SkillCategory> ENUM_MANAGER = new ExtendableEnumManager<> ();
	
	public boolean shouldSaved();
	
	public boolean shouldSynchronized();
	
	public boolean learnable();
}