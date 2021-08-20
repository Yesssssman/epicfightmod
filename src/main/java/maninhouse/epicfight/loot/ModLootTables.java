package maninhouse.epicfight.loot;

import maninhouse.epicfight.item.ModItems;
import maninhouse.epicfight.loot.function.SetSkillRandom;
import net.minecraft.item.Items;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.RandomValueRange;
import net.minecraftforge.event.LootTableLoadEvent;

public class ModLootTables {
	public static void modifyVanillaLootPools(final LootTableLoadEvent event) {
    	if (event.getName().equals(LootTables.CHESTS_DESERT_PYRAMID)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 2.0F))
    			.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(1).acceptFunction(SetSkillRandom.builder()))
    			.addEntry(ItemLootEntry.builder(Items.AIR).weight(1))
    		.build());
    		
    		event.getTable().addPool(LootPool.builder()
    				.addEntry(ItemLootEntry.builder(ModItems.KATANA.get()).weight(1))
    				.addEntry(ItemLootEntry.builder(Items.AIR).weight(3))
    		.build());
    	}
    	
    	if (event.getName().equals(LootTables.CHESTS_JUNGLE_TEMPLE)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 4.0F))
        		.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(1).acceptFunction(SetSkillRandom.builder()))
        		.addEntry(ItemLootEntry.builder(Items.AIR).weight(1))
        	.build());
    		
    		event.getTable().addPool(LootPool.builder()
    				.addEntry(ItemLootEntry.builder(ModItems.KATANA.get()).weight(1))
    				.addEntry(ItemLootEntry.builder(Items.AIR).weight(3))
    		.build());
    	}
    	
    	if (event.getName().equals(LootTables.CHESTS_SIMPLE_DUNGEON)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 3.0F))
        		.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(3).acceptFunction(SetSkillRandom.builder()))
        		.addEntry(ItemLootEntry.builder(Items.AIR).weight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.CHESTS_ABANDONED_MINESHAFT)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 3.0F))
        		.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(3).acceptFunction(SetSkillRandom.builder()))
        		.addEntry(ItemLootEntry.builder(Items.AIR).weight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.CHESTS_PILLAGER_OUTPOST)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 3.0F))
        		.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(3).acceptFunction(SetSkillRandom.builder()))
        		.addEntry(ItemLootEntry.builder(Items.AIR).weight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.CHESTS_UNDERWATER_RUIN_BIG)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 3.0F))
        		.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(3).acceptFunction(SetSkillRandom.builder()))
        		.addEntry(ItemLootEntry.builder(Items.AIR).weight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.CHESTS_SHIPWRECK_MAP)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 2.0F))
        		.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(1).acceptFunction(SetSkillRandom.builder()))
        		.addEntry(ItemLootEntry.builder(Items.AIR).weight(2))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.CHESTS_STRONGHOLD_LIBRARY)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 5.0F))
    				.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(1).acceptFunction(SetSkillRandom.builder()))
    				.addEntry(ItemLootEntry.builder(Items.AIR).weight(1))
    		.build());
    	}
    	
    	if (event.getName().equals(LootTables.CHESTS_WOODLAND_MANSION)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 5.0F))
    				.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(1).acceptFunction(SetSkillRandom.builder()))
    				.addEntry(ItemLootEntry.builder(Items.AIR).weight(1))
    		.build());
    	}
    	
    	if (event.getName().equals(LootTables.BASTION_OTHER)) {
    		event.getTable().addPool(LootPool.builder().rolls(RandomValueRange.of(1.0F, 4.0F))
    				.addEntry(ItemLootEntry.builder(ModItems.SKILLBOOK.get()).weight(1).acceptFunction(SetSkillRandom.builder()))
    				.addEntry(ItemLootEntry.builder(Items.AIR).weight(1))
    		.build());
    	}
    }
}
