package yesman.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class ElytraAnimatedLayer<E extends LivingEntity, T extends LivingData<E>, M extends EntityModel<E>> extends AnimatedLayer<E, T, M, ElytraLayer<E, M>> {
	@Override
	public void renderLayer(T entitydata, E livingentity, ElytraLayer<E, M> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		if (originalRenderer.shouldRender(livingentity.getItemStackFromSlot(EquipmentSlotType.CHEST), livingentity)) {
			OpenMatrix4f modelMatrix = new OpenMatrix4f();
			OpenMatrix4f.scale(new Vec3f(-0.9F, -0.9F, 0.9F), modelMatrix, modelMatrix);
			OpenMatrix4f.translate(new Vec3f(0.0F, -0.5F, -0.1F), modelMatrix, modelMatrix);
			OpenMatrix4f.mul(entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(8).getAnimatedTransform(), modelMatrix, modelMatrix);
			OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
			
			matrixStackIn.push();
			MathUtils.translateStack(matrixStackIn, modelMatrix);
			MathUtils.rotateStack(matrixStackIn, transpose);
			originalRenderer.render(matrixStackIn, buffer, packedLightIn, livingentity, livingentity.limbSwing, livingentity.limbSwingAmount, partialTicks, livingentity.ticksExisted, netYawHead, pitchHead);
			matrixStackIn.pop();
		}
	}
}