package yesman.epicfight.api.client.model.forgeevent;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class RenderEnderDragonEvent extends Event {
	private final EnderDragon entity;
    private final EnderDragonRenderer renderer;
    private final float partialRenderTick;
    private final PoseStack poseStack;
    private final MultiBufferSource buffers;
    private final int light;
	
	public RenderEnderDragonEvent(EnderDragon entity, EnderDragonRenderer renderer, float partialRenderTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
		this.entity = entity;
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.poseStack = poseStack;
        this.buffers = buffers;
        this.light = light;
	}
	
	public EnderDragon getEntity() {
		return entity;
	}

	public EnderDragonRenderer getRenderer() {
		return renderer;
	}

	public float getPartialRenderTick() {
		return partialRenderTick;
	}

	public PoseStack getPoseStack() {
		return poseStack;
	}

	public MultiBufferSource getBuffers() {
		return buffers;
	}

	public int getLight() {
		return light;
	}
	
	@Cancelable
	public static class Pre extends RenderEnderDragonEvent {
        public Pre(EnderDragon entity, EnderDragonRenderer renderer, float partialRenderTick, PoseStack matrixStack, MultiBufferSource buffers, int light) {
            super(entity, renderer, partialRenderTick, matrixStack, buffers, light);
        }
    }
}