package yesman.epicfight.data.loot;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import yesman.epicfight.data.loot.function.SetSkillFunction;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightLootModifiers {
	public static void registerGlobalLootModifier(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		event.getRegistry().registerAll(
				new SkillBookLootModifier.Serializer().setRegistryName(new ResourceLocation(EpicFightMod.MODID, "skillbook_loot_table_modifier"))
		);
	}
	
	public static void registerLootItemFunctionType() {
		Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(EpicFightMod.MODID, "set_skill"), new LootItemFunctionType(new SetSkillFunction.Serializer()));
	}
}