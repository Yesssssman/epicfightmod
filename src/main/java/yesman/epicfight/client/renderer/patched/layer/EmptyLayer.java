package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EmptyLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>> extends PatchedLayer<E, T, M, RenderLayer<E, M>> {
	@Override
	public void renderLayer(T entitypatch, E entityliving, RenderLayer<E, M> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		
	}
}