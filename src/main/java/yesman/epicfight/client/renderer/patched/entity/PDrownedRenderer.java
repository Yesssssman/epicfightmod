package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.renderer.patched.layer.OuterLayerRenderer;
import yesman.epicfight.world.capabilities.entitypatch.mob.DrownedPatch;

@OnlyIn(Dist.CLIENT)
public class PDrownedRenderer extends PHumanoidRenderer<Drowned, DrownedPatch, DrownedModel<Drowned>> {
	public PDrownedRenderer() {
		this.layerRendererReplace.put(DrownedOuterLayer.class, new OuterLayerRenderer());
	}
}