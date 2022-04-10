package yesman.epicfight.data.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import yesman.epicfight.gameasset.Skills;

public class SetSkillRandom extends LootItemConditionalFunction {
	private SetSkillRandom(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}
	
	@Override
	public LootItemFunctionType getType() {
		return LootItemFunctions.SET_NBT;
	}
	
	@Override
	public ItemStack run(ItemStack stack, LootContext context) {
		stack.getOrCreateTag().putString("skill", Skills.getRandomModifiableSkillName());
		return stack;
	}

	public static LootItemConditionalFunction.Builder<?> builder() {
		return simpleBuilder((conditions) -> {
			return new SetSkillRandom(conditions);
		});
	}
	
	public static class Serializer extends LootItemConditionalFunction.Serializer<SetSkillRandom> {
		@Override
		public SetSkillRandom deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
			return new SetSkillRandom(conditionsIn);
		}
	}
}