package yesman.epicfight.data.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.LootFunctionManager;
import yesman.epicfight.gameasset.Skills;

public class SetRandomSkillFunction extends LootFunction {
	protected SetRandomSkillFunction(ILootCondition[] p_i51231_1_) {
		super(p_i51231_1_);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionManager.SET_NBT;
	}
	
	@Override
	public ItemStack run(ItemStack t, LootContext u) {
		t.getOrCreateTag().putString("skill", Skills.getRandomLearnableSkillName());
		return t;
	}
	
	public static LootFunction.Builder<?> setRandomSkill() {
		return simpleBuilder((p_215866_1_) -> {
			return new SetRandomSkillFunction(p_215866_1_);
		});
	}
	
	public static class Serializer extends LootFunction.Serializer<SetRandomSkillFunction> {
		@Override
		public void serialize(JsonObject p_79325_, SetRandomSkillFunction p_79326_, JsonSerializationContext p_79327_) {
			;
		}
		
		@Override
		public SetRandomSkillFunction deserialize(JsonObject p_79323_, JsonDeserializationContext p_79324_, ILootCondition[] p_186530_3_) {
			return new SetRandomSkillFunction(p_186530_3_);
		}
	}
}