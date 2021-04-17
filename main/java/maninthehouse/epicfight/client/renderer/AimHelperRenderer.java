package maninthehouse.epicfight.client.renderer;

import org.lwjgl.opengl.GL11;

import maninthehouse.epicfight.utils.math.MathUtils;
import maninthehouse.epicfight.utils.math.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AimHelperRenderer {
	public void doRender(float partialTicks) {
		Entity entity = Minecraft.getMinecraft().player;
		RayTraceResult ray = entity.rayTrace(200.0D, partialTicks);
		Vec3d vec3 = ray.hitVec;
		Vec3f pos1 = new Vec3f((float) MathUtils.lerp((double)partialTicks, entity.lastTickPosX, entity.posX),
							   (float) MathUtils.lerp((double)partialTicks, entity.lastTickPosY, entity.posY) + entity.getEyeHeight() - 0.15F,
							   (float) MathUtils.lerp((double)partialTicks, entity.lastTickPosZ, entity.posZ));
		Vec3f pos2 = new Vec3f((float) vec3.x, (float) vec3.y, (float) vec3.z);

		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_POINT_SMOOTH);
		GL11.glLineWidth(3.0F);
		
		Vec3d projectedView = ActiveRenderInfo.getCameraPosition();
		GlStateManager.pushMatrix();//matStackIn.push();
		GlStateManager.translate(-projectedView.x, -projectedView.y, -projectedView.z);//matStackIn.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		bufferBuilder.pos(pos1.x, pos1.y, pos1.z).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
		bufferBuilder.pos(pos2.x, pos2.y, pos2.z).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
		tessellator.draw();
		
		float length = Vec3f.sub(pos2, pos1, null).length();
		float ratio = Math.min(50.0F, length);
		ratio = (51.0F - ratio) / 50.0F;
		GL11.glPointSize(ratio * 10.0F);
		bufferBuilder.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
		bufferBuilder.pos(pos2.x, pos2.y, pos2.z).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
		tessellator.draw();
		
		GlStateManager.popMatrix();//matStackIn.pop();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_POINT_SMOOTH);
	}
}
