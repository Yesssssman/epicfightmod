package yesman.epicfight.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class RenderingTool {
	public static void drawQuad(MatrixStack poseStack, IVertexBuilder vertexBuilder, Vec3f pos, float size, float r, float g, float b) {
		vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y, pos.z - size).color(r, g, b, 1.0F).endVertex();
	}
	
	public static void drawCube(MatrixStack poseStack, IVertexBuilder vertexBuilder, Vec3f pos, float size, float r, float g, float b) {
		vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y - size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y - size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y - size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y - size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y + size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y + size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y + size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y + size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y + size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y + size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y - size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y - size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y + size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y + size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y - size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y - size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y + size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y + size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y - size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y - size, pos.z - size).color(r, g, b, 1.0F).endVertex();
        
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y + size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y + size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x - size, pos.y - size, pos.z + size).color(r, g, b, 1.0F).endVertex();
        vertexBuilder.vertex(poseStack.last().pose(), pos.x + size, pos.y - size, pos.z + size).color(r, g, b, 1.0F).endVertex();
	}
}