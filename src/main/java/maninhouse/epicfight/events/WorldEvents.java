package maninhouse.epicfight.events;

import maninhouse.epicfight.loot.ModLootTables;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class WorldEvents {
	@SubscribeEvent
	public static void onLootTableRegistry(final LootTableLoadEvent event) {
    	ModLootTables.modifyVanillaLootPools(event);
    }
}