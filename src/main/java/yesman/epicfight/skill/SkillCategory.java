package yesman.epicfight.skill;

import yesman.epicfight.api.utils.game.EnumerateAssignmentManager;
import yesman.epicfight.api.utils.game.ExtendableEnum;

public interface SkillCategory extends ExtendableEnum {
	public static final EnumerateAssignmentManager<SkillCategory> ASSIGNMENT_MANAGER = new EnumerateAssignmentManager<> ();
	
	public boolean shouldSaved();
	
	public boolean shouldSynchronized();
	
	public boolean learnable();
}