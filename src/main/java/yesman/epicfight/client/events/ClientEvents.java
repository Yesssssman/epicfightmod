package yesman.epicfight.client.events;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.ScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.ScreenEvent.MouseReleasedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.provider.EntityPatchProvider;
import yesman.epicfight.world.capabilities.provider.ItemCapabilityProvider;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.RightClickItemEvent;
import yesman.epicfight.world.level.block.FractureBlockState;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT)
public class ClientEvents {
	private static final Pair<ResourceLocation, ResourceLocation> OFFHAND_TEXTURE = Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
	private static Minecraft minecraft = Minecraft.getInstance();
	
	@SubscribeEvent
	public static void mouseClickEvent(MouseClickedEvent.Pre event) {
		if (event.getScreen() instanceof AbstractContainerScreen) {
			Slot slot = ((AbstractContainerScreen<?>)event.getScreen()).getSlotUnderMouse();
			
			if (slot != null) {
				CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(minecraft.player.containerMenu.getCarried());
				
				if (!cap.canBePlacedOffhand()) {
					if (slot.getNoItemIcon() != null && slot.getNoItemIcon().equals(OFFHAND_TEXTURE)) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void mouseReleaseEvent(MouseReleasedEvent.Pre event) {
		if (event.getScreen() instanceof AbstractContainerScreen) {
			Slot slot = ((AbstractContainerScreen<?>)event.getScreen()).getSlotUnderMouse();
			
			if (slot != null) {
				CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(minecraft.player.containerMenu.getCarried());
				
				if (!cap.canBePlacedOffhand()) {
					if (slot.getNoItemIcon() != null && slot.getNoItemIcon().equals(OFFHAND_TEXTURE)) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void presssKeyInGui(KeyboardKeyPressedEvent.Pre event) {
		CapabilityItem itemCapability = CapabilityItem.EMPTY;
		
		if (event.getKeyCode() == minecraft.options.keySwapOffhand.getKey().getValue()) {
			if (event.getScreen() instanceof AbstractContainerScreen) {
				Slot slot = ((AbstractContainerScreen<?>)event.getScreen()).getSlotUnderMouse();
				
				if (slot != null && slot.hasItem()) {
					itemCapability = EpicFightCapabilities.getItemStackCapability(slot.getItem());
					
					if (!itemCapability.canBePlacedOffhand()) {
						event.setCanceled(true);
					}
				}
			}
		} else if (event.getKeyCode() >= 49 && event.getKeyCode() <= 57) {
			if (event.getScreen() instanceof AbstractContainerScreen) {
				Slot slot = ((AbstractContainerScreen<?>)event.getScreen()).getSlotUnderMouse();
				
				if (slot != null && slot.getNoItemIcon() != null && slot.getNoItemIcon().equals(OFFHAND_TEXTURE)) {
					itemCapability = EpicFightCapabilities.getItemStackCapability(minecraft.player.getInventory().getItem(event.getKeyCode() - 49));
					
					if (!itemCapability.canBePlacedOffhand()) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void rightClickItemClient(RightClickItem event) {
		if (event.getSide() == LogicalSide.CLIENT) {
			LocalPlayerPatch playerpatch = EpicFightCapabilities.getEntityPatch(event.getPlayer(), LocalPlayerPatch.class);
			
			if (playerpatch != null && playerpatch.getOriginal().getOffhandItem().getUseAnimation() == UseAnim.NONE) {
				boolean canceled = playerpatch.getEventListener().triggerEvents(EventType.CLIENT_ITEM_USE_EVENT, new RightClickItemEvent<>(playerpatch));
				
				if (playerpatch.getEntityState().movementLocked()) {
					canceled = true;
				}
				
				event.setCanceled(canceled);
			}
		}
	}
	
	@SubscribeEvent
	public static void clientRespawnEvent(ClientPlayerNetworkEvent.RespawnEvent event) {
		LocalPlayerPatch oldCap = EpicFightCapabilities.getEntityPatch(event.getOldPlayer(), LocalPlayerPatch.class);
		
		if (oldCap != null) {
			LocalPlayerPatch newCap = EpicFightCapabilities.getEntityPatch(event.getNewPlayer(), LocalPlayerPatch.class);
			newCap.onRespawnLocalPlayer(event);
			newCap.copySkillsFrom(oldCap);
			newCap.toMode(oldCap.getPlayerMode(), false);
		}
		
		ClientEngine.getInstance().renderEngine.battleModeUI.reset();
	}
	
	@SubscribeEvent
	public static void clientLogoutEvent(ClientPlayerNetworkEvent.LoggedOutEvent event) {
		if (event.getPlayer() != null) {
			ItemCapabilityReloadListener.reset();
			ItemCapabilityProvider.clear();
			EntityPatchProvider.clear();
			ClientEngine.getInstance().renderEngine.battleModeUI.reset();
			ClientEngine.getInstance().renderEngine.clearCustomEntityRenerer();
		}
		
		FractureBlockState.reset();
	}
}