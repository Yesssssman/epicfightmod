package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.RavagerModel;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.RavagerMesh;
import yesman.epicfight.world.capabilities.entitypatch.mob.RavagerPatch;

@OnlyIn(Dist.CLIENT)
public class PRavagerRenderer extends PatchedLivingEntityRenderer<Ravager, RavagerPatch, RavagerModel, RavagerMesh> {
	@Override
	protected void setJointTransforms(RavagerPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform("Head", armature, entitypatch.getHeadMatrix(partialTicks));
	}
	
	@Override
	public RavagerMesh getMesh(RavagerPatch entitypatch) {
		return Meshes.RAVAGER;
	}
}