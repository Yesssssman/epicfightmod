package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.IronGolemMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedGolemCrackLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.IronGolemPatch;

@OnlyIn(Dist.CLIENT)
public class PIronGolemRenderer extends PatchedLivingEntityRenderer<IronGolem, IronGolemPatch, IronGolemModel<IronGolem>, IronGolemMesh> {
	public PIronGolemRenderer() {
		this.addPatchedLayer(IronGolemCrackinessLayer.class, new PatchedGolemCrackLayer(Meshes.IRON_GOLEM));
	}
	
	@Override
	protected void setJointTransforms(IronGolemPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform("Head", armature, entitypatch.getHeadMatrix(partialTicks));
	}
	
	@Override
	public IronGolemMesh getMesh(IronGolemPatch entitypatch) {
		return Meshes.IRON_GOLEM;
	}
}