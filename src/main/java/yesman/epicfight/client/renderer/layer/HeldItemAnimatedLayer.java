package yesman.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public class HeldItemAnimatedLayer<E extends LivingEntity, T extends LivingData<E>, M extends EntityModel<E>> extends AnimatedLayer<E, T, M, LayerRenderer<E, M>> {
	@Override
	public void renderLayer(T entitydata, E entityliving, LayerRenderer<E, M> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		ItemStack mainHandStack = entitydata.getOriginalEntity().getHeldItemMainhand();
		RenderEngine renderEngine = ClientEngine.instance.renderEngine;
		matrixStackIn.push();
		if (mainHandStack.getItem() != Items.AIR) {
			if (entitydata.getOriginalEntity().getRidingEntity() != null) {
				if (!entitydata.getHeldItemCapability(Hand.MAIN_HAND).canUseOnMount()) {
					renderEngine.getItemRenderer(mainHandStack.getItem()).renderUnusableItemMount(mainHandStack, entitydata, buffer, matrixStackIn, packedLightIn);
					matrixStackIn.pop();
					return;
				}
			}
			renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemInHand(mainHandStack, entitydata, Hand.MAIN_HAND, buffer, matrixStackIn, packedLightIn);
		}
		matrixStackIn.pop();
		matrixStackIn.push();
		ItemStack offHandStack = entitydata.getOriginalEntity().getHeldItemOffhand();
		if (entitydata.isValidOffhandItem()) {
			renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entitydata, Hand.OFF_HAND, buffer, matrixStackIn, packedLightIn);
		}
		matrixStackIn.pop();
	}
}