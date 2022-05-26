package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedItemInHandLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>> extends PatchedLayer<E, T, M, RenderLayer<E, M>> {
	@Override
	public void renderLayer(T entitypatch, E entityliving, RenderLayer<E, M> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		ItemStack mainHandStack = entitypatch.getOriginal().getMainHandItem();
		RenderEngine renderEngine = ClientEngine.instance.renderEngine;
		
		if (mainHandStack.getItem() != Items.AIR) {
			if (entitypatch.getOriginal().getVehicle() != null) {
				if (!entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).availableOnHorse()) {
					renderEngine.getItemRenderer(mainHandStack.getItem()).renderUnusableItemMount(mainHandStack, entitypatch, buffer, matrixStackIn, packedLightIn);
					return;
				}
			}
			
			renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemInHand(mainHandStack, entitypatch, InteractionHand.MAIN_HAND, buffer, matrixStackIn, packedLightIn);
		}
		
		
		ItemStack offHandStack = entitypatch.getOriginal().getOffhandItem();
		
		if (entitypatch.isOffhandItemValid()) {
			renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entitypatch, InteractionHand.OFF_HAND, buffer, matrixStackIn, packedLightIn);
		}
	}
}