package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.model.DrownedModel;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.renderer.patched.layer.OuterLayerRenderer;
import yesman.epicfight.world.capabilities.entitypatch.mob.DrownedPatch;

@OnlyIn(Dist.CLIENT)
public class PDrownedRenderer extends PHumanoidRenderer<DrownedEntity, DrownedPatch, DrownedModel<DrownedEntity>> {
	public PDrownedRenderer() {
		this.addPatchedLayer(DrownedOuterLayer.class, new OuterLayerRenderer());
	}
}