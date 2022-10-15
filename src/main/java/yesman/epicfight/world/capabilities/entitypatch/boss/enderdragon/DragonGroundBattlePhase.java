package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Region;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class DragonGroundBattlePhase extends PatchedDragonPhase {
	private List<PlayerEntity> recognizedPlayers = Lists.newArrayList();
	private PathFinder pathFinder;
	private int aggroCounter;
	private int noPathWarningCounter;
	CombatBehaviors<EnderDragonPatch> combatBehaviors;
	
	public DragonGroundBattlePhase(EnderDragonEntity dragon) {
		super(dragon);
		
		if (!dragon.level.isClientSide()) {
			this.combatBehaviors = MobCombatBehaviors.ENDER_DRAGON.build(this.dragonpatch);
			NodeProcessor nodeEvaluator = new WalkNodeProcessor();
			nodeEvaluator.setCanPassDoors(true);
		    this.pathFinder = new PathFinder(nodeEvaluator, 100);
		}
	}
	
	@Override
	public void begin() {
		this.dragonpatch.setGroundPhase();
	}
	
	@Override
	public void doServerTick() {
		LivingEntity target = this.dragon.getTarget();
		
		if (target != null) {
			if (isValidTarget(target) && isInEndSpikes(target)) {
				EntityState state = this.dragonpatch.getEntityState();
				this.combatBehaviors.tick();
				--this.aggroCounter;
				
				if (this.combatBehaviors.hasActivatedMove()) {
					if (state.canBasicAttack()) {
						CombatBehaviors.Behavior<EnderDragonPatch> result = this.combatBehaviors.tryProceed();
						
						if (result != null) {
							result.execute(this.dragonpatch);
						}
					}
				} else {
					if (!state.inaction()) {
						CombatBehaviors.Behavior<EnderDragonPatch> result = this.combatBehaviors.selectRandomBehaviorSeries();
						
						if (result != null) {
							result.execute(this.dragonpatch);
						} else {
							if (this.dragon.tickCount % 20 == 0) {
								if (!this.checkTargetPath(target)) {
									if (this.noPathWarningCounter++ >= 3) {
										this.fly();
									}
								} else {
									this.noPathWarningCounter = 0;
								}
							}
							
							double dx = target.getX() - this.dragon.getX();
							double dz = target.getZ() - this.dragon.getZ();
							float yRot = 180.0F - (float) Math.toDegrees(MathHelper.atan2(dx, dz));
							this.dragon.yRot = (MathUtils.rotlerp(this.dragon.yRot, yRot, 6.0F));
							Vector3d forward = Vector3d.directionFromRotation(this.dragon.getRotationVector()).scale(-0.25F);
							this.dragon.move(MoverType.SELF, forward);
						}
					} else {
						if (this.aggroCounter < 0) {
							this.aggroCounter = 200;
							this.searchNearestTarget();
						}
					}
				}
			} else {
				if (!this.dragonpatch.getEntityState().inaction()) {
					this.searchNearestTarget();
				}
			}
		} else {
			this.searchNearestTarget();
			
			if (this.dragon.getTarget() == null && !this.dragonpatch.getEntityState().inaction()) {
				this.dragonpatch.playAnimationSynchronized(Animations.DRAGON_GROUND_TO_FLY, 0.0F);
				this.dragon.getPhaseManager().setPhase(PatchedPhases.FLYING);
				((DragonFlyingPhase)this.dragon.getPhaseManager().getCurrentPhase()).enableAirstrike();
			}
		}
	}
	
	@Override
	public float onHurt(DamageSource damagesource, float amount) {
		if (damagesource.isProjectile()) {
			if (damagesource.getDirectEntity() instanceof AbstractArrowEntity) {
				damagesource.getDirectEntity().setSecondsOnFire(1);
			}
			return 0.0F;
		} else if (damagesource instanceof EntityDamageSource && !(damagesource instanceof ExtendedDamageSource)) {
			return 0.0F;
		} else {
			return super.onHurt(damagesource, amount);
		}
	}
	
	private void refreshNearbyPlayers(double within) {
		this.recognizedPlayers.clear();
		this.recognizedPlayers.addAll(this.getPlayersNearbyWithin(within));
	}
	
	private boolean checkTargetPath(LivingEntity target) {
		BlockPos blockpos = this.dragon.blockPosition();
		
		while (this.dragon.level.getBlockState(blockpos).getMaterial().blocksMotion()) {
			blockpos = blockpos.above();
		}
		
		while (!this.dragon.level.getBlockState(blockpos.below()).getMaterial().blocksMotion()) {
			blockpos = blockpos.below();
		}
		
        int sight = 60;
        Region pathnavigationregion = new Region(this.dragon.level, blockpos.offset(-sight, -sight, -sight), blockpos.offset(sight, sight, sight));
        
        Path path = this.pathFinder.findPath(pathnavigationregion, this.dragon, ImmutableSet.of(target.blockPosition()), sight, 0, 1.0F);
        
        BlockPos pathEnd = path.getNode(path.getNodeCount() - 1).asBlockPos();
        BlockPos targetPos = path.getTarget();
        double xd = Math.abs(pathEnd.getX() - targetPos.getX());
        double yd = Math.abs(pathEnd.getY() - targetPos.getY());
        double zd = Math.abs(pathEnd.getZ() - targetPos.getZ());
        
        return xd < this.dragon.getBbWidth() && yd < this.dragon.getBbHeight() && zd < this.dragon.getBbWidth();
	}
	
	private void searchNearestTarget() {
		this.refreshNearbyPlayers(60.0F);
		
		if (this.recognizedPlayers.size() > 0) {
			int nearestPlayerIndex = 0;
			double nearestDistance = this.recognizedPlayers.get(0).distanceToSqr(this.dragon);
			
			for (int i = 1; i < this.recognizedPlayers.size(); i++) {
				double distance = this.recognizedPlayers.get(i).distanceToSqr(this.dragon);
				
				if (distance < nearestDistance) {
					nearestPlayerIndex = i;
					nearestDistance = distance;
				}
			}
			
			PlayerEntity nearestPlayer = this.recognizedPlayers.get(nearestPlayerIndex);
			
			if (isValidTarget(nearestPlayer) && isInEndSpikes(nearestPlayer)) {
				this.dragonpatch.setAttakTargetSync(nearestPlayer);
				return;
			}
		}
		
		this.dragonpatch.setAttakTargetSync(null);
	}
	
	public void fly() {
		this.combatBehaviors.execute(6);
	}
	
	public void resetFlyCooldown() {
		this.combatBehaviors.resetCooldown(6, false);
	}
	
	@Override
	public boolean isSitting() {
		return true;
	}
	
	@Override
	public PhaseType<? extends IPhase> getPhase() {
		return PatchedPhases.GROUND_BATTLE;
	}
}