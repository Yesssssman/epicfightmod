package yesman.epicfight.capabilities.item;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.model.ClientModel;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;

public class ArmorCapability extends CapabilityItem {
	protected static final UUID[] ARMOR_MODIFIERS = new UUID[] {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
	protected final double weight;
	protected final double stunArmor;
	private final EquipmentSlotType equipmentSlot;
	
	public ArmorCapability(Item item) {
		super(item, WeaponCategory.NOT_WEAON);
		ArmorItem armorItem = (ArmorItem) item;
		IArmorMaterial armorMaterial = armorItem.getArmorMaterial();
		this.equipmentSlot = armorItem.getEquipmentSlot();
		this.weight = armorMaterial.getDamageReductionAmount(this.equipmentSlot) * 2.5F;
		this.stunArmor = armorMaterial.getDamageReductionAmount(this.equipmentSlot) * 0.375F;
	}
	
	public ArmorCapability(Item item, double customWeight, double customStunArmor) {
		super(item, WeaponCategory.NOT_WEAON);
		ArmorItem armorItem = (ArmorItem) item;
		this.equipmentSlot = armorItem.getEquipmentSlot();
		this.weight = customWeight;
		this.stunArmor = customStunArmor;
	}
	
	@Override
	public void modifyItemTooltip(List<ITextComponent> itemTooltip, LivingData<?> entitydata) {
		itemTooltip.add(1, new StringTextComponent(TextFormatting.BLUE + " +" + (int)this.weight + " ")
				.appendSibling(new TranslationTextComponent(EpicFightAttributes.WEIGHT.get().getAttributeName()).mergeStyle(TextFormatting.BLUE)));
		if(this.stunArmor > 0.0F) {
			itemTooltip.add(1, new StringTextComponent(TextFormatting.BLUE + " +" + ItemStack.DECIMALFORMAT.format(this.stunArmor) + " ")
				.appendSibling(new TranslationTextComponent(EpicFightAttributes.STUN_ARMOR.get().getAttributeName()).mergeStyle(TextFormatting.BLUE)));
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, LivingData<?> entitydata) {
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		
		if (entitydata != null && equipmentSlot == this.equipmentSlot) {
			map.put(EpicFightAttributes.WEIGHT.get(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.weight, Operation.ADDITION));
			map.put(EpicFightAttributes.STUN_ARMOR.get(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.stunArmor, Operation.ADDITION));
		}
		
        return map;
    }
	
	@OnlyIn(Dist.CLIENT)
	public ClientModel getArmorModel(EquipmentSlotType slot) {
		return getBipedArmorModel(slot);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static ClientModel getBipedArmorModel(EquipmentSlotType slot) {
		ClientModels modelDB = ClientModels.LOGICAL_CLIENT;
		
		switch (slot) {
		case HEAD:
			return modelDB.helmet;
		case CHEST:
			return modelDB.chestplate;
		case LEGS:
			return modelDB.leggins;
		case FEET:
			return modelDB.boots;
		default:
			return null;
		}
	}
}