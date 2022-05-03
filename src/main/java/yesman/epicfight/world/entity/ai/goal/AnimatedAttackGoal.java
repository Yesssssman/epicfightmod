package yesman.epicfight.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class AnimatedAttackGoal<T extends MobPatch<?>> extends MeleeAttackGoal {
	protected final T mobpatch;
	protected final CombatBehaviors<T> combatBehaviors;
	protected final double attackRadiusSqr;
	
	public AnimatedAttackGoal(T mobpatch, CombatBehaviors<T> combatBehaviors, PathfinderMob pathfinderMob, double speedModifier, boolean longMemory) {
		this(mobpatch, combatBehaviors, pathfinderMob, speedModifier, longMemory, 0.0D);
	}
	
	public AnimatedAttackGoal(T mobpatch, CombatBehaviors<T> combatBehaviors, PathfinderMob pathfinderMob, double speedModifier, boolean longMemory, double attackRadius) {
		super(pathfinderMob, speedModifier, longMemory);
		this.mobpatch = mobpatch;
		this.combatBehaviors = combatBehaviors;
		this.attackRadiusSqr = attackRadius * attackRadius;
	}
	
	@Override
	public void tick() {
		LivingEntity livingentity = this.mob.getTarget();
		
		if (livingentity != null) {
			double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
			
			if (!(d0 > (double) this.attackRadiusSqr)) {
				this.mob.getNavigation().stop();
				this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
			} else {
				super.tick();
			}
		}
	}
	
	@Override
	protected void checkAndPerformAttack(LivingEntity target, double p_25558_) {
		EntityState state = this.mobpatch.getEntityState();
		this.combatBehaviors.tick();
		
		if (this.combatBehaviors.hasActivatedMove()) {
			if (state.canBasicAttack()) {
				CombatBehaviors.Behavior<T> result = this.combatBehaviors.tryProceed();
				
				if (result != null) {
					result.execute(this.mobpatch);
				}
			}
		} else {
			if (!state.inaction()) {
				CombatBehaviors.Behavior<T> result = this.combatBehaviors.selectRandomBehaviorSeries();
				
				if (result != null) {
					result.execute(this.mobpatch);
				}
			}
		}
	}
}