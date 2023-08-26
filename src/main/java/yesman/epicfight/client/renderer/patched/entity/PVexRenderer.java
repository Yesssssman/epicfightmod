package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.VexModel;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.monster.Vex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.VexMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.VexPatch;

@OnlyIn(Dist.CLIENT)
public class PVexRenderer extends PatchedLivingEntityRenderer<Vex, VexPatch, VexModel, VexMesh> {
	public PVexRenderer() {
		this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	protected void setJointTransforms(VexPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform("Head", armature, entitypatch.getHeadMatrix(partialTicks));
	}
	
	@Override
	public VexMesh getMesh(VexPatch entitypatch) {
		return Meshes.VEX;
	}
}