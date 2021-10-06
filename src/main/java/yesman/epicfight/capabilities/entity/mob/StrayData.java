package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import yesman.epicfight.item.EpicFightItems;

public class StrayData extends SkeletonData<StrayEntity> {
	
	@Override
	public void onEntityJoinWorld(StrayEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		orgEntity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(EpicFightItems.STRAY_HAT.get()));
		orgEntity.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(EpicFightItems.STRAY_ROBE.get()));
		orgEntity.setItemStackToSlot(EquipmentSlotType.LEGS, new ItemStack(EpicFightItems.STRAY_PANTS.get()));
	}
}