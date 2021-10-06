package yesman.epicfight.entity.ai;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.MobData;

public class AttackPatternGoal extends Goal {
	protected final MobEntity attacker;
	protected final MobData<?> mobdata;
	protected final double minDist;
	protected final double maxDist;
	protected final List<AttackAnimation> pattern;
	protected final boolean affectHorizon;
	protected int patternCounter;
	
	public AttackPatternGoal(MobData<?> mobdata, MobEntity attacker, double minDist, double maxDIst, boolean affectHorizon, List<AttackAnimation> pattern) {
		this.attacker = attacker;
		this.mobdata = mobdata;
		this.minDist = minDist * minDist;
		this.maxDist = maxDIst * maxDIst;
		this.pattern = pattern;
		this.patternCounter = 0;
		this.affectHorizon = affectHorizon;
		this.setMutexFlags(EnumSet.noneOf(Flag.class));
	}
	
	@Override
	public boolean shouldExecute() {
		LivingEntity LivingEntity = this.attacker.getAttackTarget();
		return this.isValidTarget(LivingEntity) && this.isTargetInRange(LivingEntity);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
	public boolean shouldContinueExecuting() {
    	LivingEntity LivingEntity = this.attacker.getAttackTarget();
    	return pattern.size() <= this.patternCounter && isValidTarget(LivingEntity) && isTargetInRange(LivingEntity);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void startExecuting() {
        
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
	public void resetTask() {
		this.patternCounter %= this.pattern.size();
	}

	protected boolean canExecuteAttack() {
		return !this.mobdata.getEntityState().isInaction();
	}

    /**
     * Keep ticking a continuous task that has already been started
     */
	@Override
	public void tick() {
		if (this.canExecuteAttack()) {
        	AttackAnimation attackMotion = this.pattern.get(this.patternCounter++);
        	this.patternCounter %= this.pattern.size();
        	this.mobdata.playAnimationSynchronize(attackMotion, 0);
        	this.mobdata.updateEntityState();
        }
    }
    
	protected boolean isTargetInRange(LivingEntity attackTarget) {
    	double targetRange = this.attacker.getDistanceSq(attackTarget.getPosX(), attackTarget.getBoundingBox().minY, attackTarget.getPosZ());
    	return targetRange <= this.maxDist && targetRange >= this.minDist && isInSameHorizontalPosition(attackTarget);
    }
    
	protected boolean isValidTarget(LivingEntity attackTarget) {
    	return attackTarget != null && attackTarget.isAlive() && 
    			!((attackTarget instanceof PlayerEntity) && (((PlayerEntity)attackTarget).isSpectator() || ((PlayerEntity)attackTarget).isCreative()));
    }

	protected boolean isInSameHorizontalPosition(LivingEntity attackTarget) {
    	if (this.affectHorizon) {
    		return Math.abs(this.attacker.getPosY() - attackTarget.getPosY()) <= this.attacker.getEyeHeight();
    	}
    	
    	return true;
    }
}