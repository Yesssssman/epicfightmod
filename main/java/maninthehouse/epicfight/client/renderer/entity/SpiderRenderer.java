package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.capabilities.entity.mob.SpiderData;
import maninthehouse.epicfight.client.renderer.layer.EyeLayer;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SpiderRenderer extends ArmatureRenderer<EntitySpider, SpiderData<EntitySpider>> {
	private static final ResourceLocation SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/spider.png");
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	public SpiderRenderer() {
		this.layers.add(new EyeLayer<>(SPIDER_EYE_TEXTURE));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntitySpider entityIn) {
		return SPIDER_TEXTURE;
	}
}