package yesman.epicfight.api.animation;

import yesman.epicfight.api.utils.game.EnumerateAssignmentManager;
import yesman.epicfight.api.utils.game.ExtendableEnum;

public interface LivingMotion extends ExtendableEnum {
	public static final EnumerateAssignmentManager<LivingMotion> ASSIGNMENT_MANAGER = new EnumerateAssignmentManager<> ();
}