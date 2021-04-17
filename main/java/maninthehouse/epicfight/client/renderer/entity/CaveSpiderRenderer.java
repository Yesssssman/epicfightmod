package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.capabilities.entity.mob.CaveSpiderData;
import maninthehouse.epicfight.client.renderer.layer.EyeLayer;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveSpiderRenderer extends ArmatureRenderer<EntityCaveSpider, CaveSpiderData> {
	private static final ResourceLocation CAVE_SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/cave_spider.png");
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	public CaveSpiderRenderer() {
		this.layers.add(new EyeLayer<>(SPIDER_EYE_TEXTURE));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityCaveSpider entityIn) {
		return CAVE_SPIDER_TEXTURE;
	}
}