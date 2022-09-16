package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.client.renderer.patched.layer.PatchedEyeLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.SpiderPatch;

@OnlyIn(Dist.CLIENT)
public class PSpiderRenderer extends PatchedLivingEntityRenderer<SpiderEntity, SpiderPatch<SpiderEntity>, SpiderModel<SpiderEntity>> {
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	public PSpiderRenderer() {
		this.addPatchedLayer(SpiderEyesLayer.class, new PatchedEyeLayer<>(SPIDER_EYE_TEXTURE, ClientModels.LOGICAL_CLIENT.spiderEye));
	}
}