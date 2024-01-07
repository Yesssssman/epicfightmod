package yesman.epicfight.data.conditions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

public class OffhandCategoryCondition extends LivingEntityCondition {
	private WeaponCategory category;
	
	public OffhandCategoryCondition(CompoundTag tag) {
		super(tag);
	}

	@Override
	public void read(CompoundTag tag) {
		this.category = WeaponCategory.ENUM_MANAGER.get(tag.getString("category"));
		
		if (this.category == null) {
			throw new IllegalArgumentException("Weapon category '" + this.category + "' does not exist!");
		}
	}

	@Override
	public CompoundTag serializePredicate() {
		CompoundTag tag = new CompoundTag();
		
		tag.putString("category", this.category.toString());
		
		return tag;
	}

	@Override
	public boolean predicate(LivingEntityPatch<?> target) {
		return target.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == this.category;
	}
}