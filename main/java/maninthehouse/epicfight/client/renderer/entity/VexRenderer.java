package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.capabilities.entity.mob.VexData;
import maninthehouse.epicfight.client.renderer.layer.HeldItemLayer;
import maninthehouse.epicfight.model.Armature;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VexRenderer extends ArmatureRenderer<EntityVex, VexData> {
	public static final ResourceLocation VEX_TEXTURE = new ResourceLocation("textures/entity/illager/vex.png");
	public static final ResourceLocation VEX_CHARGE_TEXTURE = new ResourceLocation("textures/entity/illager/vex_charging.png");
	
	public VexRenderer() {
		this.layers.add(new HeldItemLayer<>());
	}
	
	@Override
	protected void applyRotations(Armature armature, EntityVex entityIn, VexData entitydata, double x, double y, double z, float partialTicks) {
		super.applyRotations(armature, entityIn, entitydata, x, y, z, partialTicks);
		this.transformJoint(7, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityVex entityIn) {
		return entityIn.isCharging() ? VEX_CHARGE_TEXTURE : VEX_TEXTURE;
	}
}