package maninthehouse.epicfight.entity.ai;

import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.mob.BipedMobData;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCPlayAnimationTarget;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;

public class EntityAIRangeAttack extends EntityAIBase
{
    /** The entity the AI instance has been applied to */
    protected final EntityLiving entityHost;
    protected final BipedMobData entitydata;
    protected final StaticAnimation rangeAttackAnimation;
    /** The entity (as a RangedAttackMob) the AI instance has been applied to. */
    protected final IRangedAttackMob rangedAttackEntityHost;
    protected EntityLivingBase attackTarget;
    /**
     * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
     * maxRangedAttackTime.
     */
    protected int rangedAttackTime;
    protected final double entityMoveSpeed;
    protected int seeTime;
    protected final int attackIntervalMin;
    /** The maximum time the AI has to wait before peforming another ranged attack. */
    protected final int maxRangedAttackTime;
    protected final float attackRadius;
    protected final float maxAttackDistance;
    protected final int animationFrame;

    public EntityAIRangeAttack(IRangedAttackMob attacker, BipedMobData entitydata, StaticAnimation throwingAnimation, 
    		double movespeed, int maxAttackTime, float maxAttackDistanceIn, int animationFrame)
    {
        this(attacker, entitydata, throwingAnimation, movespeed, maxAttackTime, maxAttackTime, maxAttackDistanceIn, animationFrame);
    }

    public EntityAIRangeAttack(IRangedAttackMob attacker, BipedMobData entitydata, StaticAnimation throwingAnimation, 
    		double movespeed, int p_i1650_4_, int maxAttackTime, float maxAttackDistanceIn, int animationFrame)
    {
        this.rangedAttackTime = -1;
        
        if (!(attacker instanceof EntityLivingBase))
        {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        else
        {
        	this.entitydata = entitydata;
        	this.rangeAttackAnimation = throwingAnimation;
            this.rangedAttackEntityHost = attacker;
            this.entityHost = (EntityLiving)attacker;
            this.entityMoveSpeed = movespeed;
            this.attackIntervalMin = p_i1650_4_;
            this.maxRangedAttackTime = maxAttackTime;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
            this.animationFrame = animationFrame;
            this.setMutexBits(3);
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else
        {
            this.attackTarget = entitylivingbase;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.attackTarget = null;
        this.seeTime = 0;
        this.rangedAttackTime = -1;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
        boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);

        if(flag)
        {
            ++this.seeTime;
        }
        else
        {
            this.seeTime = 0;
        }

        if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20)
        {
            this.entityHost.getNavigator().clearPath();
        }
        else
        {
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }

        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        
        if(--this.rangedAttackTime == this.animationFrame && !this.entitydata.isInaction())
        {
        	entitydata.getServerAnimator().playAnimation(rangeAttackAnimation, 0);
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(rangeAttackAnimation.getId(), entityHost.getEntityId(), 0, attackTarget.getEntityId()), entityHost);
        }
        else if(this.rangedAttackTime == 0)
        {
            if (!flag) return;

            float f = MathHelper.sqrt(d0) / this.attackRadius;
            float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, lvt_5_1_);
            this.rangedAttackTime = MathHelper.floor(f * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
        }
        else if(this.rangedAttackTime < 0)
        {
            float f2 = MathHelper.sqrt(d0) / this.attackRadius;
            this.rangedAttackTime = MathHelper.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
        }
    }
}