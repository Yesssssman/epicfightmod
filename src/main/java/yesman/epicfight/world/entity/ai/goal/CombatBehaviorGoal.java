package yesman.epicfight.world.entity.ai.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class CombatBehaviorGoal<T extends MobPatch<?>> extends Goal {
	protected final Mob mob;
	protected final T mobpatch;
	protected final CombatBehaviors<T> combatBehaviors;
	
	public CombatBehaviorGoal(T mobpatch, CombatBehaviors<T> combatBehaviors) {
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
    	return this.canUse() && !this.mobpatch.getEntityState().hurt();
    }
	
	@Override
	public void tick() {
		if (this.isValidTarget(this.mob.getTarget())) {
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
    
	protected boolean isValidTarget(LivingEntity attackTarget) {
    	return attackTarget != null && attackTarget.isAlive() && !attackTarget.isSpectator() && !(attackTarget instanceof Player && ((Player)attackTarget).isCreative());
    }
}