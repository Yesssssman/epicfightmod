package yesman.epicfight.compat.curios;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RelicsModelProvider {
	/**
	public static HumanoidModel<?> getCuriosModel(ItemStack itemstack) {
		if (itemstack.getItem() instanceof IRenderableCurio renderableCurio) {
			return renderableCurio.getModel(itemstack);
		}
		
		return null;
	}
	**/
}