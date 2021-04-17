package maninthehouse.epicfight.entity.ai;

import maninthehouse.epicfight.capabilities.entity.mob.CreeperData;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.monster.EntityCreeper;

public class EntityAICreeperSwellStoppable extends EntityAICreeperSwell
{
	protected EntityCreeper entityCreeper;
	protected CreeperData creeperdata;
	
	public EntityAICreeperSwellStoppable(CreeperData creeperdata, EntityCreeper entitycreeperIn)
	{
		super(entitycreeperIn);
		this.entityCreeper = entitycreeperIn;
		this.creeperdata = creeperdata;
	}
	
	@Override
	public boolean shouldExecute()
    {
		return super.shouldExecute() && !this.creeperdata.isInaction();
    }
	
	@Override
	public boolean shouldContinueExecuting()
    {
		return this.shouldExecute();
    }
	
	@Override
	public void resetTask()
    {
		this.entityCreeper.setCreeperState(-1);
    }
}