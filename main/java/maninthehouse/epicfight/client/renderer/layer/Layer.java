package maninthehouse.epicfight.client.renderer.layer;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class Layer<E extends EntityLivingBase, T extends LivingData<E>> {
	public abstract void renderLayer(T entitydata, E entityliving, VisibleMatrix4f[] poses, float partialTicks);
}