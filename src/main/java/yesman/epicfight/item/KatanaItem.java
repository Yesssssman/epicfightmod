package yesman.epicfight.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;

public class KatanaItem extends WeaponItem {
	@OnlyIn(Dist.CLIENT)
	private List<ITextComponent> tooltipExpand;

	public KatanaItem(Item.Properties build) {
		super(ModItemTier.KATANA, 0, -2.0F, build);
		if (EpicFightMod.isPhysicalClient()) {
			this.tooltipExpand = new ArrayList<ITextComponent> ();
			this.tooltipExpand.add(new StringTextComponent(""));
			this.tooltipExpand.add(new TranslationTextComponent("item." + EpicFightMod.MODID + ".katana.tooltip"));
		}
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return toRepair.getItem() == Items.IRON_BARS;
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		for (ITextComponent txtComp : tooltipExpand) {
			tooltip.add(txtComp);
		}
	}
}