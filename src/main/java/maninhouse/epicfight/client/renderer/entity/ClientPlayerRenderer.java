package maninhouse.epicfight.client.renderer.entity;

import maninhouse.epicfight.client.capabilites.entity.RemoteClientPlayerData;
import maninhouse.epicfight.client.renderer.layer.CapeAnimatedLayer;
import maninhouse.epicfight.client.renderer.layer.NoRenderingLayer;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerRenderer extends BipedRenderer<AbstractClientPlayerEntity, RemoteClientPlayerData<AbstractClientPlayerEntity>, PlayerModel<AbstractClientPlayerEntity>> {
	public ClientPlayerRenderer() {
		this.layerRendererReplace.put(ArrowLayer.class, new NoRenderingLayer<>());
		this.layerRendererReplace.put(BeeStingerLayer.class, new NoRenderingLayer<>());
		this.layerRendererReplace.put(CapeLayer.class, new CapeAnimatedLayer());
	}
	
	@Override
	protected ResourceLocation getEntityTexture(AbstractClientPlayerEntity entityIn) {
		return entityIn.getLocationSkin();
	}
}