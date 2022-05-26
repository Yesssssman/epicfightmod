package yesman.epicfight.data.loot;

import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.world.item.EpicFightItems;
import yesman.epicfight.world.item.SkillBookItem;

public class SkillBookModifier extends LootModifier {
	protected SkillBookModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}
	
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		if (ConfigManager.SKILLBOOK_MOB_LOOT.get() && entity instanceof Monster) {
			Random random = new Random();
			if (random.nextFloat() < 0.025F) {
				ItemStack skillBook = new ItemStack(EpicFightItems.SKILLBOOK.get());
				SkillBookItem.setContainingSkill(Skills.getRandomModifiableSkillName(), skillBook);
				generatedLoot.add(skillBook);
			}
		}
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<SkillBookModifier> {
        @Override
        public SkillBookModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            return new SkillBookModifier(conditionsIn);
        }
        
        @Override
        public JsonObject write(SkillBookModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            return json;
        }
    }
}