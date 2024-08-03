package yesman.epicfight.data.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightLootModifiers {
	public static void registerGlobalLootModifier(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		event.getRegistry().registerAll(
				new SkillBookLootModifier.Serializer().setRegistryName(new ResourceLocation(EpicFightMod.MODID, "skillbook_loot_table_modifier"))
		);
	}
}