package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class DragonFlyingPhase extends PatchedDragonPhase {
	private Path currentPath;
	private Vector3d targetLocation;
	private boolean clockwise;
	private boolean executeAirstrike;
	
	public DragonFlyingPhase(EnderDragonEntity p_31230_) {
		super(p_31230_);
	}
	
	@Override
	public PhaseType<DragonFlyingPhase> getPhase() {
		return PatchedPhases.FLYING;
	}
	
	@Override
	public void doServerTick() {
		double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
		
		if (d0 < 100.0D || d0 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision && this.dragon.getDragonFight() != null) {
			this.findNewTarget();
		}
	}
	
	@Override
	public void begin() {
		this.currentPath = null;
		this.targetLocation = null;
	}
	
	@Nullable@Override
	public Vector3d getFlyTargetLocation() {
		return this.dragonpatch.getEntityState().inaction() ? null : this.targetLocation;
	}
	
	public void enableAirstrike() {
		this.executeAirstrike = false;
	}
	
	private void findNewTarget() {
		if (this.currentPath != null && this.currentPath.isDone()) {
			List<PlayerEntity> players = this.getPlayersNearbyWithin(100.0D);
			
			for (PlayerEntity player : players) {
				if (isValidTarget(player)) {
					if (!this.executeAirstrike && this.dragon.getRandom().nextFloat() > this.dragon.getDragonFight().getCrystalsAlive() * 0.1F) {
						if (isInEndSpikes(player)) {
							this.executeAirstrike = true;
						}
						
						this.dragonpatch.setAttakTargetSync(player);
						this.dragon.getPhaseManager().setPhase(PatchedPhases.AIRSTRIKE);
					} else if (isInEndSpikes(player)) {
						this.dragon.getPhaseManager().setPhase(PatchedPhases.LANDING);
					}
					
					return;
				}
			}
		}
		
		if (this.currentPath == null || this.currentPath.isDone()) {
			int j = this.dragon.findClosestNode();
			int k = j;
			
			if (this.dragon.getRandom().nextInt(8) == 0) {
				this.clockwise = !this.clockwise;
				k = j + 6;
			}
			
			if (this.clockwise) {
				++k;
			} else {
				--k;
			}
			
			if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() >= 0) {
				k = k % 12;
				if (k < 0) {
					k += 12;
				}
			} else {
				k = k - 12;
				k = k & 7;
				k = k + 12;
			}
			
			this.currentPath = this.dragon.findPath(j, k, (PathPoint)null);
			
			if (this.currentPath != null) {
				this.currentPath.advance();
			}
		}

		this.navigateToNextPathNode();
	}
	
	private void navigateToNextPathNode() {
		if (this.currentPath != null && !this.currentPath.isDone()) {
			BlockPos vec3i = this.currentPath.getNextNodePos();
			this.currentPath.advance();
			double d0 = (double) vec3i.getX();
			double d1 = (double) vec3i.getZ();
			double d2;
			
			do {
				d2 = (double) ((float) vec3i.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
			} while (d2 < (double) vec3i.getY());
			
			this.targetLocation = new Vector3d(d0, d2, d1);
		}
	}
}