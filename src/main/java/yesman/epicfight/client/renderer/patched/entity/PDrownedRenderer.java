package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.world.entity.monster.Drowned;
import yesman.epicfight.client.renderer.patched.layer.OuterLayerRenderer;
import yesman.epicfight.world.capabilities.entitypatch.mob.DrownedPatch;

public class PDrownedRenderer extends SimpleTextureHumanoidRenderer<Drowned, DrownedPatch, DrownedModel<Drowned>> {
	public PDrownedRenderer() {
		super("textures/entity/zombie/drowned.png");
		this.layerRendererReplace.put(DrownedOuterLayer.class, new OuterLayerRenderer());
	}
}