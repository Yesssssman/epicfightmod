package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedEyeLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, AM extends AnimatedMesh> extends PatchedLayer<E, T, M, EyesLayer<E, M>, AM> {
	private final RenderType renderType;
	
	public PatchedEyeLayer(ResourceLocation eyeTexture, AM mesh) {
		super(mesh);
		this.renderType = EpicFightRenderTypes.triangles(RenderType.eyes(eyeTexture));
	}
	
	@Override
	public void renderLayer(T entitypatch, E entityliving, EyesLayer<E, M> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		VertexConsumer ivertexbuilder = buffer.getBuffer(this.renderType);
		this.mesh.drawModelWithPose(matrixStackIn, ivertexbuilder, 15728640, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), poses);
	}
}