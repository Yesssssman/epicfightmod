package yesman.epicfight.utils.math;

import net.minecraft.util.math.MathHelper;
import yesman.epicfight.capabilities.entity.CapabilityEntity;
import yesman.epicfight.world.ModGamerules;

public class Formulars {
	public static float getAttackSpeedPenalty(float weight, float weaponAttackSpeed, CapabilityEntity<?> entitydata) {
		if (weight > 40.0F) {
			float attenuation = MathHelper.clamp(entitydata.getOriginalEntity().world.getGameRules().getInt(ModGamerules.WEIGHT_PENALTY), 0, 100) / 100.0F;
			return weaponAttackSpeed + (-0.1F * (weight / 40.0F) * (Math.max(weaponAttackSpeed - 0.8F, 0.0F) * 1.5F) * attenuation);
		} else { 
			return weaponAttackSpeed;
		}
	}
	
	public static float getStaminarConsumePenalty(double weight, float originalConsumption, CapabilityEntity<?> entitydata) {
		float attenuation = MathHelper.clamp(entitydata.getOriginalEntity().world.getGameRules().getInt(ModGamerules.WEIGHT_PENALTY), 0, 100) / 100.0F;
		return ((float)(weight / 40.0F - 1.0F) * attenuation + 1.0F) * originalConsumption;
	}
}