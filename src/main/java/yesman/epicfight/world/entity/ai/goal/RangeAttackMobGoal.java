package yesman.epicfight.world.entity.ai.goal;

import java.util.EnumSet;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.world.capabilities.entitypatch.mob.HumanoidMobPatch;

public class RangeAttackMobGoal extends Goal {
    protected final Mob mob;
    protected final HumanoidMobPatch<?> mobpatch;
    protected final StaticAnimation shotAnimation;
    protected final RangedAttackMob rangedAttackEntityHost;
    protected LivingEntity attackTarget;
    protected int rangedAttackTime;
    protected final double entityMoveSpeed;
    protected int seeTime;
    protected final int attackIntervalMin;
    protected final int maxRangedAttackTime;
    protected final float attackRadius;
    protected final float maxAttackDistance;
    protected final int animationFrame;

    public RangeAttackMobGoal(RangedAttackMob attacker, HumanoidMobPatch<?> mobpatch, StaticAnimation shotanimation, double movespeed, int maxAttackTime, float maxAttackDistanceIn, int animationFrame) {
        this(attacker, mobpatch, shotanimation, movespeed, maxAttackTime, maxAttackTime, maxAttackDistanceIn, animationFrame);
    }
    
    public RangeAttackMobGoal(RangedAttackMob attacker, HumanoidMobPatch<?> mobpatch, StaticAnimation shotanimation, 
    		double movespeed, int p_i1650_4_, int maxAttackTime, float maxAttackDistanceIn, int animationFrame) {
        this.rangedAttackTime = -1;
        
		if (!(attacker instanceof LivingEntity)) {
            throw new IllegalArgumentException("Illegal Entity");
		} else {
        	this.mobpatch = mobpatch;
        	this.shotAnimation = shotanimation;
            this.rangedAttackEntityHost = attacker;
            this.mob = (Mob)attacker;
            this.entityMoveSpeed = movespeed;
            this.attackIntervalMin = p_i1650_4_;
            this.maxRangedAttackTime = maxAttackTime;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
            this.animationFrame = animationFrame;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }
	}

	@Override
	public boolean canUse() {
		LivingEntity LivingEntity = this.mob.getTarget();
		if (LivingEntity == null) {
			return false;
		} else {
			this.attackTarget = LivingEntity;
			return true;
		}
	}

    @Override
	public boolean canContinueToUse() {
		if (this.mobpatch.getEntityState().inaction()) {
    		DynamicAnimation animation = this.mobpatch.getServerAnimator().getPlayerFor(null).getPlay();
    		if (animation != this.shotAnimation && (animation instanceof LinkAnimation && ((LinkAnimation)animation).getNextAnimation() != this.shotAnimation)) {
    			return false;
			}
    	}
    	
        return this.canUse() || !this.mob.getNavigation().isDone();
    }

	@Override
	public void stop() {
        this.attackTarget = null;
        this.seeTime = 0;
        this.rangedAttackTime = -1;
    }

    @Override
	public void tick() {
        double d0 = this.mob.distanceToSqr(this.attackTarget.getX(), this.attackTarget.getBoundingBox().minY, this.attackTarget.getZ());
        boolean flag = this.mob.getSensing().hasLineOfSight(this.attackTarget);

        if (flag) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.attackTarget, this.entityMoveSpeed);
        }
        
        this.mob.getLookControl().setLookAt(this.attackTarget, 30.0F, 30.0F);
        
		if (--this.rangedAttackTime == this.animationFrame && !this.mobpatch.getEntityState().inaction() && flag) {
			this.mobpatch.playAnimationSynchronized(this.shotAnimation, 0.0F, SPPlayAnimation.Layer.COMPOSITE_LAYER);
		} else if (this.rangedAttackTime == 0) {
            if(!flag) return;
            float f = (float)Math.sqrt(d0) / this.attackRadius;
            float lvt_5_1_ = Mth.clamp(f, 0.1F, 1.0F);
            this.rangedAttackEntityHost.performRangedAttack(this.attackTarget, lvt_5_1_);
            this.rangedAttackTime = Mth.floor(f * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
		} else if (this.rangedAttackTime < 0) {
        	float f2 = (float)Math.sqrt(d0) / this.attackRadius;
            this.rangedAttackTime = Mth.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
        }
    }
}