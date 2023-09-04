package yesman.epicfight.api.utils.math;

import net.minecraft.util.Mth;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

public class WeightFormulars {
	public static float getAttackSpeed(float weight, float weaponAttackSpeed, EntityPatch<?> entitypatch) {
		if (weight > 40.0F) {
			float attenuation = Mth.clamp(entitypatch.getOriginal().level().getGameRules().getInt(EpicFightGamerules.WEIGHT_PENALTY), 0, 100) / 100.0F;
			return weaponAttackSpeed + (-0.1F * (weight / 40.0F) * (Math.max(weaponAttackSpeed - 0.8F, 0.0F) * 1.5F) * attenuation);
		} else { 
			return weaponAttackSpeed;
		}
	}
	
	public static float getStaminaConsume(double weight, float originalConsumption, EntityPatch<?> entitypatch) {
		float attenuation = Mth.clamp(entitypatch.getOriginal().level().getGameRules().getInt(EpicFightGamerules.WEIGHT_PENALTY), 0, 100) / 100.0F;
		return ((float)(weight / 40.0F - 1.0F) * attenuation + 1.0F) * originalConsumption;
	}
}