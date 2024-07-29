package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedEyesLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, AM extends AnimatedMesh> extends ModelRenderLayer<E, T, M, EyesLayer<E, M>, AM> {
	private final RenderType renderType;
	
	public PatchedEyesLayer(ResourceLocation eyeTexture, AM mesh) {
		super(mesh);
		
		this.renderType = RenderType.eyes(eyeTexture);
	}
	
	@Override
	protected void renderLayer(T entitypatch, E entityliving, EyesLayer<E, M> vanillaLayer, PoseStack postStack, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		this.mesh.draw(postStack, buffer, this.renderType, 15728640, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), poses);
	}
}