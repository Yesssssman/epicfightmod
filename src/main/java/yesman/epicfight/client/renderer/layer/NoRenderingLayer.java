package yesman.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.utils.math.OpenMatrix4f;

public class NoRenderingLayer<E extends LivingEntity, T extends LivingData<E>, M extends EntityModel<E>> extends AnimatedLayer<E, T, M, LayerRenderer<E, M>> {
	@Override
	public void renderLayer(T entitydata, E entityliving, LayerRenderer<E, M> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		
	}
}