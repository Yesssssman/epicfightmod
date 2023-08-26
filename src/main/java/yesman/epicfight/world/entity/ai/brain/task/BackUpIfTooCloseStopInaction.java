package yesman.epicfight.world.entity.ai.brain.task;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BackUpIfTooClose;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class BackUpIfTooCloseStopInaction<E extends Mob> extends BackUpIfTooClose<E> {
	public BackUpIfTooCloseStopInaction(int tooCloseDistance, float strafeSpeed) {
		super(tooCloseDistance, strafeSpeed);
	}
	
	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
		if (super.checkExtraStartConditions(level, mob)) {
			MobPatch<?> mobpatch = EpicFightCapabilities.getEntityPatch(mob, MobPatch.class);
			boolean inaction = mobpatch.getEntityState().inaction();
			
			if (inaction) {
				mob.getMoveControl().strafe(0.0F, 0.0F);
			}
			
			return !inaction;
		}
		
		return false;
	}
}