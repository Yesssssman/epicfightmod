package maninthehouse.epicfight.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.event.RegistryEvent;

public class ModItems {
	public static final Item KATANA = new KatanaItem().setUnlocalizedName("katana").setRegistryName("katana").setCreativeTab(EpicFightItemGroup.ITEMS);
	public static final Item KATANA_SHEATH = new KatanaSheathItem().setUnlocalizedName("katana_sheath").setRegistryName("katana_sheath");
	public static final Item GREATSWORD = new GreatswordItem().setUnlocalizedName("greatsword").setRegistryName("greatsword").setCreativeTab(EpicFightItemGroup.ITEMS);
	public static final Item STONE_SPEAR = new SpearItem(Item.ToolMaterial.STONE).setUnlocalizedName("stone_spear").setRegistryName("stone_spear").setCreativeTab(EpicFightItemGroup.ITEMS);
	public static final Item IRON_SPEAR = new SpearItem(Item.ToolMaterial.IRON).setUnlocalizedName("iron_spear").setRegistryName("iron_spear").setCreativeTab(EpicFightItemGroup.ITEMS);
	public static final Item GOLDEN_SPEAR = new SpearItem(Item.ToolMaterial.GOLD).setUnlocalizedName("golden_spear").setRegistryName("golden_spear").setCreativeTab(EpicFightItemGroup.ITEMS);
	public static final Item DIAMOND_SPEAR = new SpearItem(Item.ToolMaterial.DIAMOND).setUnlocalizedName("diamond_spear").setRegistryName("diamond_spear").setCreativeTab(EpicFightItemGroup.ITEMS);
	public static final Item STRAY_HAT = new ItemArmor(ModMaterials.STRAY_CLOTH, 0, EntityEquipmentSlot.HEAD)
			.setUnlocalizedName("stray_hat").setRegistryName("stray_hat").setCreativeTab(EpicFightItemGroup.ITEMS);
	public static final Item STRAY_ROBE = new ItemArmor(ModMaterials.STRAY_CLOTH, 0, EntityEquipmentSlot.CHEST)
			.setUnlocalizedName("stray_robe").setRegistryName("stray_robe").setCreativeTab(EpicFightItemGroup.ITEMS);
	public static final Item STRAY_PANTS = new ItemArmor(ModMaterials.STRAY_CLOTH, 0, EntityEquipmentSlot.LEGS)
			.setUnlocalizedName("stray_pants").setRegistryName("stray_pants").setCreativeTab(EpicFightItemGroup.ITEMS);
	public static final Item SKILLBOOK = new SkillBookItem().setCreativeTab(EpicFightItemGroup.ITEMS);
	
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(ModItems.KATANA);
		event.getRegistry().register(ModItems.KATANA_SHEATH);
		event.getRegistry().register(ModItems.GREATSWORD);
		event.getRegistry().register(ModItems.STONE_SPEAR);
		event.getRegistry().register(ModItems.IRON_SPEAR);
		event.getRegistry().register(ModItems.GOLDEN_SPEAR);
		event.getRegistry().register(ModItems.DIAMOND_SPEAR);
		event.getRegistry().register(ModItems.STRAY_HAT);
		event.getRegistry().register(ModItems.STRAY_ROBE);
		event.getRegistry().register(ModItems.STRAY_PANTS);
		event.getRegistry().register(ModItems.SKILLBOOK);
	}
}