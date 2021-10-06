package yesman.epicfight.client.renderer.entity;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.mob.VindicatorData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public class VindicatorRenderer extends IllagerRenderer<VindicatorEntity, VindicatorData<VindicatorEntity>> {
	public VindicatorRenderer(String textureLocation) {
		super(textureLocation);
	}
	
	@Override
	protected void renderLayer(LivingRenderer<VindicatorEntity, IllagerModel<VindicatorEntity>> renderer, VindicatorData<VindicatorEntity> entitydata, VindicatorEntity entityIn, OpenMatrix4f[] poses, IRenderTypeBuffer buffer, MatrixStack matrixStackIn, int packedLightIn, float partialTicks) {
		List<LayerRenderer<VindicatorEntity, IllagerModel<VindicatorEntity>>> layers = Lists.newArrayList();
		renderer.layerRenderers.forEach(layers::add);
		Iterator<LayerRenderer<VindicatorEntity, IllagerModel<VindicatorEntity>>> iter = layers.iterator();
		float f = MathUtils.interpolateRotation(entityIn.prevRenderYawOffset, entityIn.renderYawOffset, partialTicks);
        float f1 = MathUtils.interpolateRotation(entityIn.prevRotationYawHead, entityIn.rotationYawHead, partialTicks);
        float f2 = f1 - f;
		float f7 = entityIn.getPitch(partialTicks);
		
		while (iter.hasNext()) {
			LayerRenderer<VindicatorEntity, IllagerModel<VindicatorEntity>> layer = iter.next();
			this.layerRendererReplace.computeIfPresent(layer.getClass(), (key, val) -> {
				val.renderLayer(0, entitydata, entityIn, layer, matrixStackIn, buffer, packedLightIn, poses, f2, f7, partialTicks);
				iter.remove();
				return val;
			});
		}
		
		this.layerRendererReplace.computeIfPresent(HeldItemLayer.class, (key, val) -> {
			if (entityIn.isAggressive()) {
				val.renderLayer(0, entitydata, entityIn, null, matrixStackIn, buffer, packedLightIn, poses, f2, f7, partialTicks);
			}
			return val;
		});
		
		OpenMatrix4f modelMatrix = new OpenMatrix4f();
		OpenMatrix4f.mul(entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(this.getRootJointIndex()).getAnimatedTransform(), modelMatrix, modelMatrix);
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		
		matrixStackIn.push();
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		MathUtils.rotateStack(matrixStackIn, transpose);
		matrixStackIn.translate(0.0D, this.getLayerCorrection(), 0.0D);
		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		layers.forEach((layer) -> {
			if (!(layer instanceof HeldItemLayer)) {
				layer.render(matrixStackIn, buffer, packedLightIn, entityIn, entityIn.limbSwing, entityIn.limbSwingAmount, partialTicks, entityIn.ticksExisted, f2, f7);
			}
		});
		matrixStackIn.pop();
	}
}