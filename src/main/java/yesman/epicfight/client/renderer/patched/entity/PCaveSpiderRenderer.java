package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.client.renderer.patched.layer.PatchedEyeLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.CaveSpiderPatch;

@OnlyIn(Dist.CLIENT)
public class PCaveSpiderRenderer extends PatchedLivingEntityRenderer<CaveSpider, CaveSpiderPatch, SpiderModel<CaveSpider>> {
	private static final ResourceLocation CAVE_SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/cave_spider.png");
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	public PCaveSpiderRenderer() {
		this.layerRendererReplace.put(SpiderEyesLayer.class, new PatchedEyeLayer<>(SPIDER_EYE_TEXTURE, ClientModels.LOGICAL_CLIENT.spiderEye));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(CaveSpiderPatch entityIn) {
		return CAVE_SPIDER_TEXTURE;
	}
}