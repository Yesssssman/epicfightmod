package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.layers.IronGolemCracksLayer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.layer.PatchedGolemCrackLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.IronGolemPatch;

@OnlyIn(Dist.CLIENT)
public class PIronGolemRenderer extends PatchedLivingEntityRenderer<IronGolemEntity, IronGolemPatch, IronGolemModel<IronGolemEntity>> {
	public PIronGolemRenderer() {
		this.addPatchedLayer(IronGolemCracksLayer.class, new PatchedGolemCrackLayer());
	}
	
	@Override
	protected void setJointTransforms(IronGolemPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(2, armature, entitypatch.getHeadMatrix(partialTicks));
	}
}