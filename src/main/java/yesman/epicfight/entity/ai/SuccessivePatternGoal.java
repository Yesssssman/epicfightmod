package yesman.epicfight.entity.ai;

import java.util.List;

import net.minecraft.entity.MobEntity;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.mob.BipedMobData;

public class SuccessivePatternGoal extends AttackPatternGoal {
	public SuccessivePatternGoal(BipedMobData<?> mobdata, MobEntity attacker, double patternMinRange, boolean affectHorizon,
			double patternMaxRange, boolean successive, List<AttackAnimation> pattern) {
		super(mobdata, attacker, patternMinRange, patternMaxRange, affectHorizon, pattern);
	}
	
	@Override
	protected boolean canExecuteAttack() {
    	return !this.mobdata.getEntityState().isInaction() || this.mobdata.getEntityState().canExecuteSkill();
    }
	
	@Override
	public void resetTask() {
		this.patternCounter = 0;
    }
}