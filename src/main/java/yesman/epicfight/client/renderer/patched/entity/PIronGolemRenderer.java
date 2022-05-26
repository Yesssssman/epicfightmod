package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.layer.PatchedGolemCrackLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.IronGolemPatch;

@OnlyIn(Dist.CLIENT)
public class PIronGolemRenderer extends PatchedLivingEntityRenderer<IronGolem, IronGolemPatch, IronGolemModel<IronGolem>> {
	public PIronGolemRenderer() {
		this.addPatchedLayer(IronGolemCrackinessLayer.class, new PatchedGolemCrackLayer());
	}
	
	@Override
	protected void setJointTransforms(IronGolemPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(2, armature, entitypatch.getHeadMatrix(partialTicks));
	}
}