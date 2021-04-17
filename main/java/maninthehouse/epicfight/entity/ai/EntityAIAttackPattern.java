package maninthehouse.epicfight.entity.ai;

import java.util.List;

import maninthehouse.epicfight.animation.types.attack.AttackAnimation;
import maninthehouse.epicfight.capabilities.entity.MobData;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCPlayAnimationTarget;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIAttackPattern extends EntityAIBase
{
	protected final EntityCreature attacker;
	protected final MobData mobdata;
	protected final double minDist;
	protected final double maxDist;
	protected final List<AttackAnimation> pattern;
	protected final boolean affectHorizon;
	
	protected int patternIndex;
	
	public EntityAIAttackPattern(MobData mobdata, EntityCreature attacker, double minDist, double maxDIst, boolean affectHorizon, List<AttackAnimation> pattern)
	{
		this.attacker = attacker;
		this.mobdata = mobdata;
		this.minDist = minDist * minDist;
		this.maxDist = maxDIst * maxDIst;
		this.pattern = pattern;
		this.patternIndex = 0;
		this.affectHorizon = affectHorizon;
		this.setMutexBits(8);
	}
	
	@Override
    public boolean shouldExecute()
    {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
		
		return isValidTarget(entitylivingbase) && isTargetInRange(entitylivingbase);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting()
    {
    	EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
    	
    	return pattern.size() <= patternIndex && isValidTarget(entitylivingbase) && isTargetInRange(entitylivingbase);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void resetTask()
    {
    	this.patternIndex %= pattern.size();
    }
    
    protected boolean canExecuteAttack()
    {
    	return !mobdata.isInaction();
    }
    
    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void updateTask()
    {
        if(this.canExecuteAttack())
        {
        	AttackAnimation att = pattern.get(patternIndex++);
        	this.patternIndex %= pattern.size();
        	mobdata.getServerAnimator().playAnimation(att, 0);
        	mobdata.updateInactionState();
        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(att.getId(), attacker.getEntityId(), 0, 
        			attacker.getAttackTarget().getEntityId()), attacker);
        }
    }
    
    protected boolean isTargetInRange(EntityLivingBase attackTarget)
    {
    	double targetRange = this.attacker.getDistanceSq(attackTarget.posX, attackTarget.getEntityBoundingBox().minY, attackTarget.posZ);
    	return targetRange <= this.maxDist && targetRange >= this.minDist && isInSameHorizontalPosition(attackTarget);
    }
    
    protected boolean isValidTarget(EntityLivingBase attackTarget)
    {
    	return attackTarget != null && attackTarget.isEntityAlive() && 
    			!((attackTarget instanceof EntityPlayer) && (((EntityPlayer)attackTarget).isSpectator() || ((EntityPlayer)attackTarget).isCreative()));
    }
    
    protected boolean isInSameHorizontalPosition(EntityLivingBase attackTarget)
    {
    	if(affectHorizon)
    	{
    		return Math.abs(attacker.posY - attackTarget.posY) <= attacker.getEyeHeight();
    	}
    	
    	return true;
    }
}