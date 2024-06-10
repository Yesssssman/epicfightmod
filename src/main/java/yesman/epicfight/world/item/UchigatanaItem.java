package yesman.epicfight.world.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import yesman.epicfight.main.EpicFightMod;

public class UchigatanaItem extends WeaponItem {
	public UchigatanaItem(Item.Properties build) {
		super(EpicFightItemTier.UCHIGATANA, 0, -2.0F, build);
	}
	
	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		return toRepair.getItem() == Items.IRON_BARS;
	}
    
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.literal(""));
		tooltip.add(Component.translatable("item." + EpicFightMod.MODID + ".uchigatana.tooltip"));
	}
}