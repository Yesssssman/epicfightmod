package yesman.epicfight.client.gui;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public abstract class EntityIndicator extends GuiComponent {
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
	
	public final Matrix4f getMVMatrix(PoseStack poseStack, LivingEntity entity, float x, float y, float z, boolean lockRotation, float partialTicks) {
		float posX = (float)Mth.lerp((double)partialTicks, entity.xOld, entity.getX());
		float posY = (float)Mth.lerp((double)partialTicks, entity.yOld, entity.getY());
		float posZ = (float)Mth.lerp((double)partialTicks, entity.zOld, entity.getZ());
		poseStack.pushPose();
		poseStack.translate(-posX, -posY, -posZ);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		
		float screenX = posX + x;
		float screenY = posY + y;
		float screenZ = posZ + z;
		
		OpenMatrix4f viewMatrix = OpenMatrix4f.importFromMojangMatrix(poseStack.last().pose());
		OpenMatrix4f finalMatrix = new OpenMatrix4f();
		finalMatrix.translate(new Vec3f(-screenX, screenY, -screenZ));
		poseStack.popPose();
		
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
	
	public abstract void drawIndicator(LivingEntity entityIn, @Nullable LivingEntityPatch<?> entitypatch, LocalPlayerPatch playerpatch, PoseStack matStackIn, MultiBufferSource VertexConsumer, float partialTicks);
	public abstract boolean shouldDraw(LivingEntity entityIn, @Nullable LivingEntityPatch<?> entitypatch, LocalPlayerPatch playerpatch);
}