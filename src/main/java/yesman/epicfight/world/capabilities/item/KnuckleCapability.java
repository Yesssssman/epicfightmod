package yesman.epicfight.world.capabilities.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class KnuckleCapability extends WeaponCapability {
	
	protected KnuckleCapability(CapabilityItem.Builder builder) {
		super(builder);
	}
	
	@Override
	public boolean checkOffhandValid(LivingEntityPatch<?> entitypatch) {
		ItemStack offhandItme = entitypatch.getOriginal().getOffhandItem();
		CapabilityItem itemCap = EpicFightCapabilities.getItemStackCapability(offhandItme);
		boolean isFist = itemCap.getWeaponCategory() == WeaponCategories.FIST;
		return isFist || !(offhandItme.getItem() instanceof SwordItem || offhandItme.getItem() instanceof ToolItem);
	}
	
	@Override
	public boolean canHoldInOffhandAlone() {
		return true;
	}
}