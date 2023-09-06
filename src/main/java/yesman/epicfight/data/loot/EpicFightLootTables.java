package yesman.epicfight.data.loot;

import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.data.loot.function.SetSkillFunction;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.item.EpicFightItems;

@Mod.EventBusSubscriber(modid = EpicFightMod.MODID)
public class EpicFightLootTables {
	@SubscribeEvent
	public static void modifyVanillaLootPools(final LootTableLoadEvent event) {
		int modifier = ConfigManager.SKILL_BOOK_CHEST_LOOT_MODIFYER.get();
		int dropChance = 100 + modifier;
		int antiDropChance = 100 - modifier;
		float dropChanceModifier = dropChance / (float)(antiDropChance + dropChance);
		
    	if (event.getName().equals(BuiltInLootTables.DESERT_PYRAMID)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 2.0F))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
    			)).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier)))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.25F))
    			.add(LootItem.lootTableItem(EpicFightItems.UCHIGATANA.get()))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 2.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier))
        	.build());
    		
    		event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.25F))
    			.add(LootItem.lootTableItem(EpicFightItems.UCHIGATANA.get()))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.ABANDONED_MINESHAFT)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_BIG)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.SHIPWRECK_MAP)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 2.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
        		))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.STRONGHOLD_LIBRARY)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 5.0F))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:hypervitality",
    				"epicfight:forbidden_strength",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
    			))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.WOODLAND_MANSION)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 5.0F))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:hypervitality",
    				"epicfight:forbidden_strength",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
    			))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.BASTION_OTHER)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 4.0F))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).apply(SetSkillFunction.builder(
    				"epicfight:berserker",
    				"epicfight:stamina_pillager",
    				"epicfight:technician",
    				"epicfight:swordmaster",
    				"epicfight:hypervitality",
    				"epicfight:forbidden_strength",
    				"epicfight:guard",
    				"epicfight:step",
    				"epicfight:roll"
    			))).when(LootItemRandomChanceCondition.randomChance(dropChanceModifier * 0.3F))
    		.build());
    	}
    }
}
