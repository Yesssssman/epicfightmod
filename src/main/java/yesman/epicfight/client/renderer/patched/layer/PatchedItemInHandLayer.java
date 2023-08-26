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
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedItemInHandLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, AM extends HumanoidMesh> extends PatchedLayer<E, T, M, RenderLayer<E, M>, AM> {
	
	public PatchedItemInHandLayer() {
		super(null);
	}

	@Override
	public void renderLayer(T entitypatch, E entityliving, RenderLayer<E, M> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		
		if (!(entitypatch.getArmature() instanceof HumanoidArmature)) {
			return;
		}
		
		ItemStack mainHandStack = entitypatch.getOriginal().getMainHandItem();
		RenderEngine renderEngine = ClientEngine.getInstance().renderEngine;
		
		if (mainHandStack.getItem() != Items.AIR) {
			if (entitypatch.getOriginal().getVehicle() != null) {
				if (!entitypatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).availableOnHorse()) {
					renderEngine.getItemRenderer(mainHandStack.getItem()).renderUnusableItemMount(mainHandStack, entitypatch, poses, buffer, matrixStackIn, packedLightIn);
					return;
				}
			}
			
			renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemInHand(mainHandStack, entitypatch, InteractionHand.MAIN_HAND, (HumanoidArmature)entitypatch.getArmature(), poses, buffer, matrixStackIn, packedLightIn);
		}
		
		ItemStack offHandStack = entitypatch.getOriginal().getOffhandItem();
		
		if (entitypatch.isOffhandItemValid()) {
			renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entitypatch, InteractionHand.OFF_HAND, (HumanoidArmature)entitypatch.getArmature(), poses, buffer, matrixStackIn, packedLightIn);
		}
	}
}