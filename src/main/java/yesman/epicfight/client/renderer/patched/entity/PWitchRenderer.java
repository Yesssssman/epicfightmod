package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.world.entity.monster.Witch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.WitchPatch;

@OnlyIn(Dist.CLIENT)
public class PWitchRenderer extends PatchedLivingEntityRenderer<Witch, WitchPatch, WitchModel<Witch>> {
	public PWitchRenderer() {
		this.addPatchedLayer(WitchItemLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	protected void setJointTransforms(WitchPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(9, armature, entitypatch.getHeadMatrix(partialTicks));
	}
}