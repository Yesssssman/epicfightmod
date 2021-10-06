package yesman.epicfight.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.model.DrownedModel;
import net.minecraft.entity.monster.DrownedEntity;
import yesman.epicfight.capabilities.entity.mob.DrownedData;
import yesman.epicfight.client.renderer.layer.OuterLayerRenderer;

public class DrownedRenderer extends SimpleTextureBipedRenderer<DrownedEntity, DrownedData, DrownedModel<DrownedEntity>> {
	public DrownedRenderer() {
		super("textures/entity/zombie/drowned.png");
		this.layerRendererReplace.put(DrownedOuterLayer.class, new OuterLayerRenderer());
	}
}