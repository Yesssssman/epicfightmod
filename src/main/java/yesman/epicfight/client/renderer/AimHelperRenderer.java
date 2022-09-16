package yesman.epicfight.client.renderer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class AimHelperRenderer {
	public void doRender(MatrixStack matStackIn, float partialTicks) {
		if (!EpicFightMod.CLIENT_INGAME_CONFIG.enableAimHelperPointer.getValue()) {
			return;
		}
		
		Minecraft minecraft = Minecraft.getInstance();
		Entity entity = minecraft.player;
		RayTraceResult ray = entity.pick(200.D, partialTicks, false);
		Vector3d vec3 = ray.getLocation();
		Vec3f pos1 = new Vec3f((float) MathHelper.lerp((double)partialTicks, entity.xOld, entity.getX()),
							   (float) MathHelper.lerp((double)partialTicks, entity.yOld, entity.getY()) + entity.getEyeHeight() - 0.15F,
							   (float) MathHelper.lerp((double)partialTicks, entity.zOld, entity.getZ()));
		Vec3f pos2 = new Vec3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
		
		ActiveRenderInfo renderInfo = minecraft.gameRenderer.getMainCamera();
		Vector3d projectedView = renderInfo.getPosition();
		matStackIn.pushPose();
		matStackIn.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		Matrix4f matrix = matStackIn.last().pose();
		
		int color = EpicFightMod.CLIENT_INGAME_CONFIG.aimHelperRealColor;
		float f1 = (float)(color >> 16 & 255) / 255.0F;
		float f2 = (float)(color >> 8 & 255) / 255.0F;
		float f3 = (float)(color & 255) / 255.0F;
		
		Tessellator tesselator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();
		
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.lineWidth(3.0F);
		bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, pos1.x, pos1.y, pos1.z).color(f1, f2, f3, 0.5F).endVertex();
		bufferBuilder.vertex(matrix, pos2.x, pos2.y, pos2.z).color(f1, f2, f3, 0.5F).endVertex();
		tesselator.end();
		
		float length = Vec3f.sub(pos2, pos1, null).length();
		float ratio = Math.min(50.0F, length);
		ratio = (51.0F - ratio) / 50.0F;
		
		matStackIn.popPose();
	}
}
