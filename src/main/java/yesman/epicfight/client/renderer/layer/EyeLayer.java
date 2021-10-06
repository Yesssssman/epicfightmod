package yesman.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.model.ClientModel;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public class EyeLayer<E extends LivingEntity, T extends LivingData<E>, M extends EntityModel<E>> extends AnimatedLayer<E, T, M, AbstractEyesLayer<E, M>> {
	private final RenderType renderType;
	private final ClientModel eyeModel;
	
	public EyeLayer(ResourceLocation eyeTexture, ClientModel eyeModel) {
		this.renderType = RenderType.getEyes(eyeTexture);
		this.eyeModel = eyeModel;
	}
	
	@Override
	public void renderLayer(T entitydata, E entityliving, AbstractEyesLayer<E, M> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		IVertexBuilder ivertexbuilder = buffer.getBuffer(this.renderType);
		this.eyeModel.draw(matrixStackIn, ivertexbuilder, 15728640, 1.0F, 1.0F, 1.0F, 1.0F, poses);
	}
}