package maninthehouse.epicfight.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModIngameGui extends Gui {
	public void drawTexturedModalRectFixCoord(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
		drawTexturedModalRectFixCoord(xCoord, yCoord, maxU, maxV, (float) this.zLevel, minU, minV, maxU, maxV);
	}

	public static void drawTexturedModalRectFixCoord(float minX, float minY, float maxX, float maxY, float z, float minU, float minV, float maxU, float maxV) {
		float cor = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(minX, minY + maxX, z).tex(minU * cor, (minV + maxV) * cor).endVertex();
        bufferbuilder.pos(minX + maxY, minY + maxX, z).tex((minU + maxU) * cor, (minV + maxV) * cor).endVertex();
        bufferbuilder.pos(minX + maxY, minY, z).tex((minU + maxU) * cor, (minV * cor)).endVertex();
        bufferbuilder.pos(minX, minY, z).tex(minU * cor, minV * cor).endVertex();
        tessellator.draw();
	}
}