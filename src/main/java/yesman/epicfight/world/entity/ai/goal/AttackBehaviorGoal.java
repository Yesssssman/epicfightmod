package yesman.epicfight.world.entity.ai.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class AttackBehaviorGoal extends Goal {
	protected final Mob mob;
	protected final MobPatch<?> mobpatch;
	protected final CombatBehaviors combatBehaviors;
	protected boolean shouldStop;
	
	public AttackBehaviorGoal(MobPatch<?> mobpatch, CombatBehaviors combatBehaviors) {
		this.mob = mobpatch.getOriginal();
		this.mobpatch = mobpatch;
		this.combatBehaviors = combatBehaviors;
		this.setFlags(EnumSet.noneOf(Goal.Flag.class));
	}
	
	@Override
	public boolean canUse() {
		return this.isValidTarget(this.mob.getTarget());
    }
	
    @Override
	public boolean canContinueToUse() {
    	return this.canUse() && !this.mobpatch.getEntityState().hurt() && !this.shouldStop;
    }
    
    @Override
	public void start() {
        this.shouldStop = false;
    }
    
    @Override
	public void stop() {
		this.combatBehaviors.cancel();
	}
	
	@Override
	public void tick() {
		EntityState state = this.mobpatch.getEntityState();
		this.combatBehaviors.tick();
		
		if (this.combatBehaviors.hasActivatedMove()) {
			if (state.basicAttackPossible()) {
				CombatBehaviors.Behavior result = this.combatBehaviors.tryProceed();
				
				if (result != null) {
					result.execute(this.mobpatch, this.combatBehaviors);
				} else {
					this.shouldStop = true;
				}
			}
		} else {
			if (!state.inaction()) {
				CombatBehaviors.Behavior result = this.combatBehaviors.selectRandomBehaviorSeries();
				
				if (result != null) {
					result.execute(this.mobpatch, this.combatBehaviors);
				}
			}
		}
    }
    
	protected boolean isValidTarget(LivingEntity attackTarget) {
    	return attackTarget != null && attackTarget.isAlive() && !attackTarget.isSpectator() && !(attackTarget instanceof Player && ((Player)attackTarget).isCreative());
    }
}