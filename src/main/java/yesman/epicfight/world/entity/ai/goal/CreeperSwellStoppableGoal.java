package yesman.epicfight.world.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class CreeperSwellStoppableGoal extends SwellGoal {
	protected Creeper creeperEntity;
	protected MobPatch<?> creeperpatch;
	
	public CreeperSwellStoppableGoal(MobPatch<?> creeperdata, Creeper creeperEntityIn) {
		super(creeperEntityIn);
		this.creeperEntity = creeperEntityIn;
		this.creeperpatch = creeperdata;
	}
	
	@Override
	public boolean canUse() {
		return super.canUse() && !this.creeperpatch.getEntityState().inaction();
    }
	
	@Override
	public boolean canContinueToUse() {
		return this.canUse();
    }
	
	@Override
	public void stop() {
		super.stop();
		this.creeperEntity.setSwellDir(-1);
    }
}