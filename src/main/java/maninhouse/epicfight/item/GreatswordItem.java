package maninhouse.epicfight.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldOption;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldStyle;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
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
	public void setWeaponCapability(IItemTier tier) {
    	ModWeaponCapability weaponCapability = new ModWeaponCapability(new ModWeaponCapability.Builder()
    		.setCategory(WeaponCategory.GREATSWORD)
    		.setStyleGetter((playerdata) -> HoldStyle.TWO_HAND)
    		.setSmashingSound(Sounds.WHOOSH_BIG)
    		.setHitSound(Sounds.BLADE_HIT)
    		.setWeaponCollider(Colliders.greatSword)
    		.setHoldOption(HoldOption.TWO_HANDED)
    		.addStyleCombo(HoldStyle.TWO_HAND, Animations.GREATSWORD_AUTO_1, Animations.GREATSWORD_AUTO_2, Animations.GREATSWORD_DASH, Animations.GREATSWORD_AIR_SLASH)
        	.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.GIANT_WHIRLWIND)
        	.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_GREATSWORD)
        	.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.WALK, Animations.BIPED_IDLE_GREATSWORD)
        	.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_IDLE_GREATSWORD)
        	.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FALL, Animations.BIPED_IDLE_GREATSWORD)
        	.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_GREATSWORD)
        	.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_IDLE_GREATSWORD)
        	.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_IDLE_GREATSWORD)
        	.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FLOAT, Animations.BIPED_IDLE_GREATSWORD)
        	.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.BLOCK, Animations.GREATSWORD_GUARD)
    	);
    	weaponCapability.addStyleAttributeSimple(HoldStyle.TWO_HAND, (tier.getHarvestLevel() >= 3) ? 10.0D * (tier.getHarvestLevel() - 2) : 0.0D, 2.8D + 0.4D * tier.getHarvestLevel(), 4);
    	this.capability = weaponCapability;
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