package yesman.epicfight.client.renderer.patched.layer;

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
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedItemInHandLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>> extends PatchedLayer<E, T, M, LayerRenderer<E, M>> {
	@Override
	public void renderLayer(T entitypatch, E entityliving, LayerRenderer<E, M> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		ItemStack mainHandStack = entitypatch.getOriginal().getMainHandItem();
		RenderEngine renderEngine = ClientEngine.instance.renderEngine;
		
		if (mainHandStack.getItem() != Items.AIR) {
			if (entitypatch.getOriginal().getVehicle() != null) {
				if (!entitypatch.getHoldingItemCapability(Hand.MAIN_HAND).availableOnHorse()) {
					renderEngine.getItemRenderer(mainHandStack.getItem()).renderUnusableItemMount(mainHandStack, entitypatch, buffer, matrixStackIn, packedLightIn);
					return;
				}
			}
			
			renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemInHand(mainHandStack, entitypatch, Hand.MAIN_HAND, buffer, matrixStackIn, packedLightIn);
		}
		
		
		ItemStack offHandStack = entitypatch.getOriginal().getOffhandItem();
		
		if (entitypatch.isOffhandItemValid()) {
			renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entitypatch, Hand.OFF_HAND, buffer, matrixStackIn, packedLightIn);
		}
	}
}