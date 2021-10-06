package yesman.epicfight.entity.ai;

import java.util.EnumSet;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.MobData;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCLivingMotionChange;

public class ChasingGoal extends Goal {
	protected MobData<?> mobdata;
	protected final MobEntity attacker;
	private final double speedTowardsTarget;
	private final boolean longMemory;
	private Path path;
	private int delayCounter;
	private double targetX;
	private double targetY;
	private double targetZ;
	protected final int attackInterval = 20;
	private int failedPathFindingPenalty = 0;
	private boolean canPenalize = false;

	protected final StaticAnimation chasingAnimation;
	protected final StaticAnimation walkingAnimation;
	protected final boolean changeMotion;

	public ChasingGoal(MobData<?> mobdata, MobEntity host, double speedIn, boolean useLongMemory, StaticAnimation chasingId, StaticAnimation walkId, boolean changeMotion) {
		this.mobdata = mobdata;
		this.attacker = host;
		this.speedTowardsTarget = speedIn;
		this.longMemory = useLongMemory;
		this.chasingAnimation = chasingId;
		this.walkingAnimation = walkId;
		this.changeMotion = changeMotion;
		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public ChasingGoal(MobData<?> mobdata, MobEntity host, double speedIn, boolean useLongMemory) {
		this(mobdata, host, speedIn, useLongMemory, null, null, false);
	}

	public ChasingGoal(MobData<?> mobdata, MobEntity host, double speedIn, boolean useLongMemory, StaticAnimation chasing, StaticAnimation walk) {
		this(mobdata, host, speedIn, useLongMemory, chasing, walk, true);
	}

	public boolean shouldExecute() {
		LivingEntity livingentity = this.attacker.getAttackTarget();

		if (livingentity == null || !livingentity.isAlive()) {
			return false;
		} else if (this.mobdata.getEntityState().isInaction()) {
			return false;
		} else {
			if (this.canPenalize) {
				if (--this.delayCounter <= 0) {
					this.path = this.attacker.getNavigator().pathfind(livingentity, 0);
					this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
					return this.path != null;
				} else {
					return true;
				}
			}

			this.path = this.attacker.getNavigator().pathfind(livingentity, 0);
			if (this.path != null) {
				return true;
			} else {
				return this.getAttackReachSqr(livingentity) >= this.attacker.getDistanceSq(livingentity.getPosX(), livingentity.getBoundingBox().minY, livingentity.getPosZ());
			}
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		LivingEntity livingentity = this.attacker.getAttackTarget();
		
		if (livingentity == null) {
			return false;
		} else if (!livingentity.isAlive()) {
			return false;
		} else if (!this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(livingentity.getPositionVec()))) {
			return false;
		} else {
			return !(livingentity instanceof PlayerEntity) || !livingentity.isSpectator() && !((PlayerEntity) livingentity).isCreative();
		}
	}

	@Override
	public void startExecuting() {
		this.attacker.getNavigator().setPath(this.path, this.speedTowardsTarget);
		this.attacker.setAggroed(true);
		this.delayCounter = -1;
		
		if (this.changeMotion) {
			STCLivingMotionChange msg = new STCLivingMotionChange(attacker.getEntityId(), 1);
			msg.setMotions(LivingMotion.WALK);
			msg.setAnimations(chasingAnimation);
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, attacker);
        }
	}

	@Override
	public void resetTask() {
		LivingEntity livingentity = this.attacker.getAttackTarget();
		if (!EntityPredicates.CAN_AI_TARGET.test(livingentity)) {
			this.attacker.setAttackTarget((LivingEntity) null);
		}

		this.attacker.setAggroed(false);
		this.attacker.getNavigator().clearPath();

		if (this.changeMotion) {
			STCLivingMotionChange msg = new STCLivingMotionChange(attacker.getEntityId(), 1);
			msg.setMotions(LivingMotion.WALK);
			msg.setAnimations(walkingAnimation);
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, attacker);
        }
	}
	
	@Override
	public void tick() {
		if (this.mobdata.getEntityState().isInaction()) {
			this.attacker.getNavigator().clearPath();
			this.delayCounter = -1;
			return;
		}
		
		LivingEntity livingentity = this.attacker.getAttackTarget();
		this.attacker.getLookController().setLookPositionWithEntity(livingentity, 30.0F, 30.0F);
		double d0 = this.attacker.getDistanceSq(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ());
		
		if (this.longMemory || this.attacker.getEntitySenses().canSee(livingentity) && --this.delayCounter <= 0 && 
				(this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingentity.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D
				|| this.attacker.getRNG().nextFloat() < 0.05F))
		{
			this.targetX = livingentity.getPosX();
			this.targetY = livingentity.getBoundingBox().minY;
			this.targetZ = livingentity.getPosZ();
			this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
			
			if (this.canPenalize) {
				this.delayCounter += this.failedPathFindingPenalty;
				if (this.attacker.getNavigator().getPath() != null) {
					PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
					if (finalPathPoint != null && livingentity.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1) {
						this.failedPathFindingPenalty = 0;
					} else {
						this.failedPathFindingPenalty += 10;
					}
				} else {
					this.failedPathFindingPenalty += 10;
				}
			}
			if (d0 > 1024.0D)
				this.delayCounter += 10;
			else if (d0 > 256.0D)
				this.delayCounter += 5;
			
			if (!this.attacker.getNavigator().tryMoveToEntityLiving(livingentity, this.speedTowardsTarget))
				this.delayCounter += 2;
		}
	}

	protected double getAttackReachSqr(LivingEntity attackTarget) {
		return (double)(this.attacker.getWidth() * 2.0F * this.attacker.getWidth() * 2.0F + attackTarget.getWidth());
	}
}