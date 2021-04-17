package maninthehouse.epicfight.client.renderer.layer;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.client.events.engine.RenderEngine;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HeldItemLayer<E extends EntityLivingBase, T extends LivingData<E>> extends Layer<E, T> {
	@Override
	public void renderLayer(T entitydata, E entityliving, VisibleMatrix4f[] poses, float partialTicks) {
		ItemStack mainHandStack = entitydata.getOriginalEntity().getHeldItemMainhand();
		RenderEngine renderEngine = ClientEngine.INSTANCE.renderEngine;
		GlStateManager.pushMatrix();
		if (mainHandStack.getItem() != Items.AIR) {
			if (entitydata.getOriginalEntity().getRidingEntity() != null) {
				CapabilityItem itemCap = entitydata.getHeldItemCapability(EnumHand.MAIN_HAND);
				if (itemCap != null && !itemCap.canUseOnMount()) {
					renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemBack(mainHandStack, entitydata);
					GlStateManager.popMatrix();
					return;
				}
			}
			renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemInHand(mainHandStack, entitydata, EnumHand.MAIN_HAND);
		}
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		ItemStack offHandStack = entitydata.getOriginalEntity().getHeldItemOffhand();
		if (offHandStack.getItem() != Items.AIR) {
			CapabilityItem cap = entitydata.getHeldItemCapability(EnumHand.MAIN_HAND);
			if (cap != null) {
				if (cap.canBeRenderedBoth(offHandStack)) {
					renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entitydata, EnumHand.OFF_HAND);
				}
			} else {
				renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entitydata, EnumHand.OFF_HAND);
			}
		}
		GlStateManager.popMatrix();
	}
}