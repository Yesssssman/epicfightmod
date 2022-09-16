package yesman.epicfight.data.loot;

import net.minecraft.item.Items;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.RandomValueRange;
import net.minecraftforge.event.LootTableLoadEvent;
import yesman.epicfight.data.loot.function.SetRandomSkillFunction;
import yesman.epicfight.world.item.EpicFightItems;

public class EpicFightLootTables {
	public static void modifyVanillaLootPools(final LootTableLoadEvent event) {
    	if (event.getName().equals(LootTables.DESERT_PYRAMID)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 2.0F))
    			.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.setRandomSkill()))
    			.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(1))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
    			.add(ItemLootEntry.lootTableItem(EpicFightItems.KATANA.get()).setWeight(1))
    			.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(3))
    		.build());
    	}
    	
    	if (event.getName().equals(LootTables.JUNGLE_TEMPLE)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 4.0F))
        		.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.setRandomSkill()))
        		.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(1))
        	.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
    			.add(ItemLootEntry.lootTableItem(EpicFightItems.KATANA.get()).setWeight(1))
    			.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(3))
    		.build());
    	}
    	
    	if (event.getName().equals(LootTables.SIMPLE_DUNGEON)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 3.0F))
        		.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(3).apply(SetRandomSkillFunction.setRandomSkill()))
        		.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.ABANDONED_MINESHAFT)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 3.0F))
        		.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(3).apply(SetRandomSkillFunction.setRandomSkill()))
        		.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.PILLAGER_OUTPOST)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 3.0F))
        		.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(3).apply(SetRandomSkillFunction.setRandomSkill()))
        		.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.UNDERWATER_RUIN_BIG)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 3.0F))
        		.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(3).apply(SetRandomSkillFunction.setRandomSkill()))
        		.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.SHIPWRECK_MAP)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 2.0F))
        		.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.setRandomSkill()))
        		.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(2))
        	.build());
    	}
    	
    	if (event.getName().equals(LootTables.STRONGHOLD_LIBRARY)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 5.0F))
    			.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.setRandomSkill()))
    			.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(1))
    		.build());
    	}
    	
    	if (event.getName().equals(LootTables.WOODLAND_MANSION)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 5.0F))
    			.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.setRandomSkill()))
    			.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(1))
    		.build());
    	}
    	
    	if (event.getName().equals(LootTables.BASTION_OTHER)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(RandomValueRange.between(1.0F, 4.0F))
    			.add(ItemLootEntry.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.setRandomSkill()))
    			.add(ItemLootEntry.lootTableItem(Items.AIR).setWeight(1))
    		.build());
    	}
    }
}
