package maninhouse.epicfight.entity.ai;

import java.util.List;

import maninhouse.epicfight.animation.types.AttackAnimation;
import maninhouse.epicfight.capabilities.entity.MobData;
import net.minecraft.entity.MobEntity;

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