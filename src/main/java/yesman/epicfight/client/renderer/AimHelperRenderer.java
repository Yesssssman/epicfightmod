package yesman.epicfight.client.renderer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class AimHelperRenderer {
	public void doRender(MatrixStack matStackIn, float partialTicks) {
		if (!EpicFightMod.CLIENT_INGAME_CONFIG.enableAimHelperPointer.getValue()) {
			return;
		}
		
		Entity entity = Minecraft.getInstance().player;
		RayTraceResult ray = entity.pick(200.D, partialTicks, false);
		Vector3d vec3 = ray.getHitVec();
		Vec3f pos1 = new Vec3f((float) MathHelper.lerp((double)partialTicks, entity.lastTickPosX, entity.getPosX()),
							   (float) MathHelper.lerp((double)partialTicks, entity.lastTickPosY, entity.getPosY()) + entity.getEyeHeight() - 0.15F,
							   (float) MathHelper.lerp((double)partialTicks, entity.lastTickPosZ, entity.getPosZ()));
		Vec3f pos2 = new Vec3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
		RenderType renderType = ModRenderTypes.getAimHelper();
		
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_POINT_SMOOTH);
		GL11.glLineWidth(3.0F);
		
		ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
		Vector3d projectedView = renderInfo.getProjectedView();
		matStackIn.push();
		matStackIn.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		Matrix4f matrix = matStackIn.getLast().getMatrix();
		
		int color = EpicFightMod.CLIENT_INGAME_CONFIG.aimHelperRealColor;
		float f1 = (float)(color >> 16 & 255) / 255.0F;
		float f2 = (float)(color >> 8 & 255) / 255.0F;
		float f3 = (float)(color & 255) / 255.0F;
		
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(renderType.getDrawMode(), renderType.getVertexFormat());
		bufferBuilder.pos(matrix, pos1.x, pos1.y, pos1.z).color(f1, f2, f3, 0.5F).endVertex();
		bufferBuilder.pos(matrix, pos2.x, pos2.y, pos2.z).color(f1, f2, f3, 0.5F).endVertex();
		renderType.finish(bufferBuilder, 0, 0, 0);
		
		float length = Vec3f.sub(pos2, pos1, null).length();
		float ratio = Math.min(50.0F, length);
		ratio = (51.0F - ratio) / 50.0F;
		GL11.glPointSize(ratio * 10.0F);
		bufferBuilder.begin(GL11.GL_POINTS, renderType.getVertexFormat());
		bufferBuilder.pos(matrix, pos2.x, pos2.y, pos2.z).color(f1, f2, f3, 0.5F).endVertex();
		renderType.finish(bufferBuilder, 0, 0, 0);
		
		matStackIn.pop();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_POINT_SMOOTH);
	}
}
