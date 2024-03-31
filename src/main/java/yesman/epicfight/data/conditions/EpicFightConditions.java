package yesman.epicfight.data.conditions;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.data.conditions.entity.OffhandCategoryCondition;
import yesman.epicfight.data.conditions.itemstack.TagValueCondition;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightConditions {
	public static final DeferredRegister<Supplier<Condition<?>>> CONDITIONS = DeferredRegister.create(new ResourceLocation(EpicFightMod.MODID, "conditions"), EpicFightMod.MODID);
	public static final Supplier<IForgeRegistry<Supplier<Condition<?>>>> REGISTRY = CONDITIONS.makeRegistry(RegistryBuilder::new);
	
	public static <T extends Condition<?>> Supplier<T> getConditionOrThrow(ResourceLocation key) {
		if (!REGISTRY.get().containsKey(key)) {
			throw new IllegalArgumentException("No condition named " + key);
		}
		
		return getConditionOrNull(key);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Condition<?>> Supplier<T> getConditionOrNull(ResourceLocation key) {
		return (Supplier<T>) REGISTRY.get().getValue(key);
	}
	
	public static final RegistryObject<Supplier<Condition<?>>> OFFHAND_CATEGORY = CONDITIONS.register(new ResourceLocation(EpicFightMod.MODID, "offhand_category").getPath(), () -> OffhandCategoryCondition::new);
	public static final RegistryObject<Supplier<Condition<?>>> TAG_VALUE = CONDITIONS.register(new ResourceLocation(EpicFightMod.MODID, "tag_value").getPath(), () -> TagValueCondition::new);
}
