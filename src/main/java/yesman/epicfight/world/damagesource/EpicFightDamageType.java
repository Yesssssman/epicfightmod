package yesman.epicfight.world.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import yesman.epicfight.main.EpicFightMod;

public interface EpicFightDamageType {
	
	/**
	 * Decides if damage source can hurt the entity that is lying on ground
	 */
	TagKey<DamageType> FINISHER = create("finisher");
	
	/**
	 * Decides if damage source can neutralize the entity that is invulnerable
	 */
	TagKey<DamageType> COUNTER = create("counter");

	/**
	 * Decides if damage source type is execution
	 */
	TagKey<DamageType> EXECUTION = create("execution");
	
	/**
	 * Decides if damage source is weapon innate attack
	 */
	TagKey<DamageType> WEAPON_INNATE = create("weapon_innate");
	
	/**
	 * Decides if damage source can ignore guard
	 */
	TagKey<DamageType> GUARD_PUNCTURE = create("guard_puncture");
	
	private static TagKey<DamageType> create(String tagName) {
		return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(EpicFightMod.MODID, tagName));
	}
}