package maninhouse.epicfight.effects;

import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects {
	public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, EpicFightMod.MODID);
	
	public static final RegistryObject<Effect> STUN_IMMUNITY = EFFECTS.register("stun_immunity", () -> new SuperarmorEffect());
}