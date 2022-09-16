package yesman.epicfight.data.loot;

import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.world.item.EpicFightItems;
import yesman.epicfight.world.item.SkillBookItem;

public class SkillBookModifier extends LootModifier {
	protected SkillBookModifier(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}
	
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		Entity entity = context.getParamOrNull(LootParameters.THIS_ENTITY);
		if (ConfigManager.SKILLBOOK_MOB_LOOT.get() && entity instanceof MonsterEntity) {
			Random random = new Random();
			if (random.nextFloat() < 0.025F) {
				ItemStack skillBook = new ItemStack(EpicFightItems.SKILLBOOK.get());
				SkillBookItem.setContainingSkill(Skills.getRandomLearnableSkillName(), skillBook);
				generatedLoot.add(skillBook);
			}
		}
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<SkillBookModifier> {
        @Override
        public SkillBookModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            return new SkillBookModifier(conditionsIn);
        }
        
        @Override
        public JsonObject write(SkillBookModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            return json;
        }
    }
}