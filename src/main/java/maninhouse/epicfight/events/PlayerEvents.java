package maninhouse.epicfight.events;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.ClientEngine;
import maninhouse.epicfight.entity.eventlistener.ItemUseEndEvent;
import maninhouse.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import maninhouse.epicfight.entity.eventlistener.RightClickItemEvent;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCGameruleChange;
import maninhouse.epicfight.network.server.STCGameruleChange.Gamerules;
import maninhouse.epicfight.world.ModGamerules;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class PlayerEvents {
	@SubscribeEvent
	public static void arrowLooseEvent(ArrowLooseEvent event) {
		//Colliders.update();
	}
	
	@SubscribeEvent
	public static void rightClickItemServer(RightClickItem event) {
		if (event.getSide() == LogicalSide.SERVER) {
			ServerPlayerData playerdata = (ServerPlayerData) event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerdata != null && (playerdata.getOriginalEntity().getHeldItemOffhand().getUseAction() == UseAction.NONE || playerdata.getHeldItemCapability(Hand.MAIN_HAND).isTwoHanded())) {
				playerdata.getEventListener().activateEvents(EventType.SERVER_ITEM_USE_EVENT, new RightClickItemEvent<>(playerdata));
			}
		}
	}
	
	@SubscribeEvent
	public static void itemUseStartEvent(LivingEntityUseItemEvent.Start event) {
		if (event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntity();
			PlayerData<?> playerdata = (PlayerData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			Hand hand = player.getHeldItem(Hand.MAIN_HAND).equals(event.getItem()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
			CapabilityItem itemCap = playerdata.getHeldItemCapability(hand);
			
			if (playerdata.getEntityState().isInaction()) {
				event.setCanceled(true);
			} else if (event.getItem() == player.getHeldItemOffhand() && playerdata.getHeldItemCapability(Hand.MAIN_HAND).isTwoHanded()) {
				event.setCanceled(true);
			}
			
			if (itemCap.getUseAction(playerdata) == UseAction.BLOCK) {
				event.setDuration(1000000);
			}
		}
	}
	
	@SubscribeEvent
	public static void cloneEvent(PlayerEvent.Clone event) {
		ServerPlayerData oldOne = (ServerPlayerData)event.getOriginal().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (oldOne != null && (!event.isWasDeath() || event.getOriginal().world.getGameRules().getBoolean(ModGamerules.KEEP_SKILLS))) {
			ServerPlayerData newOne = (ServerPlayerData)event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			newOne.initFromOldOne(oldOne);
		}
	}
	
	@SubscribeEvent
	public static void changeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		PlayerEntity player = event.getPlayer();
		ServerPlayerData playerdata = (ServerPlayerData)player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		playerdata.setLivingMotionCurrentItem(playerdata.getHeldItemCapability(Hand.MAIN_HAND));
	}
	
	@SubscribeEvent
	public static void itemUseStopEvent(LivingEntityUseItemEvent.Stop event) {
		if (event.getEntity().world.isRemote) {
			if (event.getEntity() instanceof ClientPlayerEntity) {
				ClientEngine.INSTANCE.renderEngine.zoomOut(0);
			}
		} else {
			if (event.getEntity() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
				ServerPlayerData playerdata = (ServerPlayerData) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerdata != null) {
					playerdata.getEventListener().activateEvents(EventType.SERVER_ITEM_STOP_EVENT, new ItemUseEndEvent(playerdata));
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void itemUseTickEvent(LivingEntityUseItemEvent.Tick event) {
		if (event.getEntity() instanceof PlayerEntity) {
			if (event.getItem().getItem() instanceof BowItem) {
				PlayerData<?> playerdata = (PlayerData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerdata.getEntityState().isInaction()) {
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void attackEntityEvent(AttackEntityEvent event) {
		boolean isLivingTarget = event.getTarget() instanceof LivingEntity ? ((LivingEntity)event.getTarget()).attackable() : false;
		if (!event.getEntity().world.getGameRules().getBoolean(ModGamerules.DO_VANILLA_ATTACK) && isLivingTarget) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void playerLogInEvent(PlayerLoggedInEvent event) {
		ModNetworkManager.sendToPlayer(new STCGameruleChange(Gamerules.HAS_FALL_ANIMATION,
					event.getEntity().world.getGameRules().getBoolean(ModGamerules.HAS_FALL_ANIMATION)), (ServerPlayerEntity)event.getPlayer());
		ModNetworkManager.sendToPlayer(new STCGameruleChange(Gamerules.SPEED_PENALTY_PERCENT,
				event.getEntity().world.getGameRules().getInt(ModGamerules.WEIGHT_PENALTY)), (ServerPlayerEntity)event.getPlayer());
	}
}