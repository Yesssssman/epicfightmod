package maninhouse.epicfight.client.renderer.entity;

import maninhouse.epicfight.client.capabilites.entity.RemoteClientPlayerData;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerRenderer extends BipedRenderer<AbstractClientPlayerEntity, RemoteClientPlayerData<AbstractClientPlayerEntity>> {
	@Override
	protected ResourceLocation getEntityTexture(AbstractClientPlayerEntity entityIn) {
		return entityIn.getLocationSkin();
	}
}