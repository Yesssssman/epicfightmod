package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimpleTexturBipedRenderer<E extends EntityLivingBase, T extends LivingData<E>> extends BipedRenderer<E, T> {
	public final ResourceLocation textureLocation;
	
	public SimpleTexturBipedRenderer(String texturePath) {
		textureLocation = new ResourceLocation(texturePath);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(E entityIn) {
		return textureLocation;
	}
}
