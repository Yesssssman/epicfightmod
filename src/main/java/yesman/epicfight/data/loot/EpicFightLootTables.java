package yesman.epicfight.data.loot;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import yesman.epicfight.data.loot.function.SetRandomSkillFunction;
import yesman.epicfight.world.item.EpicFightItems;

public class EpicFightLootTables {
	public static void modifyVanillaLootPools(final LootTableLoadEvent event) {
    	if (event.getName().equals(BuiltInLootTables.DESERT_PYRAMID)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 2.0F))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.builder()))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(1))
    		.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(EpicFightItems.KATANA.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(3))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 4.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.builder()))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(1))
        	.build());
    		
    		event.getTable().addPool(LootPool.lootPool()
    			.add(LootItem.lootTableItem(EpicFightItems.KATANA.get()).setWeight(1))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(3))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(3).apply(SetRandomSkillFunction.builder()))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.ABANDONED_MINESHAFT)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(3).apply(SetRandomSkillFunction.builder()))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.PILLAGER_OUTPOST)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(3).apply(SetRandomSkillFunction.builder()))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_BIG)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(3).apply(SetRandomSkillFunction.builder()))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(7))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.SHIPWRECK_MAP)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 2.0F))
        		.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.builder()))
        		.add(LootItem.lootTableItem(Items.AIR).setWeight(2))
        	.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.STRONGHOLD_LIBRARY)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 5.0F))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.builder()))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(1))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.WOODLAND_MANSION)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 5.0F))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.builder()))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(1))
    		.build());
    	}
    	
    	if (event.getName().equals(BuiltInLootTables.BASTION_OTHER)) {
    		event.getTable().addPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 4.0F))
    			.add(LootItem.lootTableItem(EpicFightItems.SKILLBOOK.get()).setWeight(1).apply(SetRandomSkillFunction.builder()))
    			.add(LootItem.lootTableItem(Items.AIR).setWeight(1))
    		.build());
    	}
    }
}
