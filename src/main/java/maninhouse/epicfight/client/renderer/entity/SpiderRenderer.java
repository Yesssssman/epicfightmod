package maninhouse.epicfight.client.renderer.entity;

import maninhouse.epicfight.capabilities.entity.mob.SpiderData;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.client.renderer.layer.EyeLayer;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpiderRenderer extends ArmatureRenderer<SpiderEntity, SpiderData<SpiderEntity>> {
	private static final ResourceLocation SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/spider.png");
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	private final ResourceLocation customTexture;
	
	public SpiderRenderer() {
		this(null);
	}
	
	public SpiderRenderer(ResourceLocation customTextureLocation) {
		super();
		this.customTexture = customTextureLocation;
		this.layers.add(new EyeLayer<>(SPIDER_EYE_TEXTURE, ClientModels.LOGICAL_CLIENT.ENTITY_SPIDER_FACE));
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