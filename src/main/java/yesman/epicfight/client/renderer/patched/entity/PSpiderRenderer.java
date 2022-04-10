package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.client.renderer.patched.layer.PatchedEyeLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.SpiderPatch;

@OnlyIn(Dist.CLIENT)
public class PSpiderRenderer extends PatchedLivingEntityRenderer<Spider, SpiderPatch<Spider>, SpiderModel<Spider>> {
	private static final ResourceLocation SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/spider.png");
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	private final ResourceLocation customTexture;
	
	public PSpiderRenderer() {
		this(null);
	}
	
	public PSpiderRenderer(ResourceLocation customTextureLocation) {
		super();
		this.customTexture = customTextureLocation;
		this.layerRendererReplace.put(SpiderEyesLayer.class, new PatchedEyeLayer<>(SPIDER_EYE_TEXTURE, ClientModels.LOGICAL_CLIENT.spiderEye));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(SpiderPatch<Spider> entitypatch) {
		if (this.customTexture != null) {
			return customTexture;
		} else {
			return SPIDER_TEXTURE;
		}
	}
}