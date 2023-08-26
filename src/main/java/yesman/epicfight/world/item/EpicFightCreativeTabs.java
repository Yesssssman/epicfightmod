package yesman.epicfight.world.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightCreativeTabs {
	public static final CreativeModeTab ITEMS = new CreativeModeTab(EpicFightMod.MODID + ".items") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(EpicFightItems.SKILLBOOK.get());
        }
    };
}