package maninthehouse.epicfight.capabilities.entity.mob;

import maninthehouse.epicfight.item.ModItems;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class StrayData extends SkeletonData<EntityStray> {
	@Override
	public void onEntityJoinWorld(EntityStray entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ModItems.STRAY_HAT));
		this.orgEntity.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ModItems.STRAY_ROBE));
		this.orgEntity.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ModItems.STRAY_PANTS));
	}
}