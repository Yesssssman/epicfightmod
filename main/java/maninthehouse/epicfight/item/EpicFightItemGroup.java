package maninthehouse.epicfight.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EpicFightItemGroup {
	public static final CreativeTabs ITEMS = new CreativeTabs(12, "items") {
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(ModItems.SKILLBOOK);
		}
	};
}