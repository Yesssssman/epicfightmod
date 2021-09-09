package maninhouse.epicfight.client.renderer.entity;

import maninhouse.epicfight.capabilities.entity.mob.DrownedData;
import maninhouse.epicfight.client.renderer.layer.OuterLayerRenderer;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.model.DrownedModel;
import net.minecraft.entity.monster.DrownedEntity;

public class DrownedRenderer extends SimpleTextureBipedRenderer<DrownedEntity, DrownedData, DrownedModel<DrownedEntity>> {
	public DrownedRenderer() {
		super("textures/entity/zombie/drowned.png");
		this.layerRendererReplace.put(DrownedOuterLayer.class, new OuterLayerRenderer());
	}
}