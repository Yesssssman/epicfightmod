package yesman.epicfight.world.entity.ai.brain.task;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.task.AttackStrafingTask;
import net.minecraft.world.server.ServerWorld;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class BackUpIfTooCloseStopInaction<E extends MobEntity> extends AttackStrafingTask<E> {
	public BackUpIfTooCloseStopInaction(int tooCloseDistance, float strafeSpeed) {
		super(tooCloseDistance, strafeSpeed);
	}
	
	@Override
	protected boolean checkExtraStartConditions(ServerWorld level, E mob) {
		if (super.checkExtraStartConditions(level, mob)) {
			MobPatch<?> mobpatch = (MobPatch<?>)mob.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
			boolean inaction = mobpatch.getEntityState().inaction();
			
			if (inaction) {
				mob.getMoveControl().strafe(0.0F, 0.0F);
			}
			
			return !inaction;
		}
		
		return false;
	}
}