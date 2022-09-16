package yesman.epicfight.world.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightItemGroup {
	public static final ItemGroup ITEMS = new ItemGroup(EpicFightMod.MODID + ".items") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(EpicFightItems.SKILLBOOK.get());
        }
    };
}