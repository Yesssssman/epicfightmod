package maninhouse.epicfight.client.renderer.entity;

import maninhouse.epicfight.capabilities.entity.mob.CaveSpiderData;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.client.renderer.layer.EyeLayer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CaveSpiderRenderer extends ArmatureRenderer<CaveSpiderEntity, CaveSpiderData, SpiderModel<CaveSpiderEntity>> {
	private static final ResourceLocation CAVE_SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/cave_spider.png");
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	public CaveSpiderRenderer() {
		super();
		this.layerRendererReplace.put(SpiderEyesLayer.class, new EyeLayer<>(SPIDER_EYE_TEXTURE, ClientModels.LOGICAL_CLIENT.ENTITY_SPIDER_FACE));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(CaveSpiderEntity entityIn) {
		return CAVE_SPIDER_TEXTURE;
	}
}