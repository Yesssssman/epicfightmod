package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.world.capabilities.entitypatch.mob.DrownedPatch;

@OnlyIn(Dist.CLIENT)
public class OuterLayerRenderer extends PatchedLayer<Drowned, DrownedPatch, DrownedModel<Drowned>, DrownedOuterLayer<Drowned>, HumanoidMesh> {
	public static final ResourceLocation DROWNED_OUTER_LAYER = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
	
	public OuterLayerRenderer() {
		super(Meshes.BIPED_OUTLAYER);
	}
	
	@Override
	protected void renderLayer(DrownedPatch entitypatch, Drowned entityliving, DrownedOuterLayer<Drowned> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		this.mesh.drawModelWithPose(matrixStackIn, buffer.getBuffer(EpicFightRenderTypes.triangles(RenderType.entityCutoutNoCull(DROWNED_OUTER_LAYER))), packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, LivingEntityRenderer.getOverlayCoords(entityliving, 0.0F), entitypatch.getArmature(), poses);
	}
	
	@Override
	protected void initMesh() {
		this.mesh.initialize();
	}
}