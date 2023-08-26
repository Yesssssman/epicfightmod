package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedHeadLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E> & HeadedModel, AM extends HumanoidMesh> extends PatchedLayer<E, T, M, CustomHeadLayer<E, M>, AM> {
	
	public PatchedHeadLayer() {
		super(null);
	}

	@Override
	public void renderLayer(T entitypatch, E entityliving, CustomHeadLayer<E, M> originalRenderer, PoseStack matrixStackIn, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		ItemStack itemstack = entityliving.getItemBySlot(EquipmentSlot.HEAD);
		if (!itemstack.isEmpty()) {
			ModelPart model = originalRenderer.getParentModel().getHead();
			E entity = entitypatch.getOriginal();
			OpenMatrix4f modelMatrix = new OpenMatrix4f();
			modelMatrix.scale(new Vec3f(-1.0F, -1.0F, 1.0F)).mulFront(poses[9]);
			model.x = 0;
			model.y = 0;
			model.z = 0;
			model.xRot = 0;
			model.yRot = 0;
			model.zRot = 0;
			OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
			matrixStackIn.pushPose();
			
			MathUtils.translateStack(matrixStackIn, modelMatrix);
			MathUtils.rotateStack(matrixStackIn, transpose);
			
			if (entitypatch.getOriginal().isBaby()) {
				matrixStackIn.translate(0.0F, -1.2F, 0.0F);
				matrixStackIn.scale(1.6F, 1.6F, 1.6F);
			}
			
			originalRenderer.render(matrixStackIn, buffer, packedLightIn, entity, entity.animationPosition, entity.animationSpeed, packedLightIn, entity.tickCount, netYawHead, pitchHead);
			matrixStackIn.popPose();
		}
	}
}