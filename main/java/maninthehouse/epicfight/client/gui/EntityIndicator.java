package maninthehouse.epicfight.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class EntityIndicator extends ModIngameGui {
	public static final List<EntityIndicator> ENTITY_INDICATOR_RENDERERS = Lists.newArrayList();
	public static final ResourceLocation BATTLE_ICON = new ResourceLocation(EpicFightMod.MODID, "textures/gui/battle_icons.png");
	
	public static void init() {
		new TargetIndicator();
		new HealthBarIndicator();
	}
	
	public void drawTexturedModalRect2DPlane(float minX, float minY, float maxX, float maxY, float minTexU, float minTexV, float maxTexU, float maxTexV) {
		this.drawTexturedModalRect3DPlane(minX, minY, this.zLevel, maxX, maxY, this.zLevel, minTexU, minTexV, maxTexU, maxTexV);
    }
	
	public void drawTexturedModalRect3DPlane(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float minTexU, float minTexV, float maxTexU, float maxTexV) {
        float cor = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(minX, minY, maxZ).tex((minTexU * cor), (maxTexV) * cor).endVertex();
        bufferBuilder.pos(maxX, minY, maxZ).tex((maxTexU * cor), (maxTexV) * cor).endVertex();
        bufferBuilder.pos(maxX, maxY, minZ).tex((maxTexU * cor), (minTexV) * cor).endVertex();
        bufferBuilder.pos(minX, maxY, minZ).tex((minTexU * cor), (minTexV) * cor).endVertex();
        tessellator.draw();
    }
	
	public EntityIndicator() {
		EntityIndicator.ENTITY_INDICATOR_RENDERERS.add(this);
	}
	
	public VisibleMatrix4f setupMatrix(double x, double y, double z, float correctionX, float correctionY, float correctionZ, boolean lockRotation, boolean setupProjection, float partialTicks) {
		VisibleMatrix4f matrix = new VisibleMatrix4f();
		matrix.translate((float)x, (float)y + correctionY, (float)z);
		matrix.rotateDegree(-Minecraft.getMinecraft().getRenderViewEntity().rotationYaw + 180.0F, new Vec3f(0, 1, 0));
		matrix.rotateDegree(-Minecraft.getMinecraft().getRenderViewEntity().rotationPitch, new Vec3f(1, 0, 0));
		
		return this.setupMatrix(matrix, lockRotation, setupProjection);
	}
	
	public VisibleMatrix4f setupMatrix(VisibleMatrix4f matrix, boolean lockRotation, boolean setupProjection) {
		VisibleMatrix4f finalMatrix = new VisibleMatrix4f();
		VisibleMatrix4f.mul(matrix, finalMatrix, finalMatrix);
		
		if(setupProjection) {
			VisibleMatrix4f.mul(ClientEngine.INSTANCE.renderEngine.getCurrentProjectionMatrix(), finalMatrix, finalMatrix);
		}
		
		return finalMatrix;
	}
	
	public abstract void drawIndicator(EntityLivingBase entityIn, double x, double y, double z, float partialTicks);
	public abstract boolean shouldDraw(EntityLivingBase entityIn);
}