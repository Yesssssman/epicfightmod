package yesman.epicfight.events;

import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.loot.ModLootTables;
import yesman.epicfight.main.EpicFightMod;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class WorldEvents {
	@SubscribeEvent
	public static void onLootTableRegistry(final LootTableLoadEvent event) {
    	ModLootTables.modifyVanillaLootPools(event);
    }
}