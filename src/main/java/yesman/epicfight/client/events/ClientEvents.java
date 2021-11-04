package yesman.epicfight.client.events;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.UseAction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseReleasedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.item.ItemCapabilityListener;
import yesman.epicfight.capabilities.provider.ProviderItem;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.entity.eventlistener.RightClickItemEvent;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT)
public class ClientEvents {
	private static final Pair<ResourceLocation, ResourceLocation> OFFHAND_TEXTURE = Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
	
	@SubscribeEvent
	public static void mouseClickEvent(MouseClickedEvent.Pre event) {
		if(event.getGui() instanceof ContainerScreen) {
			Slot slotUnderMouse = ((ContainerScreen<?>)event.getGui()).getSlotUnderMouse();
			if (slotUnderMouse != null) {
				CapabilityItem cap = ModCapabilities.getItemStackCapability(Minecraft.getInstance().player.inventory.getItemStack());
				if (!cap.canUsedInOffhand()) {
					if (slotUnderMouse.getBackground() != null && slotUnderMouse.getBackground().equals(OFFHAND_TEXTURE)) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void mouseReleaseEvent(MouseReleasedEvent.Pre event) {
		if (event.getGui() instanceof ContainerScreen) {
			Slot slotUnderMouse = ((ContainerScreen<?>)event.getGui()).getSlotUnderMouse();
			if (slotUnderMouse != null) {
				CapabilityItem cap = ModCapabilities.getItemStackCapability(Minecraft.getInstance().player.inventory.getItemStack());
				if (!cap.canUsedInOffhand()) {
					if (slotUnderMouse.getBackground() != null && slotUnderMouse.getBackground().equals(OFFHAND_TEXTURE)) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void presssKeyInGui(KeyboardKeyPressedEvent.Pre event) {
		CapabilityItem itemCapability = CapabilityItem.EMPTY;
		if (event.getKeyCode() == Minecraft.getInstance().gameSettings.keyBindSwapHands.getKey().getKeyCode()) {
			if (event.getGui() instanceof ContainerScreen) {
				Slot slotUnderMouse = ((ContainerScreen<?>)event.getGui()).getSlotUnderMouse();
				if (slotUnderMouse != null && slotUnderMouse.getHasStack()) {
					itemCapability = ModCapabilities.getItemStackCapability(slotUnderMouse.getStack());
					if (!itemCapability.canUsedInOffhand()) {
						event.setCanceled(true);
					}
				}
			}
		} else if (event.getKeyCode() >= 49 && event.getKeyCode() <= 57) {
			if (event.getGui() instanceof ContainerScreen) {
				Slot slotUnderMouse = ((ContainerScreen<?>)event.getGui()).getSlotUnderMouse();
				if (slotUnderMouse.getBackground() != null && slotUnderMouse.getBackground().equals(OFFHAND_TEXTURE)) {
					itemCapability = ModCapabilities.getItemStackCapability(Minecraft.getInstance().player.inventory.getStackInSlot(event.getKeyCode() - 49));
					if (!itemCapability.canUsedInOffhand()) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void rightClickItemClient(RightClickItem event) {
		if (event.getSide() == LogicalSide.CLIENT) {
			ClientPlayerData playerdata = (ClientPlayerData) event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerdata != null && playerdata.getOriginalEntity().getHeldItemOffhand().getUseAction() == UseAction.NONE) {
				playerdata.getEventListener().activateEvents(EventType.CLIENT_ITEM_USE_EVENT, new RightClickItemEvent<>(playerdata));
			}
		}
	}
	
	@SubscribeEvent
	public static void clientRespawnEvent(ClientPlayerNetworkEvent.RespawnEvent event) {
		ClientPlayerData oldOne = (ClientPlayerData)event.getOldPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (oldOne != null) { // This value is null when player dies.
			ClientPlayerData newOne = (ClientPlayerData)event.getNewPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			newOne.onEntityJoinWorld(event.getNewPlayer());
			newOne.initFromOldOne(oldOne);
		}
	}
	
	@SubscribeEvent
	public static void clientLogoutEvent(ClientPlayerNetworkEvent.LoggedOutEvent event) {
		if (event.getPlayer() != null) {
			ItemCapabilityListener.reset();
			ProviderItem.clear();
		}
	}
}