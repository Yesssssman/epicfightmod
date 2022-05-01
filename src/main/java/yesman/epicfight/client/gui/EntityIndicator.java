package yesman.epicfight.client.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public abstract class EntityIndicator extends ModIngameGui {
	public static final List<EntityIndicator> ENTITY_INDICATOR_RENDERERS = Lists.newArrayList();
	public static final ResourceLocation BATTLE_ICON = new ResourceLocation(EpicFightMod.MODID, "textures/gui/battle_icons.png");
	
	public static void init() {
		new TargetIndicator();
		new HealthBarIndicator();
	}
	
	public void drawTexturedModalRect2DPlane(Matrix4f matrix, VertexConsumer vertexBuilder, float minX, float minY, float maxX, float maxY, float minTexU, float minTexV, float maxTexU, float maxTexV) {
		this.drawTexturedModalRect3DPlane(matrix, vertexBuilder, minX, minY, this.getBlitOffset(), maxX, maxY, this.getBlitOffset(), minTexU, minTexV, maxTexU, maxTexV);
	}
	
	public void drawTexturedModalRect3DPlane(Matrix4f matrix, VertexConsumer vertexBuilder, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float minTexU, float minTexV, float maxTexU, float maxTexV) {
		float cor = 0.00390625F;
		vertexBuilder.vertex(matrix, minX, minY, maxZ).uv((minTexU * cor), (maxTexV) * cor).endVertex();
        vertexBuilder.vertex(matrix, maxX, minY, maxZ).uv((maxTexU * cor), (maxTexV) * cor).endVertex();
        vertexBuilder.vertex(matrix, maxX, maxY, minZ).uv((maxTexU * cor), (minTexV) * cor).endVertex();
        vertexBuilder.vertex(matrix, minX, maxY, minZ).uv((minTexU * cor), (minTexV) * cor).endVertex();
	}
	
	public EntityIndicator() {
		EntityIndicator.ENTITY_INDICATOR_RENDERERS.add(this);
	}
	
	public final Matrix4f getMVMatrix(PoseStack matStackIn, LivingEntity entityIn, float correctionX, float correctionY, float correctionZ, boolean lockRotation, float partialTicks) {
		float posX = (float)Mth.lerp((double)partialTicks, entityIn.xOld, entityIn.getX());
		float posY = (float)Mth.lerp((double)partialTicks, entityIn.yOld, entityIn.getY());
		float posZ = (float)Mth.lerp((double)partialTicks, entityIn.zOld, entityIn.getZ());
		matStackIn.pushPose();
		matStackIn.translate(-posX, -posY, -posZ);
		matStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		return this.getMVMatrix(matStackIn, posX + correctionX, posY + correctionY, posZ + correctionZ, lockRotation);
	}
	
	public final Matrix4f getMVMatrix(PoseStack matStackIn, float posX, float posY, float posZ, boolean lockRotation) {
		OpenMatrix4f viewMatrix = OpenMatrix4f.importFromMojangMatrix(matStackIn.last().pose());
		OpenMatrix4f finalMatrix = new OpenMatrix4f();
		finalMatrix.translate(new Vec3f(-posX, posY, -posZ));
		matStackIn.popPose();
		
		if (lockRotation) {
			finalMatrix.m00 = viewMatrix.m00;
			finalMatrix.m01 = viewMatrix.m10;
			finalMatrix.m02 = viewMatrix.m20;
			finalMatrix.m10 = viewMatrix.m01;
			finalMatrix.m11 = viewMatrix.m11;
			finalMatrix.m12 = viewMatrix.m21;
			finalMatrix.m20 = viewMatrix.m02;
			finalMatrix.m21 = viewMatrix.m12;
			finalMatrix.m22 = viewMatrix.m22;
		}
		
		finalMatrix.mulFront(viewMatrix);
		
		return OpenMatrix4f.exportToMojangMatrix(finalMatrix);
	}
	
	public abstract void drawIndicator(LivingEntity entityIn, PoseStack matStackIn, MultiBufferSource VertexConsumer, float partialTicks);
	public abstract boolean shouldDraw(LocalPlayer player, LivingEntity entityIn);
}