package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.layers.WitchHeldItemLayer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.WitchPatch;

@OnlyIn(Dist.CLIENT)
public class PWitchRenderer extends PatchedLivingEntityRenderer<WitchEntity, WitchPatch, WitchModel<WitchEntity>> {
	public PWitchRenderer() {
		this.addPatchedLayer(WitchHeldItemLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	protected void setJointTransforms(WitchPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(9, armature, entitypatch.getHeadMatrix(partialTicks));
	}
}