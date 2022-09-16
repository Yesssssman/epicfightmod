package yesman.epicfight.api.client.forgeevent;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
@Cancelable
public class RenderEnderDragonEvent extends Event {
	private final EnderDragonEntity entity;
    private final EnderDragonRenderer renderer;
    private final float partialRenderTick;
    private final MatrixStack poseStack;
    private final IRenderTypeBuffer buffers;
    private final int light;
	
	public RenderEnderDragonEvent(EnderDragonEntity entity, EnderDragonRenderer renderer, float partialRenderTick, MatrixStack poseStack, IRenderTypeBuffer buffers, int light) {
		this.entity = entity;
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.poseStack = poseStack;
        this.buffers = buffers;
        this.light = light;
	}
	
	public EnderDragonEntity getEntity() {
		return entity;
	}

	public EnderDragonRenderer getRenderer() {
		return renderer;
	}
	
	public float getPartialRenderTick() {
		return partialRenderTick;
	}

	public MatrixStack getPoseStack() {
		return poseStack;
	}

	public IRenderTypeBuffer getBuffers() {
		return buffers;
	}

	public int getLight() {
		return light;
	}
}