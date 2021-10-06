package yesman.epicfight.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GreatswordItem extends WeaponItem {
	protected static final UUID MOVEMENT_SPEED_MODIFIER = UUID.fromString("16295ED8-B092-4A75-9A94-BCD8D56668BB");
	
	private final float attackDamage;
	private final float attackSpeed;
	
	public GreatswordItem(Item.Properties build, IItemTier tier) {
		super(tier, 0, 0.0F, build);
		this.attackDamage = 11.0F + tier.getAttackDamage();
		this.attackSpeed = -2.85F - (0.05F * tier.getHarvestLevel());
	}
	
	@Override
	public int getItemEnchantability() {
		return 5;
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
    @Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		if (slot == EquipmentSlotType.MAINHAND) {
    		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, Operation.ADDITION));
    		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, Operation.ADDITION));
            builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_SPEED_MODIFIER, "Weapon modifier", -0.02D, Operation.ADDITION));
    	    return builder.build();
        }
        
        return super.getAttributeModifiers(slot, stack);
    }
}