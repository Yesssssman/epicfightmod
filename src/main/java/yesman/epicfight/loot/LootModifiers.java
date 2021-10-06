package yesman.epicfight.loot;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.main.EpicFightMod;

public class LootModifiers {
	public static final DeferredRegister<GlobalLootModifierSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, EpicFightMod.MODID);
	public static final RegistryObject<GlobalLootModifierSerializer<?>> SKILL_BOOK_SERIALIZER = SERIALIZERS.register("skillbook", () -> new SkillBookModifier.Serializer());
}