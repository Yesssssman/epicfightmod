package maninthehouse.epicfight.item;

import java.util.UUID;

import com.google.common.collect.Multimap;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.capabilities.item.CapabilityItem.HandProperty;
import maninthehouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninthehouse.epicfight.capabilities.item.CapabilityItem.WieldStyle;
import maninthehouse.epicfight.capabilities.item.ModWeaponCapability;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Colliders;
import maninthehouse.epicfight.gamedata.Skills;
import maninthehouse.epicfight.gamedata.Sounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GreatswordItem extends WeaponItem {
	protected static final UUID MOVEMENT_SPEED_MODIFIER = UUID.fromString("16295ED8-B092-4A75-9A94-BCD8D56668BB");
	
	public GreatswordItem() {
		super(ModMaterials.GREATSWORD, 13, -3.0F);
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return toRepair.getItem() == Item.getItemFromBlock(Blocks.IRON_BLOCK);
	}
	
	@Override
	public int getItemEnchantability() {
		return 5;
	}

	@Override
	public boolean canHarvestBlock(IBlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability() {
    	capability = new ModWeaponCapability(WeaponCategory.GREATSWORD, (playerdata)->WieldStyle.TWO_HAND, null, Sounds.WHOOSH_BIG, Sounds.BLADE_HIT,
    			Colliders.greatSword, HandProperty.TWO_HANDED);
    	capability.addStyleCombo(WieldStyle.TWO_HAND, Animations.GREATSWORD_AUTO_1, Animations.GREATSWORD_AUTO_2, Animations.GREATSWORD_DASH);
    	capability.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.GIANT_WHIRLWIND);
    	capability.addStyleAttributeSimple(WieldStyle.TWO_HAND, 0.0D, 4.3D, 4);
    	capability.addLivingMotionChanger(LivingMotion.IDLE, Animations.BIPED_IDLE_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.WALKING, Animations.BIPED_WALK_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.JUMPING, Animations.BIPED_JUMP_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.KNEELING, Animations.BIPED_KNEEL_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.SNEAKING, Animations.BIPED_SNEAK_MASSIVE_HELD);
    }
    
    @Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
		
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(),new AttributeModifier(MOVEMENT_SPEED_MODIFIER, "Weapon modifier", -0.02D, 0));
		}
		
		return multimap;
	}
}
