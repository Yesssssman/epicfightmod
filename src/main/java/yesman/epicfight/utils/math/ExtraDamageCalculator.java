package yesman.epicfight.utils.math;

import net.minecraft.entity.LivingEntity;

public class ExtraDamageCalculator {
	public static final CalculatorType PERCENT_OF_TARGET_LOST_HEALTH = new CalculatorType((attacker, target, arg) ->
		(target.getMaxHealth() - target.getHealth()) * arg, "skill.epicfight.percent_of_target_lost_health");
	
	public static ExtraDamageCalculator get(CalculatorType calculator, float argument) {
		return new ExtraDamageCalculator(calculator, argument);
	}
	
	private CalculatorType calculator;
	private float argument;
	
	public ExtraDamageCalculator(CalculatorType calculator, float argument) {
		this.calculator = calculator;
		this.argument = argument;
	}
	
	public float getArgument() {
		return this.argument;
	}
	
	public float get(LivingEntity attacker, LivingEntity target) {
		return this.calculator.extraDamage.getBonusDamage(attacker, target, this.argument);
	}
	
	@Override
	public String toString() {
		return this.calculator.tooltip;
	}
	
	static class CalculatorType {
		IExtraDamage extraDamage;
		String tooltip;
		
		public CalculatorType(IExtraDamage extraDamage, String tooltip) {
			this.extraDamage = extraDamage;
			this.tooltip = tooltip;
		}
	}
}
