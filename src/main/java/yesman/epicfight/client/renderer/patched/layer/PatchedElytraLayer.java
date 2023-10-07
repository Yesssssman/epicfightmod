package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedElytraLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, AM extends HumanoidMesh> extends PatchedLayer<E, T, M, ElytraLayer<E, M>, AM> {
	
	public PatchedElytraLayer() {
		super(null);
	}
	
	@Override
	protected void renderLayer(T entitypatch, E livingentity, ElytraLayer<E, M> vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn,
			OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		if (vanillaLayer.shouldRender(livingentity.getItemBySlot(EquipmentSlot.CHEST), livingentity)) {
			vanillaLayer.getParentModel().copyPropertiesTo(vanillaLayer.elytraModel);
			OpenMatrix4f modelMatrix = new OpenMatrix4f();
			modelMatrix.scale(new Vec3f(-0.9F, -0.9F, 0.9F)).translate(new Vec3f(0.0F, -0.5F, -0.1F)).mulFront(poses[8]);
			OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
			poseStack.pushPose();
			MathUtils.translateStack(poseStack, modelMatrix);
			MathUtils.rotateStack(poseStack, transpose);
			vanillaLayer.render(poseStack, buffer, packedLightIn, livingentity, livingentity.walkAnimation.position(), livingentity.walkAnimation.speed(), partialTicks, bob, yRot, xRot);
			poseStack.popPose();
		}
	}
}