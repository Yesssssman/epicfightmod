package yesman.epicfight.api.client.forgeevent;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PrepareModelEvent extends Event {
	private final AnimatedMesh mesh;
	private final LivingEntityPatch<?> entitypatch;
	private final MultiBufferSource buffer;
	private final PoseStack poseStack;
	private final int packedLight;
	private final float partialTicks;
	
	private final PatchedLivingEntityRenderer<?, ?, ?, ?> renderer;
	
	public PrepareModelEvent(PatchedLivingEntityRenderer<?, ?, ?, ?> renderer, AnimatedMesh mesh, LivingEntityPatch<?> entitypatch, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		this.renderer = renderer;
		this.mesh = mesh;
		this.entitypatch = entitypatch;
		this.buffer = buffer;
		this.poseStack = poseStack;
		this.packedLight = packedLight;
		this.partialTicks = partialTicks;
	}

	public AnimatedMesh getMesh() {
		return this.mesh;
	}
	
	public LivingEntityPatch<?> getEntityPatch() {
		return this.entitypatch;
	}

	public MultiBufferSource getBuffer() {
		return this.buffer;
	}

	public PoseStack getPoseStack() {
		return this.poseStack;
	}

	public int getPackedLight() {
		return this.packedLight;
	}

	public float getPartialTicks() {
		return this.partialTicks;
	}

	public PatchedLivingEntityRenderer<?, ?, ?, ?> getRenderer() {
		return this.renderer;
	}
}