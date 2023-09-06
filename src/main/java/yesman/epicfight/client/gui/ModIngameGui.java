package yesman.epicfight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ModIngameGui {
	public void drawTexturedModalRectFixCoord(Matrix4f matrix, float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
		drawTexturedModalRectFixCoord(matrix, xCoord, yCoord, maxU, maxV, this.getBlitOffset(), minU, minV, maxU, maxV);
    }
	
	public static void drawTexturedModalRectFixCoord(Matrix4f matrix, float minX, float minY, float maxX, float maxY, float z, float minU, float minV, float maxU, float maxV) {
		float cor = 0.00390625F;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, minX, minY + maxX, z).uv(minU * cor, (minV + maxV) * cor).endVertex();
        bufferbuilder.vertex(matrix, minX + maxY, minY + maxX, z).uv((minU + maxU) * cor, (minV + maxV) * cor).endVertex();
        bufferbuilder.vertex(matrix, minX + maxY, minY, z).uv((minU + maxU) * cor, (minV * cor)).endVertex();
        bufferbuilder.vertex(matrix, minX, minY, z).uv(minU * cor, minV * cor).endVertex();
        tessellator.end();
	}

	public int getBlitOffset() {
		return 0;
	}
}