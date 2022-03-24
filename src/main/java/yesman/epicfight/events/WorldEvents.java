package yesman.epicfight.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.data.loot.ModLootTables;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.network.server.SPChangeGamerule;
import yesman.epicfight.world.EpicFightGamerules;
import yesman.epicfight.world.capabilities.item.ItemCapabilityListener;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class WorldEvents {
	@SubscribeEvent
	public static void onLootTableRegistry(final LootTableLoadEvent event) {
    	ModLootTables.modifyVanillaLootPools(event);
    }
		
	@SubscribeEvent
	public static void onDatapackSync(final OnDatapackSyncEvent event) {
		ServerPlayer serverplayer = (ServerPlayer)event.getPlayer();
		EpicFightNetworkManager.sendToPlayer(new SPChangeGamerule(SPChangeGamerule.Gamerules.HAS_FALL_ANIMATION, serverplayer.level.getGameRules().getBoolean(EpicFightGamerules.HAS_FALL_ANIMATION)), serverplayer);
		EpicFightNetworkManager.sendToPlayer(new SPChangeGamerule(SPChangeGamerule.Gamerules.SPEED_PENALTY_PERCENT, serverplayer.level.getGameRules().getInt(EpicFightGamerules.WEIGHT_PENALTY)), serverplayer);
		if (!serverplayer.getServer().isSingleplayerOwner(serverplayer.getGameProfile())) {
			SPDatapackSync armorPacket = new SPDatapackSync(ItemCapabilityListener.armorCount(), SPDatapackSync.Type.ARMOR);
			SPDatapackSync weaponPacket = new SPDatapackSync(ItemCapabilityListener.weaponCount(), SPDatapackSync.Type.WEAPON);
			ItemCapabilityListener.getArmorDataStream().forEach(armorPacket::write);
			ItemCapabilityListener.getWeaponDataStream().forEach(weaponPacket::write);
			EpicFightNetworkManager.sendToPlayer(armorPacket, serverplayer);
			EpicFightNetworkManager.sendToPlayer(weaponPacket, serverplayer);
		}
    }
}