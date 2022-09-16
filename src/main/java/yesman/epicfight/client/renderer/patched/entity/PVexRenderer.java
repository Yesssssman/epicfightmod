package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.VexModel;
import net.minecraft.entity.monster.VexEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.VexPatch;

@OnlyIn(Dist.CLIENT)
public class PVexRenderer extends PatchedLivingEntityRenderer<VexEntity, VexPatch, VexModel> {
	public PVexRenderer() {
		this.addPatchedLayer(HeldItemLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	protected void setJointTransforms(VexPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(7, armature, entitypatch.getHeadMatrix(partialTicks));
	}
}