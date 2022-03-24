package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class DragonGroundBattlePhase extends PatchedDragonPhase {
	private List<Player> recognizedPlayers = Lists.newArrayList();
	private CombatBehaviors combatBehaviors;
	private int flyingCooldown;
	
	public DragonGroundBattlePhase(EnderDragon dragon) {
		super(dragon);
		
		if (!dragon.level.isClientSide()) {
			this.combatBehaviors = MobCombatBehaviors.ENDER_DRAGON_ATTACKS.build(this.dragonpatch);
		}
	}
	
	@Override
	public void begin() {
		this.dragonpatch.setGroundPhase();
	}
	
	@Override
	public void doServerTick() {
		if (this.flyingCooldown > 0) {
			this.flyingCooldown--;
		}
		
		LivingEntity target = this.dragon.getTarget();
		
		if (target != null) {
			if (isValidTarget(target) && isInEndSpikes(target)) {
				EntityState state = this.dragonpatch.getEntityState();
				this.combatBehaviors.tick();
				
				if (this.combatBehaviors.hasActivatedMove()) {
					if (state.basicAttackPossible()) {
						CombatBehaviors.Behavior result = this.combatBehaviors.tryProceed();
						
						if (result != null) {
							result.execute(this.dragonpatch, this.combatBehaviors);
						} else {
							this.combatBehaviors.cancel();
						}
					}
				} else {
					if (!state.inaction()) {
						CombatBehaviors.Behavior result = this.combatBehaviors.selectRandomBehaviorSeries();
						
						if (result != null) {
							result.execute(this.dragonpatch, this.combatBehaviors);
						} else {
							double dx = target.getX() - this.dragon.getX();
							double dz = target.getZ() - this.dragon.getZ();
							float yRot = 180.0F - (float) Math.toDegrees(Mth.atan2(dx, dz));
							this.dragon.setYRot(MathUtils.rotlerp(this.dragon.getYRot(), yRot, 6.0F));
							Vec3 forward = this.dragon.getForward().scale(-0.25F);
							this.dragon.move(MoverType.SELF, forward);
						}
						
						if (this.dragon.getHealth() / this.dragon.getMaxHealth() < 0.6F) {
							int crystalAlive = this.dragon.getDragonFight().getCrystalsAlive();
							float flyingChance = (float)crystalAlive * 0.1F;
							float f = this.dragon.getRandom().nextFloat();
							
							if (f < flyingChance) {
								if (this.flyingCooldown <= 0) {
									this.flyingCooldown = 1200;
									this.dragonpatch.playAnimationSynchronized(Animations.DRAGON_GROUND_TO_FLY, 0.0F);
									this.dragon.getPhaseManager().setPhase(PatchedPhases.FLYING);
									((DragonFlyingPhase)this.dragon.getPhaseManager().getCurrentPhase()).enableAirstrike();
								}
							} else if (crystalAlive != 0) {
								this.dragon.getPhaseManager().setPhase(PatchedPhases.CRYSTAL_LINK);
							}
						}
					}
				}
			} else {
				this.searchNewTarget();
			}
		} else {
			this.searchNewTarget();
			
			if (this.dragon.getTarget() == null && !this.dragonpatch.getEntityState().inaction()) {
				this.dragonpatch.playAnimationSynchronized(Animations.DRAGON_GROUND_TO_FLY, 0.0F);
				this.dragon.getPhaseManager().setPhase(PatchedPhases.FLYING);
				((DragonFlyingPhase)this.dragon.getPhaseManager().getCurrentPhase()).enableAirstrike();
			}
		}
	}
	
	private void refreshNearbyPlayers(double within) {
		this.recognizedPlayers.clear();
		this.recognizedPlayers.addAll(this.getPlayersWithin(within));
	}
	
	private void searchNewTarget() {
		this.refreshNearbyPlayers(60.0F);
		
		for (Player player : this.recognizedPlayers) {
			if (isValidTarget(player) && isInEndSpikes(player)) {
				this.dragonpatch.setAttakTargetSync(player);
				return;
			}
		}
		
		this.dragonpatch.setAttakTargetSync(null);
	}
	
	@Override
	public boolean isSitting() {
		return true;
	}
	
	@Override
	public EnderDragonPhase<? extends DragonPhaseInstance> getPhase() {
		return PatchedPhases.GROUND_BATTLE;
	}
}