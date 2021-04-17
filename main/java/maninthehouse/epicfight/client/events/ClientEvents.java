package maninthehouse.epicfight.client.events;

import org.lwjgl.input.Mouse;

import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.client.model.DynamicPerspectiveModel;
import maninthehouse.epicfight.item.ModItems;
import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Side.CLIENT)
public class ClientEvents {
	private static final String OFFHAND_TEXTURE_NAME = "minecraft:item/empty_armor_slot_shield";
	
	@SubscribeEvent
	public static void mouseClickEvent(MouseInputEvent.Pre event) {
		if (event.getGui() instanceof GuiContainer) {
			Slot slotUnderMouse = ((GuiContainer) event.getGui()).getSlotUnderMouse();
			
			if (slotUnderMouse != null) {
				CapabilityItem cap = Minecraft.getMinecraft().player.inventory.getItemStack().getCapability(ModCapabilities.CAPABILITY_ITEM, null);
				
				if (cap != null && !cap.canUsedInOffhand()) {
					if (slotUnderMouse.getBackgroundSprite() == Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(OFFHAND_TEXTURE_NAME)) {
						Mouse.setGrabbed(true);
						event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onItemModelRegister(ModelRegistryEvent event) {
		OBJLoader.INSTANCE.addDomain(EpicFightMod.MODID);
		ModelLoader.setCustomModelResourceLocation(ModItems.KATANA_SHEATH, 0, new ModelResourceLocation(ModItems.KATANA_SHEATH.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.STRAY_ROBE, 0, new ModelResourceLocation(ModItems.STRAY_ROBE.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.STRAY_HAT, 0, new ModelResourceLocation(ModItems.STRAY_HAT.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.STRAY_PANTS, 0, new ModelResourceLocation(ModItems.STRAY_PANTS.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ModItems.SKILLBOOK, 0, new ModelResourceLocation(ModItems.SKILLBOOK.getRegistryName(), "inventory"));
		
		registerModelVariant(ModItems.STONE_SPEAR, "epicfight:spear", "texture0");
		registerModelVariant(ModItems.GOLDEN_SPEAR, "epicfight:spear", "texture1");
		registerModelVariant(ModItems.IRON_SPEAR, "epicfight:spear", "texture2");
		registerModelVariant(ModItems.DIAMOND_SPEAR, "epicfight:spear", "texture3");
		registerModelVariant(ModItems.KATANA);
		registerModelVariant(ModItems.GREATSWORD);
	}
	
	@SubscribeEvent
	public static void onBakeModel(ModelBakeEvent event) {
		IBakedModel greatswordModel = event.getModelRegistry().getObject(new ModelResourceLocation(ModItems.GREATSWORD.getRegistryName()+"3d", "inventory"));
		IBakedModel katanaModel = event.getModelRegistry().getObject(new ModelResourceLocation(ModItems.KATANA.getRegistryName()+"3d", "inventory"));
		IBakedModel spearModel1 = event.getModelRegistry().getObject(new ModelResourceLocation("epicfight:spear3d", "texture0"));
		IBakedModel spearModel2 = event.getModelRegistry().getObject(new ModelResourceLocation("epicfight:spear3d", "texture1"));
		IBakedModel spearModel3 = event.getModelRegistry().getObject(new ModelResourceLocation("epicfight:spear3d", "texture2"));
		IBakedModel spearModel4 = event.getModelRegistry().getObject(new ModelResourceLocation("epicfight:spear3d", "texture3"));
		
		DynamicPerspectiveModel greatsword = new DynamicPerspectiveModel(event.getModelRegistry().getObject(
				new ModelResourceLocation(ModItems.GREATSWORD.getRegistryName()+"2d", "inventory")), greatswordModel);
		DynamicPerspectiveModel katana = new DynamicPerspectiveModel(event.getModelRegistry().getObject(
				new ModelResourceLocation(ModItems.KATANA.getRegistryName()+"2d", "inventory")), katanaModel);
		DynamicPerspectiveModel spear1 = new DynamicPerspectiveModel(event.getModelRegistry().getObject(
				new ModelResourceLocation("epicfight:spear2d", "texture0")), spearModel1);
		DynamicPerspectiveModel spear2 = new DynamicPerspectiveModel(event.getModelRegistry().getObject(
				new ModelResourceLocation("epicfight:spear2d", "texture1")), spearModel2);
		DynamicPerspectiveModel spear3 = new DynamicPerspectiveModel(event.getModelRegistry().getObject(
				new ModelResourceLocation("epicfight:spear2d", "texture2")), spearModel3);
		DynamicPerspectiveModel spear4 = new DynamicPerspectiveModel(event.getModelRegistry().getObject(
				new ModelResourceLocation("epicfight:spear2d", "texture3")), spearModel4);
		
		event.getModelRegistry().putObject(new ModelResourceLocation(ModItems.GREATSWORD.getRegistryName()+"3d", "inventory"), greatsword);
		event.getModelRegistry().putObject(new ModelResourceLocation(ModItems.KATANA.getRegistryName()+"3d", "inventory"), katana);
		event.getModelRegistry().putObject(new ModelResourceLocation("epicfight:spear3d", "texture0"), spear1);
		event.getModelRegistry().putObject(new ModelResourceLocation("epicfight:spear3d", "texture1"), spear2);
		event.getModelRegistry().putObject(new ModelResourceLocation("epicfight:spear3d", "texture2"), spear3);
		event.getModelRegistry().putObject(new ModelResourceLocation("epicfight:spear3d", "texture3"), spear4);
	}
	
	private static void registerModelVariant(Item item) {
		registerModelVariant(item, item.getRegistryName().toString(), "inventory");
	}
	
	private static void registerModelVariant(Item item, String name, String variant) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(name+"2d", variant));
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(name+"3d", variant));
	}
}