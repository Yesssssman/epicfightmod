package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedEyeLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>> extends PatchedLayer<E, T, M, AbstractEyesLayer<E, M>> {
	private final RenderType renderType;
	private final ClientModel eyeModel;
	
	public PatchedEyeLayer(ResourceLocation eyeTexture, ClientModel eyeModel) {
		this.renderType = RenderType.eyes(eyeTexture);
		this.eyeModel = eyeModel;
	}
	
	@Override
	public void renderLayer(T entitypatch, E entityliving, AbstractEyesLayer<E, M> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		IVertexBuilder ivertexbuilder = buffer.getBuffer(this.renderType);
		this.eyeModel.drawAnimatedModel(matrixStackIn, ivertexbuilder, 15728640, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, poses);
	}
}