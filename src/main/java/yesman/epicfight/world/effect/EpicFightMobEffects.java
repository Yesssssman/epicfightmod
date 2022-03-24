package yesman.epicfight.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightMobEffects {
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, EpicFightMod.MODID);
	
	public static final RegistryObject<MobEffect> STUN_IMMUNITY = EFFECTS.register("stun_immunity", () -> new VisibleMobEffect(MobEffectCategory.BENEFICIAL, "stun_immunity", 16758016));
	public static final RegistryObject<MobEffect> BLOOMING = EFFECTS.register("blooming", () -> new VisibleMobEffect(MobEffectCategory.BENEFICIAL, "blooming", 16735744));
}