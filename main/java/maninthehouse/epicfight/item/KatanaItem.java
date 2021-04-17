package maninthehouse.epicfight.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import maninthehouse.epicfight.capabilities.item.KatanaCapability;
import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class KatanaItem extends WeaponItem {
	@SideOnly(Side.CLIENT)
	private List<String> tooltipExpand;
	
	public KatanaItem() {
		super(ModMaterials.KATANA, 5, -2.0F);
		if (EpicFightMod.isPhysicalClient()) {
			tooltipExpand = new ArrayList<String> ();
			tooltipExpand.add("");
			tooltipExpand.add("If you don't act for 5 second, it will go to the sheathing state. In sheathing state, you can reinforce the next attack");
		}
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return toRepair.getItem() == Items.IRON_INGOT;
    }
	
	@Override
	public boolean canHarvestBlock(IBlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability() {
    	capability = new KatanaCapability();
    }
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		for (String str : tooltipExpand) {
			tooltip.add(str);
		}
    }
}