package yesman.epicfight.world.entity.ai.goal;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class ChasingGoal extends Goal {
	protected MobPatch<?> mobpatch;
	protected final Mob mob;
	private final double speedTowardsTarget;
	private final double keepDistance;
	private final boolean longMemory;
	private Path path;
	private int delayCounter;
	private double targetX;
	private double targetY;
	private double targetZ;
	private int failedPathFindingPenalty = 0;
	private boolean canPenalize = false;
	
	public ChasingGoal(MobPatch<?> mobpatch, Mob mob, double speedIn, double keepDistance, boolean useLongMemory) {
		this.mobpatch = mobpatch;
		this.mob = mob;
		this.speedTowardsTarget = speedIn;
		this.keepDistance = keepDistance;
		this.longMemory = useLongMemory;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	public ChasingGoal(MobPatch<?> mobpatch, Mob host, double speedIn, boolean useLongMemory) {
		this(mobpatch, host, speedIn, 0.0D, useLongMemory);
	}
	
	@Override
	public boolean canUse() {
		LivingEntity livingentity = this.mob.getTarget();
		
		if (livingentity == null || !livingentity.isAlive()) {
			return false;
		} else {
			if (this.canPenalize) {
				if (--this.delayCounter <= 0) {
					this.path = this.mob.getNavigation().createPath(livingentity, 0);
					this.delayCounter = 4 + this.mob.getRandom().nextInt(7);
					return this.path != null;
				} else {
					return true;
				}
			}

			this.path = this.mob.getNavigation().createPath(livingentity, 0);
			
			if (this.path != null) {
				return true;
			} else {
				return this.getAttackDistanceSqr(livingentity) >= this.mob.distanceToSqr(livingentity.getX(), livingentity.getBoundingBox().minY, livingentity.getZ());
			}
		}
	}
	
	@Override
	public boolean canContinueToUse() {
		LivingEntity livingentity = this.mob.getTarget();
		
		if (livingentity == null) {
			return false;
		} else if (!livingentity.isAlive()) {
			return false;
		} else if (!this.mob.isWithinRestriction(new BlockPos(livingentity.position()))) {
			return false;
		} else {
			return !livingentity.isSpectator() && (!(livingentity instanceof Player) || !((Player)livingentity).isCreative());
		}
	}
	
	@Override
	public void start() {
		this.mob.getNavigation().moveTo(this.path, this.speedTowardsTarget);
		this.mob.setAggressive(true);
		this.delayCounter = -1;
	}
	
	@Override
	public void stop() {
		LivingEntity livingentity = this.mob.getTarget();
		
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
			this.mob.setTarget((LivingEntity) null);
		}
		
		this.mob.setAggressive(false);
		this.mob.getNavigation().stop();
	}
	
	@Override
	public void tick() {
		LivingEntity livingentity = this.mob.getTarget();
		
		if (livingentity == null || this.mobpatch.getEntityState().inaction()) {
			this.mob.getNavigation().stop();
			this.delayCounter = -1;
			return;
		}
		
		this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
		this.mob.setYRot(this.mob.yBodyRot);
		double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
		
		if (d0 <= this.keepDistance) {
            this.mob.getNavigation().stop();
        } else {
        	if (this.longMemory || this.mob.getSensing().hasLineOfSight(livingentity) && --this.delayCounter <= 0 && 
    				(this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingentity.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D
    					|| this.mob.getRandom().nextFloat() < 0.05F))
    		{	
    			this.targetX = livingentity.getX();
    			this.targetY = livingentity.getBoundingBox().minY;
    			this.targetZ = livingentity.getZ();
    			this.delayCounter = 4 + this.mob.getRandom().nextInt(7);
    			
    			if (this.canPenalize) {
    				this.delayCounter += this.failedPathFindingPenalty;
    				if (this.mob.getNavigation().getPath() != null) {
    					Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
    					if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1) {
    						this.failedPathFindingPenalty = 0;
    					} else {
    						this.failedPathFindingPenalty += 10;
    					}
    				} else {
    					this.failedPathFindingPenalty += 10;
    				}
    			}
    			
    			if (d0 > 1024.0D) {
    				this.delayCounter += 10;
    			} else if (d0 > 256.0D) {
    				this.delayCounter += 5;
    			}
    			
    			if (!this.mob.getNavigation().moveTo(livingentity, this.speedTowardsTarget)) {
    				this.delayCounter += 2;
    			}
    		}
        }
	}

	protected double getAttackDistanceSqr(LivingEntity attackTarget) {
		return (double)(this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + attackTarget.getBbWidth());
	}
}