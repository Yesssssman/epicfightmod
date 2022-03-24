package yesman.epicfight.client.renderer.patched.entity;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.mob.VindicatorPatch;

@OnlyIn(Dist.CLIENT)
public class PVindicatorRenderer extends PPIllagerRenderer<Vindicator, VindicatorPatch<Vindicator>> {
	public PVindicatorRenderer(String textureLocation) {
		super(textureLocation);
	}
	
	@Override
	protected void renderLayer(LivingEntityRenderer<Vindicator, IllagerModel<Vindicator>> renderer, VindicatorPatch<Vindicator> entitypatch, Vindicator entityIn, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack matrixStackIn, int packedLightIn, float partialTicks) {
		List<RenderLayer<Vindicator, IllagerModel<Vindicator>>> layers = Lists.newArrayList();
		renderer.layers.forEach(layers::add);
		Iterator<RenderLayer<Vindicator, IllagerModel<Vindicator>>> iter = layers.iterator();
		float f = MathUtils.lerpBetween(entityIn.yBodyRotO, entityIn.yBodyRot, partialTicks);
        float f1 = MathUtils.lerpBetween(entityIn.yHeadRotO, entityIn.yHeadRot, partialTicks);
        float f2 = f1 - f;
		float f7 = entityIn.getViewXRot(partialTicks);
		
		while (iter.hasNext()) {
			RenderLayer<Vindicator, IllagerModel<Vindicator>> layer = iter.next();
			this.layerRendererReplace.computeIfPresent(layer.getClass(), (key, val) -> {
				val.renderLayer(0, entitypatch, entityIn, layer, matrixStackIn, buffer, packedLightIn, poses, f2, f7, partialTicks);
				iter.remove();
				return val;
			});
		}
		
		this.layerRendererReplace.computeIfPresent(ItemInHandLayer.class, (key, val) -> {
			if (entityIn.isAggressive()) {
				val.renderLayer(0, entitypatch, entityIn, null, matrixStackIn, buffer, packedLightIn, poses, f2, f7, partialTicks);
			}
			return val;
		});
		
		OpenMatrix4f modelMatrix = new OpenMatrix4f();
		modelMatrix.mulFront(entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointById(this.getRootJointIndex()).getAnimatedTransform());
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		
		matrixStackIn.pushPose();
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		MathUtils.rotateStack(matrixStackIn, transpose);
		matrixStackIn.translate(0.0D, this.getLayerCorrection(), 0.0D);
		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		layers.forEach((layer) -> {
			if (!(layer instanceof ItemInHandLayer)) {
				layer.render(matrixStackIn, buffer, packedLightIn, entityIn, entityIn.animationPosition, entityIn.animationSpeed, partialTicks, entityIn.tickCount, f2, f7);
			}
		});
		matrixStackIn.popPose();
	}
}