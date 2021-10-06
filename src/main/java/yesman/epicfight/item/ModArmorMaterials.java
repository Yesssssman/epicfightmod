package yesman.epicfight.item;

import java.util.function.Supplier;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import yesman.epicfight.main.EpicFightMod;

public enum ModArmorMaterials implements IArmorMaterial {
	STRAY_CLOTH("stray_cloth", 4, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
	      return Ingredient.fromItems(Items.STRING);
	   });
	
	private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
	private final String name;
	private final int enchantability;
	private final int maxDamageFactor;
	private final int[] damageReductionAmountArray;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final float knockbackResistance;
	private final LazyValue<Ingredient> repairMaterial;

	private ModArmorMaterials(String nameIn, int maxDamageFactorIn, int[] damageReductionAmountsIn, int enchantabilityIn,
			SoundEvent equipSoundIn, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterialSupplier) {
		this.name = nameIn;
	    this.maxDamageFactor = maxDamageFactorIn;
	    this.damageReductionAmountArray = damageReductionAmountsIn;
	    this.enchantability = enchantabilityIn;
	    this.soundEvent = equipSoundIn;
	    this.toughness = toughness;
	    this.knockbackResistance = knockbackResistance;
	    this.repairMaterial = new LazyValue<>(repairMaterialSupplier);
	}
	
	@Override
	public int getDurability(EquipmentSlotType slotIn)
	{
		return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
	}

	@Override
	public int getDamageReductionAmount(EquipmentSlotType slotIn)
	{
		return this.damageReductionAmountArray[slotIn.getIndex()];
	}

	@Override
	public int getEnchantability()
	{
		return this.enchantability;
	}

	@Override
	public SoundEvent getSoundEvent()
	{
		return soundEvent;
	}

	@Override
	public Ingredient getRepairMaterial()
	{
		return this.repairMaterial.getValue();
	}

	@Override
	public String getName()
	{
		return EpicFightMod.MODID + ":" + this.name;
	}

	@Override
	public float getToughness()
	{
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance()
	{
		return knockbackResistance;
	}
}