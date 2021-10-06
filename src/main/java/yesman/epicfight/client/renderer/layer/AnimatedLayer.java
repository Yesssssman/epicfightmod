package yesman.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public abstract class AnimatedLayer<E extends LivingEntity, T extends LivingData<E>, M extends EntityModel<E>, R extends LayerRenderer<E, M>> {
	public final void renderLayer(int z, T entitydata, E entityliving, LayerRenderer<E, M> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		this.renderLayer(entitydata, entityliving, this.cast(originalRenderer), matrixStackIn, buffer, packedLightIn, poses, netYawHead, pitchHead, partialTicks);
	}
	
	public abstract void renderLayer(T entitydata, E entityliving, R originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks);
	
	@SuppressWarnings("unchecked")
	private R cast(LayerRenderer<E, M> layer) {
		return (R)layer;
	}
}