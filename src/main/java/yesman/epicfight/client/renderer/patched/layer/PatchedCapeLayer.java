package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedCapeLayer extends PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, CapeLayer, HumanoidMesh>  {
	public PatchedCapeLayer() {
		super(null);
	}
	
	@Override
	protected void renderLayer(AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch, AbstractClientPlayer entityliving, CapeLayer originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		if (entityliving.isCapeLoaded() && !entityliving.isInvisible() && entityliving.isModelPartShown(PlayerModelPart.CAPE) && entityliving.getCloakTextureLocation() != null) {
			ItemStack itemstack = entityliving.getItemBySlot(EquipmentSlot.CHEST);
			
			if (itemstack.getItem() != Items.ELYTRA) {
				OpenMatrix4f modelMatrix = new OpenMatrix4f();
				modelMatrix.scale(new Vec3f(-1.0F, -1.0F, 1.0F)).mulFront(poses[8]);
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