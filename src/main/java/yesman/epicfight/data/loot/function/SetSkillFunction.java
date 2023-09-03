package yesman.epicfight.data.loot.function;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.skill.Skill;

public class SetSkillFunction implements LootItemFunction {
	private final List<Pair<Float, String>> skillsAndWeight;
	
	public SetSkillFunction(List<Pair<Float, String>> skillsAndWeight) {
		this.skillsAndWeight = skillsAndWeight;
	}
	
	private Skill getSkillForSeed(float seed) {
		for (Pair<Float, String> pair : this.skillsAndWeight) {
			if (seed < pair.getFirst()) {
				return SkillManager.getSkill(pair.getSecond());
			}
		}
		
		return SkillManager.getSkill(this.skillsAndWeight.get(0).getSecond());
	}
	
	@Override
	public ItemStack apply(ItemStack itemstack, LootContext lootContext) {
		float val = lootContext.getRandom().nextFloat();
		itemstack.getOrCreateTag().putString("skill", this.getSkillForSeed(val).toString());
		
		return itemstack;
	}
	
	public static LootItemFunction.Builder builder(String... skills) {
		return new LootItemFunction.Builder() {
			public LootItemFunction build() {
				List<Pair<Float, String>> list = Lists.newArrayList();
				float weight = 1.0F / skills.length;
				float weightSum = 0.0F;
				
				for (String skill : skills) {
					weightSum += weight;
					list.add(Pair.of(weightSum, skill));
				}
				
				return new SetSkillFunction(list);
			}
		};
	}
	
	public static LootItemFunction.Builder builder(Object... skillAndWeight) {
		return new LootItemFunction.Builder() {
			public LootItemFunction build() {
				List<Pair<Float, String>> list = Lists.newArrayList();
				float weightTotal = 0.0F;
				float weightSum = 0.0F;
				
				for (int i = 0; i < skillAndWeight.length / 2; i++) {
					weightTotal += (float)skillAndWeight[i * 2];
				}
				
				for (int i = 0; i < skillAndWeight.length / 2; i++) {
					weightSum += (float)skillAndWeight[i * 2];
					list.add(Pair.of(weightSum / weightTotal, (String)skillAndWeight[i * 2 + 1]));
				}
				
				return new SetSkillFunction(list);
			}
		};
	}
	
	@Override
	public LootItemFunctionType getType() {
		return LootItemFunctions.SET_NBT;
	}
	
	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<SetSkillFunction> {
		@Override
		public void serialize(JsonObject jsonObj, SetSkillFunction skillFunction, JsonSerializationContext jsonDeserializationContext) {
			JsonArray skillArray = new JsonArray();
			JsonArray weightArray = new JsonArray();
			
			for (Pair<Float, String> pair : skillFunction.skillsAndWeight) {
				skillArray.add(pair.getSecond());
				weightArray.add(pair.getFirst());
			}
			
			jsonObj.add("skills", skillArray);
			jsonObj.add("weights", weightArray);
		}
		
		@Override
		public SetSkillFunction deserialize(JsonObject jsonObj, JsonDeserializationContext jsonDeserializationContext) {
			JsonArray skillArray = jsonObj.getAsJsonArray("skills");
			JsonArray weightArray = jsonObj.getAsJsonArray("weights");
			List<Pair<Float, String>> list = Lists.newArrayList();
			
			float totalWeights = 0.0F;
			
			for (int i = 0; i < skillArray.size(); i++) {
				totalWeights += weightArray.get(i).getAsFloat();
			}
			
			for (int i = 0; i < skillArray.size(); i++) {
				list.add(Pair.of(weightArray.get(i).getAsFloat() / totalWeights, skillArray.get(i).getAsString()));
			}
			
			return new SetSkillFunction(list);
		}
	}
}