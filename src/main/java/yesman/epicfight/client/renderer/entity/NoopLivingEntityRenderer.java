package yesman.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public class NoopLivingEntityRenderer<T extends LivingEntity> extends LivingRenderer<T, EntityModel<T>> {
	public NoopLivingEntityRenderer(EntityRendererManager context, float shadowRadius) {
		super(context, null, shadowRadius);
	}
	
	@Override
	public void render(LivingEntity livingEntity, float yRot, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int packedLight) {
		MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<T, EntityModel<T>>(livingEntity, this, partialTicks, poseStack, multiBufferSource, packedLight));
	}
	
	@Override
	public ResourceLocation getTextureLocation(LivingEntity p_114482_) {
		return null;
	}
}