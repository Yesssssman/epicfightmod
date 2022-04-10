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
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeLivingMotion;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class ChasingGoal extends Goal {
	protected MobPatch<?> mobpatch;
	protected final Mob attacker;
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

	public ChasingGoal(MobPatch<?> mobpatch, Mob host, double speedIn, boolean useLongMemory, StaticAnimation chasingId, StaticAnimation walkId, boolean changeMotion) {
		this.mobpatch = mobpatch;
		this.attacker = host;
		this.speedTowardsTarget = speedIn;
		this.longMemory = useLongMemory;
		this.chasingAnimation = chasingId;
		this.walkingAnimation = walkId;
		this.changeMotion = changeMotion;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public ChasingGoal(MobPatch<?> mobpatch, Mob host, double speedIn, boolean useLongMemory) {
		this(mobpatch, host, speedIn, useLongMemory, null, null, false);
	}

	public ChasingGoal(MobPatch<?> mobpatch, Mob host, double speedIn, boolean useLongMemory, StaticAnimation chasing, StaticAnimation walk) {
		this(mobpatch, host, speedIn, useLongMemory, chasing, walk, true);
	}
	
	@Override
	public boolean canUse() {
		LivingEntity livingentity = this.attacker.getTarget();

		if (livingentity == null || !livingentity.isAlive()) {
			return false;
		} else {
			if (this.canPenalize) {
				if (--this.delayCounter <= 0) {
					this.path = this.attacker.getNavigation().createPath(livingentity, 0);
					this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
					return this.path != null;
				} else {
					return true;
				}
			}

			this.path = this.attacker.getNavigation().createPath(livingentity, 0);
			
			if (this.path != null) {
				return true;
			} else {
				return this.getAttackDistanceSqr(livingentity) >= this.attacker.distanceToSqr(livingentity.getX(), livingentity.getBoundingBox().minY, livingentity.getZ());
			}
		}
	}
	
	@Override
	public boolean canContinueToUse() {
		LivingEntity livingentity = this.attacker.getTarget();
		
		if (livingentity == null) {
			return false;
		} else if (!livingentity.isAlive()) {
			return false;
		} else if (!this.attacker.isWithinRestriction(new BlockPos(livingentity.position()))) {
			return false;
		} else {
			return !livingentity.isSpectator() && (!(livingentity instanceof Player) || !((Player)livingentity).isCreative());
		}
	}
	
	@Override
	public void start() {
		this.attacker.getNavigation().moveTo(this.path, this.speedTowardsTarget);
		this.attacker.setAggressive(true);
		this.delayCounter = -1;
		
		if (this.changeMotion) {
			SPChangeLivingMotion msg = new SPChangeLivingMotion(this.attacker.getId(), 1, SPPlayAnimation.Layer.BASE_LAYER);
			msg.setMotions(LivingMotion.WALK);
			msg.setAnimations(this.chasingAnimation);
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.attacker);
        }
	}

	@Override
	public void stop() {
		LivingEntity livingentity = this.attacker.getTarget();
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
			this.attacker.setTarget((LivingEntity) null);
		}

		this.attacker.setAggressive(false);
		this.attacker.getNavigation().stop();

		if (this.changeMotion) {
			SPChangeLivingMotion msg = new SPChangeLivingMotion(this.attacker.getId(), 1, SPPlayAnimation.Layer.BASE_LAYER);
			msg.setMotions(LivingMotion.WALK);
			msg.setAnimations(this.walkingAnimation);
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.attacker);
        }
	}
	
	@Override
	public void tick() {
		if (this.mobpatch.getEntityState().inaction()) {
			this.attacker.getNavigation().stop();
			this.delayCounter = -1;
			return;
		}
		
		LivingEntity livingentity = this.attacker.getTarget();
		this.attacker.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
		this.attacker.setYRot(this.attacker.yBodyRot);
		double d0 = this.attacker.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
		
		if (this.longMemory || this.attacker.getSensing().hasLineOfSight(livingentity) && --this.delayCounter <= 0 && 
				(this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingentity.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D
					|| this.attacker.getRandom().nextFloat() < 0.05F))
		{
			this.targetX = livingentity.getX();
			this.targetY = livingentity.getBoundingBox().minY;
			this.targetZ = livingentity.getZ();
			this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
			
			if (this.canPenalize) {
				this.delayCounter += this.failedPathFindingPenalty;
				if (this.attacker.getNavigation().getPath() != null) {
					Node finalPathPoint = this.attacker.getNavigation().getPath().getEndNode();
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
			
			if (!this.attacker.getNavigation().moveTo(livingentity, this.speedTowardsTarget)) {
				this.delayCounter += 2;
			}
		}
	}

	protected double getAttackDistanceSqr(LivingEntity attackTarget) {
		return (double)(this.attacker.getBbWidth() * 2.0F * this.attacker.getBbWidth() * 2.0F + attackTarget.getBbWidth());
	}
}