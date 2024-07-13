package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public abstract class PatchedStuckInBodyLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends PlayerModel<E>, R extends StuckInBodyLayer<E, M>> extends PatchedLayer<E, T, M, R> {
	@Override
	protected void renderLayer(T entitypatch, E entityliving, R vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
			OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		int i = Math.min(EpicFightMod.CLIENT_CONFIGS.maxStuckProjectiles.getValue(), this.numStuck(entityliving));
		
		RandomSource randomsource = RandomSource.create((long) entityliving.getId());
		
		if (i > 0) {
			for (int j = 0; j < i; ++j) {
				poseStack.pushPose();
				
				int randomJoint = Math.abs(randomsource.nextInt()) % entitypatch.getArmature().getJointNumber();
				
				OpenMatrix4f modelMatrix = new OpenMatrix4f().mulFront(poses[randomJoint]);
				OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
				MathUtils.translateStack(poseStack, modelMatrix);
				MathUtils.rotateStack(poseStack, transpose);
	            Vec3f vec = entitypatch.getArmature().searchJointById(randomJoint).getLocalTrasnform().toTranslationVector();
	            
				float f = randomsource.nextFloat();
				float f1 = randomsource.nextFloat();
				float f2 = randomsource.nextFloat();
				float f3 = Mth.lerp(f, -vec.x * 0.5F, vec.x * 0.5F);
				float f4 = Mth.lerp(f1, -vec.y * 0.5F, vec.y * 0.5F);
				float f5 = Mth.lerp(f2, -vec.z * 0.5F, vec.z * 0.5F);
				poseStack.translate(f3, f4, f5);
				f = -1.0F * (f * 2.0F - 1.0F);
				f1 = -1.0F * (f1 * 2.0F - 1.0F);
				f2 = -1.0F * (f2 * 2.0F - 1.0F);
				this.renderStuckItem(poseStack, buffer, packedLight, entityliving, f, f1, f2, partialTicks);
				
				poseStack.popPose();
			}
		}
	}
	
	protected abstract int numStuck(E entity);
	protected abstract void renderStuckItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Entity entity, float f1, float f2, float f3, float partialTick);
}
