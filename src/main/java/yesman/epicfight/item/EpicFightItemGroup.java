package yesman.epicfight.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightItemGroup {
	public static final ItemGroup ITEMS = new ItemGroup(EpicFightMod.MODID + ".items") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(EpicFightItems.SKILLBOOK.get());
        }
    };
}