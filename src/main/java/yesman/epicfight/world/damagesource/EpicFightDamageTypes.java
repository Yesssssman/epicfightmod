package yesman.epicfight.world.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import yesman.epicfight.main.EpicFightMod;

public interface EpicFightDamageTypes {
	ResourceKey<DamageType> SHOCKWAVE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(EpicFightMod.MODID, "shockwave"));
	ResourceKey<DamageType> WITHER_BEAM = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(EpicFightMod.MODID, "wither_beam"));
}