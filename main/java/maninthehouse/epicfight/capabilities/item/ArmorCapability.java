package maninthehouse.epicfight.capabilities.item;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.model.ClientModel;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ArmorCapability extends CapabilityItem {
	protected static final UUID[] ARMOR_MODIFIERS = new UUID[] {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
	protected double weight;
	protected double stunArmor;
	private final EntityEquipmentSlot equipmentSlot;
	
	public ArmorCapability(Item item) {
		super(item, WeaponCategory.NONE_WEAON);
		ItemArmor armorItem = (ItemArmor) item;
		ArmorMaterial armorMaterial = armorItem.getArmorMaterial();
		this.equipmentSlot = armorItem.armorType;
		this.weight = armorMaterial.getDamageReductionAmount(this.equipmentSlot) * 2.5F;
		this.stunArmor = armorMaterial.getDamageReductionAmount(this.equipmentSlot) * 0.375F;
	}
	
	public ArmorCapability(Item item, double customWeight, double customStunArmor) {
		super(item, WeaponCategory.NONE_WEAON);
		ItemArmor armorItem = (ItemArmor) item;
		this.equipmentSlot = armorItem.armorType;
		this.weight = customWeight;
		this.stunArmor = customStunArmor;
	}
	
	@Override
	public void modifyItemTooltip(List<String> itemTooltip, LivingData<?> entitydata) {
		itemTooltip.add(1, new TextComponentString(TextFormatting.BLUE + " +" + (int)this.weight + " ")
				.appendSibling(new TextComponentTranslation("attribute.name." + EpicFightMod.MODID +"."+ModAttributes.WEIGHT.getName()))
				.setStyle(new Style().setColor(TextFormatting.BLUE)).getFormattedText());
		if(this.stunArmor > 0.0F) {
			itemTooltip.add(1, new TextComponentString(TextFormatting.BLUE + " +" + ItemStack.DECIMALFORMAT.format(this.stunArmor) + " ")
				.appendSibling(new TextComponentTranslation("attribute.name." + EpicFightMod.MODID +"."+ModAttributes.MAX_STUN_ARMOR.getName()))
				.setStyle(new Style().setColor(TextFormatting.BLUE)).getFormattedText());
		}
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, LivingData<?> entitydata) {
		Multimap<String, AttributeModifier> map = HashMultimap.<String, AttributeModifier>create();
		
		if (entitydata != null && equipmentSlot == this.equipmentSlot) {
			map.put(ModAttributes.WEIGHT.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.weight, 0));
			map.put(ModAttributes.MAX_STUN_ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.stunArmor, 0));
		}
		
        return map;
    }
	
	@SideOnly(Side.CLIENT)
	public ClientModel getArmorModel(EntityEquipmentSlot slot) {
		return getBipedArmorModel(slot);
	}
	
	@SideOnly(Side.CLIENT)
	public static ClientModel getBipedArmorModel(EntityEquipmentSlot slot) {
		ClientModels modelDB = ClientModels.LOGICAL_CLIENT;
		
		switch (slot) {
		case HEAD:
			return modelDB.ITEM_HELMET;
		case CHEST:
			return modelDB.ITEM_CHESTPLATE;
		case LEGS:
			return modelDB.ITEM_LEGGINS;
		case FEET:
			return modelDB.ITEM_BOOTS;
		default:
			return null;
		}
	}
}
