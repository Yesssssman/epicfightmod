package yesman.epicfight.utils.math;

import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface IExtraDamage {
	float getBonusDamage(LivingEntity attacker, LivingEntity target, float arg);
}