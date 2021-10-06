package yesman.epicfight.entity.ai;

import java.util.List;

import net.minecraft.entity.MobEntity;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.MobData;

public class AttackPatternPercentGoal extends AttackPatternGoal
{
	protected final float executeChance;
	
	public AttackPatternPercentGoal(MobData<?> mobdata, MobEntity attacker, double patternMinRange, double patternMaxRange, float chance, boolean affectHorizon,
			List<AttackAnimation> pattern)
	{
		super(mobdata, attacker, patternMinRange, patternMaxRange, affectHorizon, pattern);
		this.executeChance = chance;
	}
	
	@Override
    public boolean shouldExecute()
    {
		return super.shouldExecute() && attacker.getRNG().nextFloat() < executeChance;
    }
}