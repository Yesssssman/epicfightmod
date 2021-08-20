package maninhouse.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.mob.DrownedData;
import maninhouse.epicfight.client.model.ClientModel;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.client.renderer.ModRenderTypes;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.util.ResourceLocation;

public class OuterLayerRenderer extends Layer<DrownedEntity, DrownedData> {
	public static final ResourceLocation DROWNED_OUTER_LAYER = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
	
	@Override
	public void renderLayer(DrownedData entitydata, DrownedEntity entityliving, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn,
			OpenMatrix4f[] poses, float partialTicks) {
		matrixStackIn.push();
		ClientModel model = ClientModels.LOGICAL_CLIENT.ENTITY_BIPED_OUTER_LAYER;
		model.draw(matrixStackIn, buffer.getBuffer(ModRenderTypes.getEntityCutoutNoCull(DROWNED_OUTER_LAYER)), packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		matrixStackIn.pop();
	}
}