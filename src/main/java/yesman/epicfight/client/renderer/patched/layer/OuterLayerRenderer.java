package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.model.DrownedModel;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.world.capabilities.entitypatch.mob.DrownedPatch;

@OnlyIn(Dist.CLIENT)
public class OuterLayerRenderer extends PatchedLayer<DrownedEntity, DrownedPatch, DrownedModel<DrownedEntity>, DrownedOuterLayer<DrownedEntity>> {
	public static final ResourceLocation DROWNED_OUTER_LAYER = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
	
	@Override
	public void renderLayer(DrownedPatch entitypatch, DrownedEntity entityliving, DrownedOuterLayer<DrownedEntity> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		matrixStackIn.pushPose();
		ClientModel model = ClientModels.LOGICAL_CLIENT.drownedOuterLayer;
		model.drawAnimatedModel(matrixStackIn, buffer.getBuffer(EpicFightRenderTypes.animatedModel(DROWNED_OUTER_LAYER)), packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, LivingRenderer.getOverlayCoords(entityliving, 0.0F), poses);
		matrixStackIn.popPose();
	}
}