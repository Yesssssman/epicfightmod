package maninhouse.epicfight.client.renderer.entity;

import maninhouse.epicfight.capabilities.entity.mob.DrownedData;
import maninhouse.epicfight.client.renderer.layer.OuterLayerRenderer;
import net.minecraft.entity.monster.DrownedEntity;

public class DrownedRenderer extends SimpleTextureBipedRenderer<DrownedEntity, DrownedData> {
	public DrownedRenderer() {
		super("textures/entity/zombie/drowned.png");
		this.layers.add(new OuterLayerRenderer());
	}
}