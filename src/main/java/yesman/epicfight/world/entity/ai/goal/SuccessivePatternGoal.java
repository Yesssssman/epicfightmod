package yesman.epicfight.world.entity.ai.goal;

import java.util.List;

import net.minecraft.world.entity.Mob;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.mob.HumanoidMobPatch;

public class SuccessivePatternGoal extends AttackPatternGoal {
	public SuccessivePatternGoal(HumanoidMobPatch<?> mobpatch, Mob attacker, double patternMinRange, boolean affectHorizon, double patternMaxRange, boolean successive, List<AttackAnimation> pattern) {
		super(mobpatch, attacker, patternMinRange, patternMaxRange, affectHorizon, pattern);
	}
	
	@Override
	protected boolean canExecuteAttack() {
    	return !this.mobpatch.getEntityState().inaction() || this.mobpatch.getEntityState().canUseSkill();
    }
	
	@Override
	public void stop() {
		this.patternCounter = 0;
    }
}