package yesman.epicfight.compat.curios;

import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RelicsModelProvider {
	public static HumanoidModel<?> getCuriosModel(ItemStack itemstack) {
		if (itemstack.getItem() instanceof IRenderableCurio renderableCurio) {
			return renderableCurio.getModel(itemstack);
		}
		
		return null;
	}
}