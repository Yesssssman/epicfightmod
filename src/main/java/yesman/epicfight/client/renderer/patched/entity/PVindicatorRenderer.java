package yesman.epicfight.client.renderer.patched.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@OnlyIn(Dist.CLIENT)
public class PVindicatorRenderer extends PIllagerRenderer<AbstractIllagerEntity, MobPatch<AbstractIllagerEntity>> {
	public PVindicatorRenderer() {
		this.addPatchedLayer(HeldItemLayer.class, new PatchedItemInHandLayer<AbstractIllagerEntity, MobPatch<AbstractIllagerEntity>, IllagerModel<AbstractIllagerEntity>>() {
			@Override
			public void renderLayer(MobPatch<AbstractIllagerEntity> entitypatch, AbstractIllagerEntity entityliving, LayerRenderer<AbstractIllagerEntity, IllagerModel<AbstractIllagerEntity>> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
				if (entityliving.isAggressive()) {
					super.renderLayer(entitypatch, entityliving, originalRenderer, matrixStackIn, buffer, packedLightIn, poses, netYawHead, pitchHead, partialTicks);
				}
			}
		});
	}
}