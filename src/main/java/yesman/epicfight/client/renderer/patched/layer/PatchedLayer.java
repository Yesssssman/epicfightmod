package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public abstract class PatchedLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, R extends RenderLayer<E, M>, AM extends AnimatedMesh> {
	protected final AM mesh;
	
	public PatchedLayer(AM mesh) {
		this.mesh = mesh;
	}
	
	public final void renderLayer(int z, T entitypatch, E entityliving, RenderLayer<E, M> vanillaLayer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn,
			OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		this.initMesh();
		this.renderLayer(entitypatch, entityliving, this.cast(vanillaLayer), matrixStackIn, buffer, packedLightIn, poses, bob, yRot, xRot, partialTicks);
	}
	
	protected abstract void renderLayer(T entitypatch, E entityliving, R vanillaLayer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn,
			OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks);
	
	protected void initMesh() {
		if (this.mesh != null) {
			this.mesh.initialize();
		}
	}
	
	@SuppressWarnings("unchecked")
	private R cast(RenderLayer<E, M> layer) {
		return (R)layer;
	}
}