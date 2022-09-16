package yesman.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.renderer.LightningRenderHelper;

@OnlyIn(Dist.CLIENT)
public class DroppedNetherStarRenderer extends ItemRenderer {
	public DroppedNetherStarRenderer(EntityRendererManager entityRenderManager, net.minecraft.client.renderer.ItemRenderer itemRendrere) {
		super(entityRenderManager, itemRendrere);
	}
	
	@Override
	public void render(ItemEntity entityIn, float yRot, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int packedLight) {
		super.render(entityIn, yRot, partialTicks, poseStack, multiBufferSource, packedLight);
		
		poseStack.pushPose();
		poseStack.translate(0.0D, entityIn.getBbHeight() + MathHelper.sin(((float) entityIn.getAge() + partialTicks) / 10.0F + entityIn.bobOffs) * 0.1F + 0.1F, 0.0D);
		IVertexBuilder vertexBuilder = multiBufferSource.getBuffer(RenderType.lightning());
		float progression = (entityIn.tickCount + partialTicks) * 0.01F;
		float repeater = ((float)Math.sin(progression * 5.0F) + 1.0F) * 0.5F;
		LightningRenderHelper.renderCyclingLight(vertexBuilder, poseStack, 32, 0, 255, 9, 0.05F, progression, repeater);
		poseStack.popPose();
	}
}