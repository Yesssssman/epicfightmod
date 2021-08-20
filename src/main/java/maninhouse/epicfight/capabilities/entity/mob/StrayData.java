package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.item.ModItems;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class StrayData extends SkeletonData<StrayEntity> {
	
	@Override
	public void onEntityJoinWorld(StrayEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		orgEntity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(ModItems.STRAY_HAT.get()));
		orgEntity.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(ModItems.STRAY_ROBE.get()));
		orgEntity.setItemStackToSlot(EquipmentSlotType.LEGS, new ItemStack(ModItems.STRAY_PANTS.get()));
	}
}