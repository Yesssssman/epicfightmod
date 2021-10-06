package yesman.epicfight.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.LootFunctionManager;
import yesman.epicfight.gamedata.Skills;

public class SetSkillRandom extends LootFunction {
	private SetSkillRandom(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}
	
	@Override
	public LootFunctionType getFunctionType() {
		return LootFunctionManager.SET_NBT;
	}
	
	@Override
	public ItemStack doApply(ItemStack stack, LootContext context) {
		stack.getOrCreateTag().putString("skill", Skills.getRandomModifiableSkillName());
		return stack;
	}

	public static LootFunction.Builder<?> builder() {
		return builder((conditions) -> {
			return new SetSkillRandom(conditions);
		});
	}
	
	public static class Serializer extends LootFunction.Serializer<SetSkillRandom> {
		@Override
		public SetSkillRandom deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
			return new SetSkillRandom(conditionsIn);
		}
	}
}