package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.client.capabilites.entity.RemoteClientPlayerData;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerRenderer extends BipedRenderer<AbstractClientPlayer, RemoteClientPlayerData<AbstractClientPlayer>> {
	@Override
	protected ResourceLocation getEntityTexture(AbstractClientPlayer entityIn) {
		return entityIn.getLocationSkin();
	}
	
	@Override
	protected void renderLayer(RemoteClientPlayerData<AbstractClientPlayer> entitydata, AbstractClientPlayer entityIn, VisibleMatrix4f[] poses, float partialTicks) {
		if (!entityIn.isSpectator()) {
			super.renderLayer(entitydata, entityIn, poses, partialTicks);
		}
	}
}