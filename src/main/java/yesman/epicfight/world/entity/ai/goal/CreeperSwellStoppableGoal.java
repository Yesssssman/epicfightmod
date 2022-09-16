package yesman.epicfight.world.entity.ai.goal;

import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.monster.CreeperEntity;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class CreeperSwellStoppableGoal extends CreeperSwellGoal {
	protected CreeperEntity creeperEntity;
	protected MobPatch<?> creeperpatch;
	
	public CreeperSwellStoppableGoal(MobPatch<?> creeperdata, CreeperEntity creeperEntityIn) {
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