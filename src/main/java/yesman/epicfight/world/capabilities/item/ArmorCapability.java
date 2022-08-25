package yesman.epicfight.world.capabilities.item;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ArmorCapability extends CapabilityItem {
	protected static final UUID[] ARMOR_MODIFIERS = new UUID[] {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
	protected final double weight;
	protected final double stunArmor;
	private final EquipmentSlot equipmentSlot;
	
	protected ArmorCapability(CapabilityItem.Builder builder) {
		super(builder);
		
		ArmorCapability.Builder armorBuilder = (ArmorCapability.Builder)builder;
		
		this.equipmentSlot = armorBuilder.equipmentSlot;
		this.weight = armorBuilder.weight;
		this.stunArmor = armorBuilder.stunArmor;
	}
	
	@Override
	public void modifyItemTooltip(ItemStack stack, List<Component> itemTooltip, LivingEntityPatch<?> entitypatch) {
		itemTooltip.add(1, new TextComponent(ChatFormatting.BLUE + " +" + (int)this.weight + " ").append(new TranslatableComponent(EpicFightAttributes.WEIGHT.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)));
		
		if (this.stunArmor > 0.0F) {
			itemTooltip.add(1, new TextComponent(ChatFormatting.BLUE + " +" + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.stunArmor) + " ").append(new TranslatableComponent(EpicFightAttributes.STUN_ARMOR.get().getDescriptionId()).withStyle(ChatFormatting.BLUE)));
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, LivingEntityPatch<?> entitypatch) {
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		
		if (entitypatch != null && equipmentSlot == this.equipmentSlot) {
			map.put(EpicFightAttributes.WEIGHT.get(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.weight, Operation.ADDITION));
			map.put(EpicFightAttributes.STUN_ARMOR.get(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.stunArmor, Operation.ADDITION));
		}
		
        return map;
    }
	
	public static ArmorCapability.Builder builder() {
		return new ArmorCapability.Builder();
	}
	
	public static class Builder extends CapabilityItem.Builder {
		EquipmentSlot equipmentSlot;
		double weight;
		double stunArmor;
		
		protected Builder() {
			this.constructor = ArmorCapability::new;
			this.weight = -1.0D;
			this.stunArmor = -1.0D;
		}
		
		public Builder item(Item item) {
			if (item instanceof ArmorItem) {
				ArmorItem armorItem = (ArmorItem) item;
				ArmorMaterial armorMaterial = armorItem.getMaterial();
				
				this.equipmentSlot = armorItem.getSlot();
				
				if (this.weight < 0.0D) {
					this.weight = armorMaterial.getDefenseForSlot(this.equipmentSlot) * 2.5F;
				}
				
				if (this.stunArmor < 0.0D) {
					this.stunArmor = armorMaterial.getDefenseForSlot(this.equipmentSlot) * 0.375F;
				}
			}
			
			return this;
		}
		
		public Builder weight(double weight) {
			this.weight = weight;
			return this;
		}
		
		public Builder stunArmor(double stunArmor) {
			this.stunArmor = stunArmor;
			return this;
		}
	}
}