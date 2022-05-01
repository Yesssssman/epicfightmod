package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
import net.minecraft.world.entity.monster.Stray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.renderer.patched.layer.NoRenderingLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.SkeletonPatch;

@OnlyIn(Dist.CLIENT)
public class PStrayRenderer extends PHumanoidRenderer<Stray, SkeletonPatch<Stray>, SkeletonModel<Stray>> {
	public PStrayRenderer() {
		this.layerRendererReplace.put(StrayClothingLayer.class, new NoRenderingLayer<>());
	}
}