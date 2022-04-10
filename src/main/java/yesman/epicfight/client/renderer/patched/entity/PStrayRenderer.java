package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
import net.minecraft.world.entity.monster.Stray;
import yesman.epicfight.client.renderer.patched.layer.NoRenderingLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.SkeletonPatch;

public class PStrayRenderer extends SimpleTextureHumanoidRenderer<Stray, SkeletonPatch<Stray>, SkeletonModel<Stray>> {
	public PStrayRenderer() {
		super("textures/entity/skeleton/stray.png");
		this.layerRendererReplace.put(StrayClothingLayer.class, new NoRenderingLayer<>());
	}
}