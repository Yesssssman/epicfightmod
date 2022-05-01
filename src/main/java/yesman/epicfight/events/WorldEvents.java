package yesman.epicfight.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.data.loot.ModLootTables;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPDatapackSync;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class WorldEvents {
	@SubscribeEvent
	public static void onLootTableRegistry(final LootTableLoadEvent event) {
    	ModLootTables.modifyVanillaLootPools(event);
    }
	
	@SubscribeEvent
	public static void onDatapackSync(final OnDatapackSyncEvent event) {
		ServerPlayer serverplayer = (ServerPlayer)event.getPlayer();
		
		if (!serverplayer.getServer().isSingleplayerOwner(serverplayer.getGameProfile())) {
			SPDatapackSync armorPacket = new SPDatapackSync(ItemCapabilityReloadListener.armorCount(), SPDatapackSync.Type.ARMOR);
			SPDatapackSync weaponPacket = new SPDatapackSync(ItemCapabilityReloadListener.weaponCount(), SPDatapackSync.Type.WEAPON);
			SPDatapackSync mobPatchPacket = new SPDatapackSync(MobPatchReloadListener.getTagSize(), SPDatapackSync.Type.MOB);
			ItemCapabilityReloadListener.getArmorDataStream().forEach(armorPacket::write);
			ItemCapabilityReloadListener.getWeaponDataStream().forEach(weaponPacket::write);
			MobPatchReloadListener.getDataStream().forEach(mobPatchPacket::write);
			EpicFightNetworkManager.sendToPlayer(armorPacket, serverplayer);
			EpicFightNetworkManager.sendToPlayer(weaponPacket, serverplayer);
			EpicFightNetworkManager.sendToPlayer(mobPatchPacket, serverplayer);
		}
    }
}