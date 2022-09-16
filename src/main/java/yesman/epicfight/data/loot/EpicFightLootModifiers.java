package yesman.epicfight.data.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightLootModifiers {
	public static void register(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		event.getRegistry().registerAll(
				new SkillBookModifier.Serializer().setRegistryName(new ResourceLocation(EpicFightMod.MODID, "skillbook_modifier"))
		);
	}
}