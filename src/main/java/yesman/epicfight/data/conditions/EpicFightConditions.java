package yesman.epicfight.data.conditions;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightConditions {
	public static final DeferredRegister<Function<CompoundTag, Condition<?>>> CONDITIONS = DeferredRegister.create(new ResourceLocation(EpicFightMod.MODID, "conditions"), EpicFightMod.MODID);
	public static final Supplier<IForgeRegistry<Function<CompoundTag, Condition<?>>>> CONDITIONS_REGISTRY = CONDITIONS.makeRegistry(RegistryBuilder::new);
	
	public static final RegistryObject<Function<CompoundTag, Condition<?>>> OFFHAND_CATEGORY_CHECK = CONDITIONS.register(new ResourceLocation(EpicFightMod.MODID, "dummy_animation").getPath(), () -> OffhandCategoryCondition::new);
}
