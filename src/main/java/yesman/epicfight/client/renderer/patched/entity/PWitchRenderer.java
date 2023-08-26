package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.world.entity.monster.Witch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.WitchPatch;

@OnlyIn(Dist.CLIENT)
public class PWitchRenderer extends PatchedLivingEntityRenderer<Witch, WitchPatch, WitchModel<Witch>, HumanoidMesh> {
	public PWitchRenderer() {
		this.addPatchedLayer(WitchItemLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	protected void setJointTransforms(WitchPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform("Head", armature, entitypatch.getHeadMatrix(partialTicks));
	}
	
	@Override
	public HumanoidMesh getMesh(WitchPatch entitypatch) {
		return Meshes.WITCH;
	}
}