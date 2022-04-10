package yesman.epicfight.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class AttackPatternGoal extends Goal {
	protected final Mob attacker;
	protected final MobPatch<?> mobpatch;
	protected final double minDist;
	protected final double maxDist;
	protected final List<AttackAnimation> pattern;
	protected final boolean affectHorizon;
	protected int patternCounter;
	
	public AttackPatternGoal(MobPatch<?> mobpatch, Mob attacker, double minDist, double maxDIst, boolean affectHorizon, List<AttackAnimation> pattern) {
		this.attacker = attacker;
		this.mobpatch = mobpatch;
		this.minDist = minDist * minDist;
		this.maxDist = maxDIst * maxDIst;
		this.pattern = pattern;
		this.patternCounter = 0;
		this.affectHorizon = affectHorizon;
		this.setFlags(EnumSet.noneOf(Goal.Flag.class));
	}
	
	@Override
	public boolean canUse() {
		LivingEntity LivingEntity = this.attacker.getTarget();
		return this.isValidTarget(LivingEntity) && this.isTargetInRange(LivingEntity);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
	public boolean canContinueToUse() {
    	LivingEntity LivingEntity = this.attacker.getTarget();
    	return pattern.size() <= this.patternCounter && isValidTarget(LivingEntity) && isTargetInRange(LivingEntity);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void start() {
        
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
	public void stop() {
		this.patternCounter %= this.pattern.size();
	}
    
	protected boolean canExecuteAttack() {
		return !this.mobpatch.getEntityState().inaction();
	}
	
	@Override
	public void tick() {
		if (this.canExecuteAttack()) {
        	AttackAnimation attackMotion = this.pattern.get(this.patternCounter++);
        	this.patternCounter %= this.pattern.size();
        	this.mobpatch.playAnimationSynchronized(attackMotion, 0);
        	this.mobpatch.updateEntityState();
        }
    }
    
	protected boolean isTargetInRange(LivingEntity attackTarget) {
    	double targetRange = this.attacker.distanceToSqr(attackTarget.getX(), attackTarget.getBoundingBox().minY, attackTarget.getZ());
    	return targetRange <= this.maxDist && targetRange >= this.minDist && isInSameHorizontalPosition(attackTarget);
    }
    
	protected boolean isValidTarget(LivingEntity attackTarget) {
    	return attackTarget != null && attackTarget.isAlive() && !((attackTarget instanceof Player) && (((Player)attackTarget).isSpectator() || ((Player)attackTarget).isCreative()));
    }

	protected boolean isInSameHorizontalPosition(LivingEntity attackTarget) {
    	if (this.affectHorizon) {
    		return Math.abs(this.attacker.getY() - attackTarget.getY()) <= this.attacker.getEyeHeight();
    	}
    	
    	return true;
    }
}