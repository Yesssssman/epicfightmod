package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import yesman.epicfight.world.item.EpicFightItems;

public class StrayPatch<T extends AbstractSkeletonEntity> extends SkeletonPatch<T> {
	@Override
	public void onJoinWorld(T entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		this.original.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(EpicFightItems.STRAY_HAT.get()));
		this.original.setItemSlot(EquipmentSlotType.CHEST, new ItemStack(EpicFightItems.STRAY_ROBE.get()));
		this.original.setItemSlot(EquipmentSlotType.LEGS, new ItemStack(EpicFightItems.STRAY_PANTS.get()));
	}
}