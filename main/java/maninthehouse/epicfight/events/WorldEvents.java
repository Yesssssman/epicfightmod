package maninthehouse.epicfight.events;

import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCGameruleChange;
import maninthehouse.epicfight.utils.game.Formulars;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class WorldEvents {
	@SubscribeEvent
	public static void loadWorldEvent(WorldEvent.Load event) {
		GameRules gameRule = event.getWorld().getWorldInfo().getGameRulesInstance();
		
		if (!gameRule.hasRule("doVanillaAttack")) {
			gameRule.addGameRule("doVanillaAttack", "true", GameRules.ValueType.BOOLEAN_VALUE);
		}
		
		if (!gameRule.hasRule("hasFallAnimation")) {
			gameRule.addGameRule("hasFallAnimation", "true", GameRules.ValueType.BOOLEAN_VALUE);
		}
		
		if (!gameRule.hasRule("speedPenaltyPercent")) {
			gameRule.addGameRule("speedPenaltyPercent", "100", GameRules.ValueType.NUMERICAL_VALUE);
		}
	}
	
	@SubscribeEvent
	public static void gameruleChangeEvent(GameRuleChangeEvent event) {
		if(event.getRuleName().equals("hasFallAnimation")) {
			ModNetworkManager.sendToAll(new STCGameruleChange(event.getRuleName(), event.getRules().getString(event.getRuleName())));
		} else if(event.getRuleName().equals("speedPenaltyPercent")) {
			for (EntityPlayerMP player : event.getServer().getPlayerList().getPlayers()) {
				ServerPlayerData playerdata = (ServerPlayerData)player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
				if (playerdata != null) {
					IAttributeInstance mainhandAttackSpeed = playerdata.getOriginalEntity().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
					IAttributeInstance offhandAttackSpeed = playerdata.getOriginalEntity().getAttributeMap().getAttributeInstance(ModAttributes.OFFHAND_ATTACK_SPEED);
					
					mainhandAttackSpeed.removeModifier(ServerPlayerData.WEIGHT_PENALTY_MODIFIIER);
					float mainWeaponSpeed = (float) mainhandAttackSpeed.getBaseValue();
					for(AttributeModifier attributeModifier : playerdata.getOriginalEntity().getHeldItemMainhand()
							.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
						mainWeaponSpeed += (float)attributeModifier.getAmount();
					}
					
					mainhandAttackSpeed.applyModifier(new AttributeModifier(ServerPlayerData.WEIGHT_PENALTY_MODIFIIER, "weight penalty modifier",
							Formulars.getAttackSpeedPenalty(playerdata.getWeight(), mainWeaponSpeed, playerdata), 0));
					
					offhandAttackSpeed.removeModifier(ServerPlayerData.WEIGHT_PENALTY_MODIFIIER);
					float offWeaponSpeed = (float) offhandAttackSpeed.getBaseValue();
					for(AttributeModifier attributeModifier : playerdata.getOriginalEntity().getHeldItemOffhand()
							.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
						offWeaponSpeed += (float)attributeModifier.getAmount();
					}
					
					offhandAttackSpeed.applyModifier(new AttributeModifier(ServerPlayerData.WEIGHT_PENALTY_MODIFIIER, "weight penalty modifier",
							Formulars.getAttackSpeedPenalty(playerdata.getWeight(), offWeaponSpeed, playerdata), 0));
				}
			}
			
			ModNetworkManager.sendToAll(new STCGameruleChange(event.getRuleName(), event.getRules().getString(event.getRuleName())));
		}
	}
	
	@SubscribeEvent
	public static void playerLogInEvent(PlayerLoggedInEvent event) {
		GameRules gamerules = event.player.world.getGameRules();
		ModNetworkManager.sendToPlayer(new STCGameruleChange("hasFallAnimation", gamerules.getString("hasFallAnimation")), (EntityPlayerMP)event.player);
		ModNetworkManager.sendToPlayer(new STCGameruleChange("speedPenaltyPercent", gamerules.getString("speedPenaltyPercent")), (EntityPlayerMP)event.player);
	}
}
