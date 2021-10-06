package yesman.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import yesman.epicfight.client.capabilites.player.RemoteClientPlayerData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class CapeAnimatedLayer extends AnimatedLayer<AbstractClientPlayerEntity, RemoteClientPlayerData<AbstractClientPlayerEntity>, PlayerModel<AbstractClientPlayerEntity>, CapeLayer>  {
	@Override
	public void renderLayer(RemoteClientPlayerData<AbstractClientPlayerEntity> entitydata, AbstractClientPlayerEntity entityliving, CapeLayer originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		if (entityliving.hasPlayerInfo() && !entityliving.isInvisible() && entityliving.isWearing(PlayerModelPart.CAPE) && entityliving.getLocationCape() != null) {
			ItemStack itemstack = entityliving.getItemStackFromSlot(EquipmentSlotType.CHEST);
			if (itemstack.getItem() != Items.ELYTRA) {
				OpenMatrix4f modelMatrix = new OpenMatrix4f();
				OpenMatrix4f.scale(new Vec3f(-1.0F, -1.0F, 1.0F), modelMatrix, modelMatrix);
				OpenMatrix4f.mul(entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(8).getAnimatedTransform(), modelMatrix, modelMatrix);
				OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
				matrixStackIn.push();
				MathUtils.translateStack(matrixStackIn, modelMatrix);
				MathUtils.rotateStack(matrixStackIn, transpose);
				matrixStackIn.translate(0.0D, -0.4D, -0.025D);
				originalRenderer.render(matrixStackIn, buffer, packedLightIn, entityliving, entityliving.limbSwing, entityliving.limbSwingAmount, partialTicks, entityliving.ticksExisted, netYawHead, pitchHead);
				matrixStackIn.pop();
			}
		}
	}
}