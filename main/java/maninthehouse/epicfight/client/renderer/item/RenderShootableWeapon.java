package maninthehouse.epicfight.client.renderer.item;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderShootableWeapon extends RenderItemBase {
	@Override
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, EnumHand hand) {
		super.renderItemInHand(stack, itemHolder, EnumHand.OFF_HAND);
	}
}