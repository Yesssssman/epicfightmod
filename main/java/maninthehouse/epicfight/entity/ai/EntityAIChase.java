package maninthehouse.epicfight.entity.ai;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.MobData;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCLivingMotionChange;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIChase extends EntityAIBase
{
    World world;
    protected EntityCreature attacker;
    protected final MobData entitydata;
    /** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
    /** The speed with which the mob will approach the target */
    double speedTowardsTarget;
    /** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
    boolean longMemory;
    /** The PathEntity of our entity. */
    Path path;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    protected final int attackInterval = 20;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;
    
    protected final StaticAnimation chasingAnimation;
    protected final StaticAnimation walkingAnimation;
    protected final boolean motionChange;

    public EntityAIChase(MobData entitydata, EntityCreature creature, double speedIn, boolean useLongMemory, StaticAnimation chasingId, StaticAnimation walkId, boolean motionChange)
    {
    	this.entitydata = entitydata;
        this.attacker = creature;
        this.world = creature.world;
        this.longMemory = useLongMemory;
        this.chasingAnimation = chasingId;
        this.walkingAnimation = walkId;
        this.motionChange = motionChange;
        this.speedTowardsTarget = speedIn;
        this.setMutexBits(3);
    }
    
    public EntityAIChase(MobData entitydata, EntityCreature creature, double speedIn, boolean useLongMemory, StaticAnimation chasingId, StaticAnimation walkId)
    {
    	this(entitydata, creature, speedIn, useLongMemory, chasingId, walkId, true);
    }
    
    public EntityAIChase(MobData entitydata, EntityCreature creature, double speedIn, boolean useLongMemory)
    {
    	this(entitydata, creature, speedIn, useLongMemory, null, null, false);
    }
    
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else
        {
            if (canPenalize)
            {
                if (--this.delayCounter <= 0)
                {
                    this.path = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
                    this.delayCounter = 2 + this.attacker.getRNG().nextInt(3);
                    return this.path != null;
                }
                else
                {
                    return true;
                }
            }
            this.path = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);

            if (this.path != null)
            {
                return true;
            }
            else
            {
                return this.getAttackReachSqr(entitylivingbase) >= this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        
        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else if (!this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)))
        {
            return false;
        }
        else
        {
            return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).isCreative();
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        this.attacker.getNavigator().setPath(this.path, this.speedTowardsTarget);
        this.delayCounter = 0;
        
        if(motionChange)
        {
        	STCLivingMotionChange msg = new STCLivingMotionChange(attacker.getEntityId(), 1);
			msg.setMotions(LivingMotion.WALKING);
			msg.setAnimations(chasingAnimation);
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, attacker);
        }
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void resetTask()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        
        if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer)entitylivingbase).isSpectator() || ((EntityPlayer)entitylivingbase).isCreative()))
        {
            this.attacker.setAttackTarget((EntityLivingBase)null);
        }
        
        this.attacker.getNavigator().clearPath();
        
        if(motionChange)
        {
        	STCLivingMotionChange msg = new STCLivingMotionChange(attacker.getEntityId(), 1);
			msg.setMotions(LivingMotion.WALKING);
			msg.setAnimations(walkingAnimation);
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, attacker);
        }
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void updateTask()
    {
    	if(this.entitydata.isInaction())
    	{
    		delayCounter = -1;
    		return;
    	}
    	
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ);
        --this.delayCounter;
        
        if((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0D && 
        		this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D
        		|| this.attacker.getRNG().nextFloat() < 0.05F))
        {
            this.targetX = entitylivingbase.posX;
            this.targetY = entitylivingbase.getEntityBoundingBox().minY;
            this.targetZ = entitylivingbase.posZ;
            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
            
            if (this.canPenalize)
            {
                this.delayCounter += failedPathFindingPenalty;
                if (this.attacker.getNavigator().getPath() != null)
                {
                    net.minecraft.pathfinding.PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
                    if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                        failedPathFindingPenalty = 0;
                    else
                        failedPathFindingPenalty += 10;
                }
                else
                {
                    failedPathFindingPenalty += 10;
                }
            }
            
            if (d0 > 1024.0D)
            {
                this.delayCounter += 10;
            }
            else if (d0 > 256.0D)
            {
                this.delayCounter += 5;
            }
            
            if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget))
            {
                this.delayCounter += 15;
            }
        }
    }

    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        return (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width);
    }
}