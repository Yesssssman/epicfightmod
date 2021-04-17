package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.capabilities.entity.mob.IronGolemData;
import maninthehouse.epicfight.model.Armature;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IronGolemRenderer extends ArmatureRenderer<EntityIronGolem, IronGolemData> {
	private static final ResourceLocation IRON_GOLEM_TEXTURE = new ResourceLocation("textures/entity/iron_golem.png");
	
	@Override
	protected void applyRotations(Armature armature, EntityIronGolem entityIn, IronGolemData entitydata, double x, double y, double z, float partialTicks) {
        super.applyRotations(armature, entityIn, entitydata, x, y, z, partialTicks);
        transformJoint(2, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityIronGolem entityIn) {
		return IRON_GOLEM_TEXTURE;
	}
}