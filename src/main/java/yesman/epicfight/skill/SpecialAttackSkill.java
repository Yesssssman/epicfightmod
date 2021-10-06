package yesman.epicfight.skill;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.property.Property.AttackPhaseProperty;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSExecuteSkill;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;
import yesman.epicfight.utils.math.ValueCorrector;

public abstract class SpecialAttackSkill extends Skill {
	protected List<Map<AttackPhaseProperty<?>, Object>> properties;

	public SpecialAttackSkill(float consumption, String skillName) {
		this(consumption, 0, 1, ActivateType.ONE_SHOT, skillName);
	}
	
	public SpecialAttackSkill(float consumption, int duration, ActivateType activateType, String skillName) {
		this(consumption, duration, 1, activateType, skillName);
	}
	
	public SpecialAttackSkill(float consumption, int duration, int maxStack, ActivateType activateType, String skillName) {
		super(SkillCategory.WEAPON_SPECIAL_ATTACK, consumption, duration, maxStack, true, activateType, Resource.SPECIAL_GAUAGE, skillName);
		this.properties = Lists.<Map<AttackPhaseProperty<?>, Object>>newArrayList();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {
		if (this.canExecute(executer)) {
			ModNetworkManager.sendToServer(new CTSExecuteSkill(this.slot.getIndex(), true, args));
		}
	}
	
	@Override
	public boolean canExecute(PlayerData<?> executer) {
		if (executer.isRemote()) {
			return true;
		} else {
			return executer.getHeldItemCapability(Hand.MAIN_HAND).getSpecialAttack(executer) == this && executer.getOriginalEntity().getRidingEntity() == null && (!executer.getSkill(this.slot).isActivated() || this.activateType == ActivateType.TOGGLE);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
		list.add(new TranslationTextComponent("skill." + EpicFightMod.MODID + "." + this.getSkillName()).mergeStyle(TextFormatting.WHITE)
				.appendSibling(new StringTextComponent(String.format("[%.0f]", this.consumption)).mergeStyle(TextFormatting.AQUA)));
		list.add(new TranslationTextComponent("skill." + EpicFightMod.MODID + "." + this.getSkillName() + ".tooltip").mergeStyle(TextFormatting.DARK_GRAY));
		return list;
	}
	
	protected void generateTooltipforPhase(List<ITextComponent> list, ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerdata, Map<AttackPhaseProperty<?>, Object> propertyMap, String title) {
		Multimap<Attribute, AttributeModifier> attributes = itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
		Multimap<Attribute, AttributeModifier> capAttributes = cap.getAttributeModifiers(EquipmentSlotType.MAINHAND, playerdata);
		double damage = playerdata.getOriginalEntity().getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + EnchantmentHelper.getModifierForCreature(itemStack, CreatureAttribute.UNDEFINED);
		double armorNegation = playerdata.getOriginalEntity().getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).getBaseValue();
		double impact = playerdata.getOriginalEntity().getAttribute(EpicFightAttributes.IMPACT.get()).getBaseValue();
		double maxStrikes = playerdata.getOriginalEntity().getAttribute(EpicFightAttributes.MAX_STRIKES.get()).getBaseValue();
		ValueCorrector damageCorrector = ValueCorrector.base();
		ValueCorrector armorNegationCorrector = ValueCorrector.base();
		ValueCorrector impactCorrector = ValueCorrector.base();
		ValueCorrector maxStrikesCorrector = ValueCorrector.base();
		
		for (AttributeModifier modifier : attributes.get(Attributes.ATTACK_DAMAGE)) {
			damage += modifier.getAmount();
		}
		for (AttributeModifier modifier : capAttributes.get(EpicFightAttributes.ARMOR_NEGATION.get())) {
			armorNegation += modifier.getAmount();
		}
		for (AttributeModifier modifier : capAttributes.get(EpicFightAttributes.IMPACT.get())) {
			impact += modifier.getAmount();
		}
		for (AttributeModifier modifier : capAttributes.get(EpicFightAttributes.MAX_STRIKES.get())) {
			maxStrikes += modifier.getAmount();
		}
		
		this.getProperty(AttackPhaseProperty.DAMAGE, propertyMap).ifPresent(damageCorrector::merge);
		this.getProperty(AttackPhaseProperty.ARMOR_NEGATION, propertyMap).ifPresent(armorNegationCorrector::merge);
		this.getProperty(AttackPhaseProperty.IMPACT, propertyMap).ifPresent(impactCorrector::merge);
		this.getProperty(AttackPhaseProperty.MAX_STRIKES, propertyMap).ifPresent(maxStrikesCorrector::merge);
		
		damage = damageCorrector.get(playerdata.getDamageToEntity(null, null, (float)damage));
		armorNegation = armorNegationCorrector.get((float)armorNegation);
		impact = impactCorrector.get((float)impact);
		maxStrikes = maxStrikesCorrector.get((float)maxStrikes);
		
		list.add(new StringTextComponent(title).mergeStyle(TextFormatting.UNDERLINE).mergeStyle(TextFormatting.GRAY));
		
		IFormattableTextComponent damageComponent = new TranslationTextComponent("skill.epicfight.damage",
				new StringTextComponent(ItemStack.DECIMALFORMAT.format(damage)).mergeStyle(TextFormatting.RED)
		).mergeStyle(TextFormatting.DARK_GRAY);
		
		this.getProperty(AttackPhaseProperty.EXTRA_DAMAGE, propertyMap).ifPresent((extraDamage) -> {
			damageComponent.appendSibling(new TranslationTextComponent(extraDamage.toString(),
					new StringTextComponent(ItemStack.DECIMALFORMAT.format(extraDamage.getArgument() * 100F) + "%").mergeStyle(TextFormatting.RED)))
				.mergeStyle(TextFormatting.DARK_GRAY);
		});
		
		list.add(damageComponent);
		
		if (armorNegation != 0.0D) {
			list.add(new TranslationTextComponent(EpicFightAttributes.ARMOR_NEGATION.get().getAttributeName(),
					new StringTextComponent(ItemStack.DECIMALFORMAT.format(armorNegation)).mergeStyle(TextFormatting.GOLD)
			).mergeStyle(TextFormatting.DARK_GRAY));
		}
		
		if (impact != 0.0D) {
			list.add(new TranslationTextComponent(EpicFightAttributes.IMPACT.get().getAttributeName(),
					new StringTextComponent(ItemStack.DECIMALFORMAT.format(impact)).mergeStyle(TextFormatting.AQUA)
			).mergeStyle(TextFormatting.DARK_GRAY));
		}
		
		list.add(new TranslationTextComponent(EpicFightAttributes.MAX_STRIKES.get().getAttributeName(),
				new StringTextComponent(ItemStack.DECIMALFORMAT.format(maxStrikes)).mergeStyle(TextFormatting.WHITE)
		).mergeStyle(TextFormatting.DARK_GRAY));
		
		Optional<StunType> stunOption = this.getProperty(AttackPhaseProperty.STUN_TYPE, propertyMap);
		
		stunOption.ifPresent((stunType) -> {
			list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Apply " + stunType.toString()));
		});
		if (!stunOption.isPresent()) {
			list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Apply " + StunType.SHORT.toString()));
		}	
	}
	
	@SuppressWarnings("unchecked")
	protected <V> Optional<V> getProperty(AttackPhaseProperty<V> propertyType, Map<AttackPhaseProperty<?>, Object> map) {
		return (Optional<V>) Optional.ofNullable(map.get(propertyType));
	}
	
	public SpecialAttackSkill newPropertyLine() {
		this.properties.add(Maps.<AttackPhaseProperty<?>, Object>newHashMap());
		return this;
	}
	
	public <T> SpecialAttackSkill addProperty(AttackPhaseProperty<T> attribute, T object) {
		this.properties.get(properties.size()-1).put(attribute, object);
		return this;
	}
	
	public abstract SpecialAttackSkill registerPropertiesToAnimation();
}