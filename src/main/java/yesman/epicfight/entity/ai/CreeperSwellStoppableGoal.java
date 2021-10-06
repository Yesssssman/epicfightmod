package yesman.epicfight.entity.ai;

import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.monster.CreeperEntity;
import yesman.epicfight.capabilities.entity.mob.CreeperData;

public class CreeperSwellStoppableGoal extends CreeperSwellGoal {
	protected CreeperEntity CreeperEntity;
	protected CreeperData creeperdata;
	
	public CreeperSwellStoppableGoal(CreeperData creeperdata, CreeperEntity CreeperEntityIn) {
		super(CreeperEntityIn);
		this.CreeperEntity = CreeperEntityIn;
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
		this.CreeperEntity.setCreeperState(-1);
    }
}