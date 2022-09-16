package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.model.CreeperModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.mob.CreeperPatch;

@OnlyIn(Dist.CLIENT)
public class PCreeperRenderer extends PatchedLivingEntityRenderer<CreeperEntity, CreeperPatch, CreeperModel<CreeperEntity>> {
	@Override
	protected int getOverlayCoord(CreeperEntity entity, CreeperPatch entitypatch, float partialTicks) {
		float f = entity.getSwelling(partialTicks);
		float overlay = (int) (f * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(f, 0.5F, 1.0F);
		return OverlayTexture.pack(OverlayTexture.u(overlay), OverlayTexture.v(entity.hurtTime > 5 || entity.deathTime > 0));
	}
	
	@Override
	protected void setJointTransforms(CreeperPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(2, armature, entitypatch.getHeadMatrix(partialTicks));
	}
}