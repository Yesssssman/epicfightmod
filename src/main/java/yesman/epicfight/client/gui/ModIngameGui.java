package yesman.epicfight.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModIngameGui extends AbstractGui {
	public void drawTexturedModalRectFixCoord(Matrix4f matrix, float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
		drawTexturedModalRectFixCoord(matrix, xCoord, yCoord, maxU, maxV, (float)this.getBlitOffset(), minU, minV, maxU, maxV);
    }
	
	public static void drawTexturedModalRectFixCoord(Matrix4f matrix, float minX, float minY, float maxX, float maxY, float z, float minU, float minV, float maxU, float maxV) {
		float cor = 0.00390625F;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.vertex(matrix, minX, minY + maxX, z).uv(minU * cor, (minV + maxV) * cor).endVertex();
        bufferbuilder.vertex(matrix, minX + maxY, minY + maxX, z).uv((minU + maxU) * cor, (minV + maxV) * cor).endVertex();
        bufferbuilder.vertex(matrix, minX + maxY, minY, z).uv((minU + maxU) * cor, (minV * cor)).endVertex();
        bufferbuilder.vertex(matrix, minX, minY, z).uv(minU * cor, minV * cor).endVertex();
        tessellator.end();
	}
}