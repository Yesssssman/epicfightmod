package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.renderer.layer.HeldItemLayer;
import maninthehouse.epicfight.client.renderer.layer.WearableItemLayer;
import maninthehouse.epicfight.model.Armature;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class BipedRenderer<E extends EntityLivingBase, T extends LivingData<E>> extends ArmatureRenderer<E, T> {
	public BipedRenderer() {
		this.layers.add(new HeldItemLayer<>());
		this.layers.add(new WearableItemLayer<>(EntityEquipmentSlot.HEAD));
		this.layers.add(new WearableItemLayer<>(EntityEquipmentSlot.CHEST));
		this.layers.add(new WearableItemLayer<>(EntityEquipmentSlot.LEGS));
		this.layers.add(new WearableItemLayer<>(EntityEquipmentSlot.FEET));
	}
	
	@Override
	protected void applyRotations(Armature armature, E entityIn, T entitydata, double x, double y, double z, float partialTicks) {
		super.applyRotations(armature, entityIn, entitydata, x, y, z, partialTicks);
		if (entityIn.isSneaking()) {
			GlStateManager.translate(0.0F, 0.15F, 0.0F);
		}
		if (entityIn.isChild()) {
			this.transformJoint(9, armature, new VisibleMatrix4f().scale(new Vec3f(1.25F, 1.25F, 1.25F)));
		}
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
}