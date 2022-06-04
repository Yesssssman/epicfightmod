package yesman.epicfight.data.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import yesman.epicfight.gameasset.Skills;

public class SetRandomSkillFunction implements LootItemFunction {
	@Override
	public LootItemFunctionType getType() {
		return LootItemFunctions.SET_NBT;
	}
	
	@Override
	public ItemStack apply(ItemStack t, LootContext u) {
		t.getOrCreateTag().putString("skill", Skills.getRandomLearnableSkillName());
		return t;
	}

	public static LootItemFunction.Builder builder() {
		return new LootItemFunction.Builder() {
			public LootItemFunction build() {
				return new SetRandomSkillFunction();
			}
		};
	}
	
	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<SetRandomSkillFunction> {
		@Override
		public void serialize(JsonObject p_79325_, SetRandomSkillFunction p_79326_, JsonSerializationContext p_79327_) {
			;
		}
		
		@Override
		public SetRandomSkillFunction deserialize(JsonObject p_79323_, JsonDeserializationContext p_79324_) {
			return new SetRandomSkillFunction();
		}
	}
}