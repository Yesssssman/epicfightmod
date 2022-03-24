package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import yesman.epicfight.world.item.EpicFightItems;

public class StrayPatch extends SkeletonPatch<Stray> {
	@Override
	public void onJoinWorld(Stray entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		this.original.setItemSlot(EquipmentSlot.HEAD, new ItemStack(EpicFightItems.STRAY_HAT.get()));
		this.original.setItemSlot(EquipmentSlot.CHEST, new ItemStack(EpicFightItems.STRAY_ROBE.get()));
		this.original.setItemSlot(EquipmentSlot.LEGS, new ItemStack(EpicFightItems.STRAY_PANTS.get()));
	}
}