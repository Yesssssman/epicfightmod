package yesman.epicfight.client.renderer.patched.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.VindicatorPatch;

@OnlyIn(Dist.CLIENT)
public class PVindicatorRenderer extends PAbstractIllagerRenderer<Vindicator, VindicatorPatch<Vindicator>> {
	public PVindicatorRenderer() {
		this.layerRendererReplace.put(ItemInHandLayer.class, new PatchedItemInHandLayer<>() {
			@Override
			public void renderLayer(VindicatorPatch<Vindicator> entitypatch, Vindicator entityliving, RenderLayer<Vindicator, IllagerModel<Vindicator>> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
				if (entityliving.isAggressive()) {
					super.renderLayer(entitypatch, entityliving, originalRenderer, matrixStackIn, buffer, packedLightIn, poses, netYawHead, pitchHead, partialTicks);
				}
			}
		});
	}
}