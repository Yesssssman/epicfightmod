package maninhouse.epicfight.utils.math;

import maninhouse.epicfight.capabilities.entity.CapabilityEntity;
import maninhouse.epicfight.world.ModGamerules;
import net.minecraft.util.math.MathHelper;

public class Formulars {
	public static float getAttackSpeedPenalty(float weight, float weaponAttackSpeed, CapabilityEntity<?> entity) {
		if (weight > 40.0F) {
			float attenuation = MathHelper.clamp(entity.getOriginalEntity().world.getGameRules().getInt(ModGamerules.ATTACK_SPEED_PENALTY), 0, 100) / 100.0F;
			return weaponAttackSpeed + (-0.1F * (weight / 40.0F) * (Math.max(weaponAttackSpeed - 0.8F, 0.0F) * 1.5F) * attenuation);
		} else { 
			return weaponAttackSpeed;
		}
	}
	
	public static float getStaminarConsumePenalty(double weight, float originalConsumption) {
		return (float)(weight / 40.0F) * originalConsumption;
	}
}