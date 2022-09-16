package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedCapeLayer extends PatchedLayer<AbstractClientPlayerEntity, AbstractClientPlayerPatch<AbstractClientPlayerEntity>, PlayerModel<AbstractClientPlayerEntity>, CapeLayer>  {
	@Override
	public void renderLayer(AbstractClientPlayerPatch<AbstractClientPlayerEntity> entitypatch, AbstractClientPlayerEntity entityliving, CapeLayer originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		if (entityliving.isCapeLoaded() && !entityliving.isInvisible() && entityliving.isModelPartShown(PlayerModelPart.CAPE) && entityliving.getCloakTextureLocation() != null) {
			ItemStack itemstack = entityliving.getItemBySlot(EquipmentSlotType.CHEST);
			if (itemstack.getItem() != Items.ELYTRA) {
				OpenMatrix4f modelMatrix = new OpenMatrix4f();
				modelMatrix.scale(new Vec3f(-1.0F, -1.0F, 1.0F)).mulFront(entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().searchJointById(8).getAnimatedTransform());
				OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
				matrixStackIn.pushPose();
				MathUtils.translateStack(matrixStackIn, modelMatrix);
				MathUtils.rotateStack(matrixStackIn, transpose);
				matrixStackIn.translate(0.0D, -0.4D, -0.025D);
				originalRenderer.render(matrixStackIn, buffer, packedLightIn, entityliving, entityliving.animationPosition, entityliving.animationSpeed, partialTicks, entityliving.tickCount, netYawHead, pitchHead);
				matrixStackIn.popPose();
			}
		}
	}
}