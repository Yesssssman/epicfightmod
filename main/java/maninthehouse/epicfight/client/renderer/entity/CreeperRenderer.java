package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.capabilities.entity.mob.CreeperData;
import maninthehouse.epicfight.model.Armature;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CreeperRenderer extends ArmatureRenderer<EntityCreeper, CreeperData> {
	public static final ResourceLocation CREEPER_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper.png");
	
	@Override
	protected ResourceLocation getEntityTexture(EntityCreeper entityIn) {
		return CREEPER_TEXTURE;
	}
	
	@Override
	protected void applyRotations(Armature armature, EntityCreeper entityIn, CreeperData entitydata, double x, double y, double z, float partialTicks) {
		super.applyRotations(armature, entityIn, entitydata, x, y, z, partialTicks);
		this.transformJoint(2, armature, entitydata.getHeadMatrix(partialTicks));
	}
}