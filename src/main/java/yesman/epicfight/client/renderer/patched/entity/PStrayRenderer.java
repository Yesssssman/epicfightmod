package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.layers.StayClothingLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.CreatureEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.renderer.patched.layer.EmptyLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.SkeletonPatch;

@OnlyIn(Dist.CLIENT)
public class PStrayRenderer extends PHumanoidRenderer<CreatureEntity, SkeletonPatch<CreatureEntity>, BipedModel<CreatureEntity>> {
	public PStrayRenderer() {
		this.addPatchedLayer(StayClothingLayer.class, new EmptyLayer<>());
	}
}