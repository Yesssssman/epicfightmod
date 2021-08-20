package maninhouse.epicfight.item;

import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class EpicFightItemGroup {
	public static final ItemGroup ITEMS = new ItemGroup(EpicFightMod.MODID + ".items") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.SKILLBOOK.get());
        }
    };
}