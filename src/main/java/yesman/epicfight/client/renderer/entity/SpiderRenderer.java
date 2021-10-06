package yesman.epicfight.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.mob.SpiderData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.client.renderer.layer.EyeLayer;

@OnlyIn(Dist.CLIENT)
public class SpiderRenderer extends ArmatureRenderer<SpiderEntity, SpiderData<SpiderEntity>, SpiderModel<SpiderEntity>> {
	private static final ResourceLocation SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/spider.png");
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	private final ResourceLocation customTexture;
	
	public SpiderRenderer() {
		this(null);
	}
	
	public SpiderRenderer(ResourceLocation customTextureLocation) {
		super();
		this.customTexture = customTextureLocation;
		this.layerRendererReplace.put(SpiderEyesLayer.class, new EyeLayer<>(SPIDER_EYE_TEXTURE, ClientModels.LOGICAL_CLIENT.spiderEye));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(SpiderEntity entityIn) {
		if (this.customTexture != null) {
			return customTexture;
		} else {
			return SPIDER_TEXTURE;
		}
	}
}