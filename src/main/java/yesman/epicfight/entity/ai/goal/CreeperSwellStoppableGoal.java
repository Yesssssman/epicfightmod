package yesman.epicfight.entity.ai.goal;

import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.monster.CreeperEntity;
import yesman.epicfight.capabilities.entity.mob.CreeperData;

public class CreeperSwellStoppableGoal extends CreeperSwellGoal {
	protected CreeperEntity creeperEntity;
	protected CreeperData creeperdata;
	
	public CreeperSwellStoppableGoal(CreeperData creeperdata, CreeperEntity CreeperEntityIn) {
		super(CreeperEntityIn);
		this.creeperEntity = CreeperEntityIn;
		this.creeperdata = creeperdata;
	}
	
	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && !this.creeperdata.getEntityState().isInaction();
    }
	
	@Override
	public boolean shouldContinueExecuting() {
		return this.shouldExecute();
    }
	
	@Override
	public void resetTask() {
		super.resetTask();
		this.creeperEntity.setCreeperState(-1);
    }
}