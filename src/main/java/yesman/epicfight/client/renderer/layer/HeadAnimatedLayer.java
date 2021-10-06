package yesman.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class HeadAnimatedLayer<E extends LivingEntity, T extends LivingData<E>, M extends EntityModel<E> & IHasHead> extends AnimatedLayer<E, T, M, HeadLayer<E, M>> {
	@Override
	public void renderLayer(T entitydata, E entityliving, HeadLayer<E, M> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		ItemStack itemstack = entityliving.getItemStackFromSlot(EquipmentSlotType.HEAD);
		if (!itemstack.isEmpty()) {
			ModelRenderer model = originalRenderer.getEntityModel().getModelHead();
			E entity = entitydata.getOriginalEntity();
			OpenMatrix4f modelMatrix = new OpenMatrix4f();
			OpenMatrix4f.scale(new Vec3f(-1.0F, -1.0F, 1.0F), modelMatrix, modelMatrix);
			OpenMatrix4f.mul(entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
			model.rotateAngleX = 0;
			model.rotateAngleY = 0;
			model.rotateAngleZ = 0;
			model.rotationPointX = 0;
			model.rotationPointY = 0;
			model.rotationPointZ = 0;
			OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
			matrixStackIn.push();
			
			MathUtils.translateStack(matrixStackIn, modelMatrix);
			MathUtils.rotateStack(matrixStackIn, transpose);
			
			if (entitydata.getOriginalEntity().isChild()) {
				matrixStackIn.translate(0.0F, -1.2F, 0.0F);
				matrixStackIn.scale(1.6F, 1.6F, 1.6F);
			}
			
			originalRenderer.render(matrixStackIn, buffer, packedLightIn, entity, entity.limbSwing, entity.limbSwingAmount, packedLightIn, entity.ticksExisted, netYawHead, pitchHead);
			matrixStackIn.pop();
		}
	}
}