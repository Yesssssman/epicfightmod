package yesman.epicfight.world.entity.decoration;

import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightPaintingVariants {
	public static final DeferredRegister<PaintingVariant> PAINTING_VARIANTS = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, EpicFightMod.MODID);
	
	public static final RegistryObject<PaintingVariant> EPICFIGHT_LOGO = PAINTING_VARIANTS.register("epicfight_logo", () -> new PaintingVariant(16, 16));
	public static final RegistryObject<PaintingVariant> EPICFIGHT_LOGO_X2 = PAINTING_VARIANTS.register("epicfight_logo_x2", () -> new PaintingVariant(32, 32));
	public static final RegistryObject<PaintingVariant> EPICFIGHT_LOGO_X3 = PAINTING_VARIANTS.register("epicfight_logo_x3", () -> new PaintingVariant(48, 48));
	public static final RegistryObject<PaintingVariant> EPICFIGHT_LOGO_X4 = PAINTING_VARIANTS.register("epicfight_logo_x4", () -> new PaintingVariant(64, 64));
}
