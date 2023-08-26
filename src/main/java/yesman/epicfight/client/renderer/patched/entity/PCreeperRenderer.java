package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.CreeperMesh;
import yesman.epicfight.world.capabilities.entitypatch.mob.CreeperPatch;

@OnlyIn(Dist.CLIENT)
public class PCreeperRenderer extends PatchedLivingEntityRenderer<Creeper, CreeperPatch, CreeperModel<Creeper>, CreeperMesh> {
	@Override
	protected int getOverlayCoord(Creeper entity, CreeperPatch entitypatch, float partialTicks) {
		float f = entity.getSwelling(partialTicks);
		float overlay = (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
		return OverlayTexture.pack(OverlayTexture.u(overlay), OverlayTexture.v(entity.hurtTime > 5 || entity.deathTime > 0));
	}
	
	@Override
	protected void setJointTransforms(CreeperPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform("Head", armature, entitypatch.getHeadMatrix(partialTicks));
	}
	
	@Override
	public CreeperMesh getMesh(CreeperPatch entitypatch) {
		return Meshes.CREEPER;
	}
}