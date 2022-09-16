package yesman.epicfight.world.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightMobEffects {
	public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, EpicFightMod.MODID);
	
	public static final RegistryObject<Effect> STUN_IMMUNITY = EFFECTS.register("stun_immunity", () -> new VisibleMobEffect(EffectType.BENEFICIAL, "stun_immunity", 16758016));
	public static final RegistryObject<Effect> BLOOMING = EFFECTS.register("blooming", () -> new VisibleMobEffect(EffectType.BENEFICIAL, "blooming", 16735744));
}