package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.layer.PatchedHeadLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.AbstractIllagerPatch;

@OnlyIn(Dist.CLIENT)
public class PAbstractIllagerRenderer<E extends AbstractIllager, T extends AbstractIllagerPatch<E>> extends PatchedLivingEntityRenderer<E, T, IllagerModel<E>> {
	public PAbstractIllagerRenderer() {
		this.layerRendererReplace.put(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
		this.layerRendererReplace.put(CustomHeadLayer.class, new PatchedHeadLayer<>());
	}
	
	@Override
	protected void setJointTransforms(T entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(9, armature, entitypatch.getHeadMatrix(partialTicks));
	}
}