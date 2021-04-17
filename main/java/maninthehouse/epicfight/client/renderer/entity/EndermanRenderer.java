package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.capabilities.entity.mob.EndermanData;
import maninthehouse.epicfight.client.renderer.layer.EyeLayer;
import maninthehouse.epicfight.client.renderer.layer.HeldItemLayer;
import maninthehouse.epicfight.model.Armature;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EndermanRenderer extends ArmatureRenderer<EntityEnderman, EndermanData> {
	private static final ResourceLocation ENDERMAN_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman.png");
	private static final ResourceLocation ENDERMAN_EYE_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	
	public EndermanRenderer() {
		this.layers.add(new EyeLayer<>(ENDERMAN_EYE_TEXTURE));
		this.layers.add(new HeldItemLayer<>());
	}
	
	@Override
	protected void applyRotations(Armature armature, EntityEnderman entityIn, EndermanData entitydata, double x, double y, double z, float partialTicks) {
		super.applyRotations(armature, entityIn, entitydata, x, y, z, partialTicks);
		this.transformJoint(15, armature, entitydata.getHeadMatrix(partialTicks));
		
		if (entitydata.isRaging()) {
			VisibleMatrix4f head = new VisibleMatrix4f();
			VisibleMatrix4f.translate(new Vec3f(0, 0.25F, 0), head, head);
			transformJoint(16, armature, head);
		}
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityEnderman entityIn) {
		return ENDERMAN_TEXTURE;
	}
}