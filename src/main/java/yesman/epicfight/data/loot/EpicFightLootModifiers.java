package yesman.epicfight.data.loot;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.main.EpicFightMod;

import java.util.function.Supplier;

public class EpicFightLootModifiers {
	private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM =
			DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EpicFightMod.MODID);
	private static final DeferredRegister<EntityDataSerializer<?>> EDS =
			DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, EpicFightMod.MODID);

	public static void register(IEventBus bus) {
		GLM.register("skillbook_modifier", SkillBookModifier.CODEC);
		GLM.register(bus);
		EDS.register("skillbook_modifier", (Supplier<EntityDataSerializer<?>>) () ->
				EntityDataSerializer.simple((friendlyByteBuf, instance) ->
						friendlyByteBuf.writeUtf(SkillBookModifier.CODEC.get().encodeStart(JsonOps.COMPRESSED, instance)
						.getOrThrow(false, error -> {
							throw new IllegalArgumentException("Error encoding SkillBookModifier: " + error);
						}).toString()), friendlyByteBuf -> {
			String json = friendlyByteBuf.readUtf();
			return SkillBookModifier.CODEC.get().decode(JsonOps.COMPRESSED, JsonParser.parseString(json)).getOrThrow(false, error -> {
				throw new IllegalArgumentException("Error decoding custom entity data: " + error);
			}).getFirst();
		}));
		EDS.register(bus);
	}
}