package yesman.epicfight.world.entity.ai.brain.task;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.world.server.ServerWorld;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class MoveToTargetSinkStopInaction extends WalkToTargetTask {
	@Override
	protected boolean canStillUse(ServerWorld level, MobEntity mob, long gameTime) {
		if (super.canStillUse(level, mob, gameTime)) {
			MobPatch<?> mobpatch = (MobPatch<?>)mob.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
			return !mobpatch.getEntityState().inaction();
		}
		
		return false;
	}
}