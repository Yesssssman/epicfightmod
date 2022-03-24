package yesman.epicfight.world.entity.ai.goal;

import java.util.List;

import net.minecraft.world.entity.Mob;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class AttackPatternPercentGoal extends AttackPatternGoal {
	protected final float executeChance;
	
	public AttackPatternPercentGoal(MobPatch<?> mobpatch, Mob attacker, double patternMinRange, double patternMaxRange, float chance, boolean affectHorizon,
			List<AttackAnimation> pattern) {
		super(mobpatch, attacker, patternMinRange, patternMaxRange, affectHorizon, pattern);
		this.executeChance = chance;
	}
	
	@Override
	public boolean canUse() {
		return super.canUse() && attacker.getRandom().nextFloat() < executeChance;
    }
}