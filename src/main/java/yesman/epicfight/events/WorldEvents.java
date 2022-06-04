package yesman.epicfight.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.data.loot.ModLootTables;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeGamerule;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.server.commands.PlayerModeCommand;
import yesman.epicfight.server.commands.PlayerSkillCommand;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

@Mod.EventBusSubscriber(modid = EpicFightMod.MODID)
public class WorldEvents {
	@SubscribeEvent
	public static void onLootTableRegistry(final LootTableLoadEvent event) {
		if (ConfigManager.SKILLBOOK_CHEST_LOOT.get()) {
			ModLootTables.modifyVanillaLootPools(event);
		}
    }
	
	@SubscribeEvent
	public static void onCommandRegistry(final RegisterCommandsEvent event) {
		PlayerModeCommand.register(event.getDispatcher());
		PlayerSkillCommand.register(event.getDispatcher());
    }
	
	@SubscribeEvent
	public static void onDatapackSync(final OnDatapackSyncEvent event) {
		ServerPlayer player = event.getPlayer();
		PacketDistributor.PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);
		
		if (player != null) {
			EpicFightNetworkManager.sendToClient(new SPChangeGamerule(SPChangeGamerule.SynchronizedGameRules.WEIGHT_PENALTY, player.level.getGameRules().getInt(EpicFightGamerules.WEIGHT_PENALTY)), target);
			EpicFightNetworkManager.sendToClient(new SPChangeGamerule(SPChangeGamerule.SynchronizedGameRules.DIABLE_ENTITY_UI, player.level.getGameRules().getBoolean(EpicFightGamerules.DISABLE_ENTITY_UI)), target);
		}
		
		if (player == null || !player.getServer().isSingleplayerOwner(player.getGameProfile())) {
			SPDatapackSync armorPacket = new SPDatapackSync(ItemCapabilityReloadListener.armorCount(), SPDatapackSync.Type.ARMOR);
			SPDatapackSync weaponPacket = new SPDatapackSync(ItemCapabilityReloadListener.weaponCount(), SPDatapackSync.Type.WEAPON);
			SPDatapackSync mobPatchPacket = new SPDatapackSync(MobPatchReloadListener.getTagSize(), SPDatapackSync.Type.MOB);
			ItemCapabilityReloadListener.getArmorDataStream().forEach(armorPacket::write);
			ItemCapabilityReloadListener.getWeaponDataStream().forEach(weaponPacket::write);
			MobPatchReloadListener.getDataStream().forEach(mobPatchPacket::write);
			EpicFightNetworkManager.sendToClient(armorPacket, target);
			EpicFightNetworkManager.sendToClient(weaponPacket, target);
			EpicFightNetworkManager.sendToClient(mobPatchPacket, target);
		}
    }
}