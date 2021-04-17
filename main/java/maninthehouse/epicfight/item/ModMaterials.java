package maninthehouse.epicfight.item;

import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ModMaterials {
	public static final ToolMaterial KATANA = EnumHelper.addToolMaterial("katana", 2, 550, 6.0F, 2.0F, 14);
	public static final ToolMaterial GREATSWORD = EnumHelper.addToolMaterial("greatSword", 4, 1625, 9.0F, 5.0F, 22);
	
	public static final ArmorMaterial STRAY_CLOTH = EnumHelper.addArmorMaterial(
			"stray_cloth", EpicFightMod.MODID + ":stray_cloth", 32, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
}