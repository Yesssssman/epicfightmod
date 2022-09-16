package yesman.epicfight.events;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeGamerule;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.ItemUseEndEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.RightClickItemEvent;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

@Mod.EventBusSubscriber(modid = EpicFightMod.MODID)
public class PlayerEvents {
	/**@SubscribeEvent
	public static void arrowLooseEvent(ArrowLooseEvent event) {
		ColliderPreset.update();
	}**/
	
	@SubscribeEvent
	public static void startTrackingEvent(StartTracking event) {
		Entity trackingTarget = event.getTarget();
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)trackingTarget.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		
		if (entitypatch != null) {
			entitypatch.onStartTracking((ServerPlayerEntity)event.getPlayer());
		}
	}
	
	@SubscribeEvent
	public static void rightClickItemServerEvent(RightClickItem event) {
		if (event.getSide() == LogicalSide.SERVER) {
			ServerPlayerPatch playerpatch = (ServerPlayerPatch) event.getPlayer().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
			
			if (playerpatch != null && (playerpatch.getOriginal().getOffhandItem().getUseAnimation() == UseAction.NONE || !playerpatch.getHoldingItemCapability(Hand.MAIN_HAND).getStyle(playerpatch).canUseOffhand())) {
				boolean canceled = playerpatch.getEventListener().triggerEvents(EventType.SERVER_ITEM_USE_EVENT, new RightClickItemEvent<>(playerpatch));
				
				if (playerpatch.getEntityState().movementLocked()) {
					canceled = true;
				}
				
				event.setCanceled(canceled);
			}
		}
	}
	
	@SubscribeEvent
	public static void itemUseStartEvent(LivingEntityUseItemEvent.Start event) {
		if (event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntity();
			PlayerPatch<?> playerpatch = (PlayerPatch<?>) event.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
			Hand hand = player.getItemInHand(Hand.MAIN_HAND).equals(event.getItem()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
			CapabilityItem itemCap = playerpatch.getHoldingItemCapability(hand);
			
			if (!playerpatch.getEntityState().canUseSkill()) {
				event.setCanceled(true);
			} else if (event.getItem() == player.getOffhandItem() && !playerpatch.getHoldingItemCapability(Hand.MAIN_HAND).getStyle(playerpatch).canUseOffhand()) {
				event.setCanceled(true);
			}
			
			if (itemCap.getUseAnimation(playerpatch) == UseAction.BLOCK) {
				event.setDuration(Integer.MAX_VALUE);
			}
		}
	}
	
	@SubscribeEvent
	public static void cloneEvent(PlayerEvent.Clone event) {
		//event.getOriginal().reviveCaps();
		ServerPlayerPatch oldCap = (ServerPlayerPatch)event.getOriginal().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		
		if (oldCap != null) {
			ServerPlayerPatch newCap = (ServerPlayerPatch)event.getPlayer().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
			
			if ((!event.isWasDeath() || event.getOriginal().level.getGameRules().getBoolean(EpicFightGamerules.KEEP_SKILLS))) {
				newCap.copySkillsFrom(oldCap);
			}
			
			newCap.toMode(oldCap.getPlayerMode(), false);
		}
		
		//event.getOriginal().invalidateCaps();
	}
	
	@SubscribeEvent
	public static void changeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		PlayerEntity player = event.getPlayer();
		ServerPlayerPatch playerpatch = (ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		playerpatch.modifyLivingMotionByCurrentItem();
		
		EpicFightNetworkManager.sendToPlayer(new SPChangeGamerule(SPChangeGamerule.SynchronizedGameRules.WEIGHT_PENALTY, player.level.getGameRules().getInt(EpicFightGamerules.WEIGHT_PENALTY)), (ServerPlayerEntity)player);
		EpicFightNetworkManager.sendToPlayer(new SPChangeGamerule(SPChangeGamerule.SynchronizedGameRules.DIABLE_ENTITY_UI, player.level.getGameRules().getBoolean(EpicFightGamerules.DISABLE_ENTITY_UI)), (ServerPlayerEntity)player);
	}
	
	@SubscribeEvent
	public static void itemUseStopEvent(LivingEntityUseItemEvent.Stop event) {
		if (event.getEntity().level.isClientSide()) {
			if (event.getEntity() instanceof ClientPlayerEntity) {
				ClientEngine.instance.renderEngine.zoomOut(0);
			}
		} else {
			if (event.getEntity() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
				ServerPlayerPatch playerpatch = (ServerPlayerPatch) player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerpatch != null) {
					boolean canceled = playerpatch.getEventListener().triggerEvents(EventType.SERVER_ITEM_STOP_EVENT, new ItemUseEndEvent(playerpatch));
					event.setCanceled(canceled);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void itemUseTickEvent(LivingEntityUseItemEvent.Tick event) {
		if (event.getEntity() instanceof PlayerEntity) {
			if (event.getItem().getItem() instanceof BowItem) {
				PlayerPatch<?> playerpatch = (PlayerPatch<?>) event.getEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerpatch.getEntityState().inaction()) {
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void attackEntityEvent(AttackEntityEvent event) {
		boolean isLivingTarget = event.getTarget() instanceof LivingEntity ? ((LivingEntity)event.getTarget()).attackable() : false;
		if (!event.getEntity().level.getGameRules().getBoolean(EpicFightGamerules.DO_VANILLA_ATTACK) && isLivingTarget) {
			event.setCanceled(true);
		}
	}
}