package yesman.epicfight.events;

import com.google.common.collect.Lists;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.data.loot.EpicFightLootTables;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeGamerule;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.network.server.SPDatapackSyncSkill;
import yesman.epicfight.server.commands.PlayerModeCommand;
import yesman.epicfight.server.commands.PlayerSkillCommand;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

@Mod.EventBusSubscriber(modid = EpicFightMod.MODID)
public class WorldEvents {
	@SubscribeEvent
	public static void onLootTableRegistry(final LootTableLoadEvent event) {
		EpicFightLootTables.modifyVanillaLootPools(event);
    }
	
	@SubscribeEvent
	public static void onCommandRegistry(final RegisterCommandsEvent event) {
		PlayerModeCommand.register(event.getDispatcher());
		PlayerSkillCommand.register(event.getDispatcher());
    }
	
	@SubscribeEvent
	public static void onDatapackSync(final OnDatapackSyncEvent event) {
		if (event.getPlayer() != null) {
			EpicFightNetworkManager.sendToPlayer(new SPChangeGamerule(SPChangeGamerule.SynchronizedGameRules.WEIGHT_PENALTY, event.getPlayer().level().getGameRules().getInt(EpicFightGamerules.WEIGHT_PENALTY)), event.getPlayer());
			EpicFightNetworkManager.sendToPlayer(new SPChangeGamerule(SPChangeGamerule.SynchronizedGameRules.DIABLE_ENTITY_UI, event.getPlayer().level().getGameRules().getBoolean(EpicFightGamerules.DISABLE_ENTITY_UI)), event.getPlayer());
			EpicFightNetworkManager.sendToPlayer(new SPChangeGamerule(SPChangeGamerule.SynchronizedGameRules.STIFF_COMBO_ATTACKS, event.getPlayer().level().getGameRules().getBoolean(EpicFightGamerules.STIFF_COMBO_ATTACKS)), event.getPlayer());
			EpicFightNetworkManager.sendToPlayer(new SPChangeGamerule(SPChangeGamerule.SynchronizedGameRules.CAN_SWITCH_COMBAT, event.getPlayer().level().getGameRules().getBoolean(EpicFightGamerules.CAN_SWITCH_COMBAT)), event.getPlayer());
			
			if (!event.getPlayer().getServer().isSingleplayerOwner(event.getPlayer().getGameProfile())) {
				synchronizeWorldData(event.getPlayer());
			} else {
				ServerPlayerPatch serverplayerpatch = EpicFightCapabilities.getEntityPatch(event.getPlayer(), ServerPlayerPatch.class);
				CapabilitySkill skillCapability = serverplayerpatch.getSkillCapability();
				
				for (SkillContainer skill : skillCapability.skillContainers) {
					if (skill.getSkill() != null) {
						// Reload skill
						skill.setSkill(SkillManager.getSkill(skill.getSkill().toString()), true);
					}
				}
			}
		} else {
			event.getPlayerList().getPlayers().forEach(WorldEvents::synchronizeWorldData);
		}
    }
	
	private static void synchronizeWorldData(ServerPlayer player) {
		SPDatapackSyncSkill skillParamsPacket = new SPDatapackSyncSkill(SkillManager.getParamCount(), SPDatapackSync.Type.SKILL_PARAMS);
		ServerPlayerPatch serverplayerpatch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
		CapabilitySkill skillCapability = serverplayerpatch.getSkillCapability();
		
		for (SkillContainer skill : skillCapability.skillContainers) {
			if (skill.getSkill() != null) {
				// Reload skill
				skill.setSkill(SkillManager.getSkill(skill.getSkill().toString()), true);
			}
		}
		
		for (SkillCategory category : SkillCategory.ENUM_MANAGER.universalValues()) {
			if (skillCapability.hasCategory(category)) {
				skillParamsPacket.addLearnedSkill(Lists.newArrayList(skillCapability.getLearnedSkills(category).stream().map((skill) -> skill.toString()).iterator()));
			}
		}
		
		SkillManager.getDataStream().forEach(skillParamsPacket::write);
		EpicFightNetworkManager.sendToPlayer(skillParamsPacket, player);
		
		SPDatapackSync armorPacket = new SPDatapackSync(ItemCapabilityReloadListener.armorCount(), SPDatapackSync.Type.ARMOR);
		SPDatapackSync weaponPacket = new SPDatapackSync(ItemCapabilityReloadListener.weaponCount(), SPDatapackSync.Type.WEAPON);
		SPDatapackSync mobPatchPacket = new SPDatapackSync(MobPatchReloadListener.getTagCount(), SPDatapackSync.Type.MOB);
		SPDatapackSync weaponTypePacket = new SPDatapackSync(WeaponTypeReloadListener.getTagCount(), SPDatapackSync.Type.WEAPON_TYPE);
		
		ItemCapabilityReloadListener.getArmorDataStream().forEach(armorPacket::write);
		ItemCapabilityReloadListener.getWeaponDataStream().forEach(weaponPacket::write);
		MobPatchReloadListener.getDataStream().forEach(mobPatchPacket::write);
		WeaponTypeReloadListener.getWeaponTypeDataStream().forEach(weaponTypePacket::write);
		
		EpicFightNetworkManager.sendToPlayer(weaponTypePacket, player);
		EpicFightNetworkManager.sendToPlayer(armorPacket, player);
		EpicFightNetworkManager.sendToPlayer(weaponPacket, player);
		EpicFightNetworkManager.sendToPlayer(mobPatchPacket, player);
	}
}