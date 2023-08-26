package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedHeadLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@OnlyIn(Dist.CLIENT)
public class PIllagerRenderer<E extends AbstractIllager, T extends MobPatch<E>> extends PatchedLivingEntityRenderer<E, T, IllagerModel<E>, HumanoidMesh> {
	public PIllagerRenderer() {
		this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
		this.addPatchedLayer(CustomHeadLayer.class, new PatchedHeadLayer<>());
	}
	
	@Override
	protected void setJointTransforms(T entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform("Head", armature, entitypatch.getHeadMatrix(partialTicks));
	}

	@Override
	public HumanoidMesh getMesh(T entitypatch) {
		return Meshes.ILLAGER;
	}
}