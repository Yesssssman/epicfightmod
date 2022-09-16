package yesman.epicfight.skill;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.ValueCorrector;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public abstract class SpecialAttackSkill extends Skill {
	public static Skill.Builder<? extends SpecialAttackSkill> createBuilder(ResourceLocation resourceLocation) {
		return (new Skill.Builder<SpecialAttackSkill>(resourceLocation)).setCategory(SkillCategories.WEAPON_SPECIAL_ATTACK).setResource(Resource.SPECIAL_GAUAGE);
	}
	
	protected List<Map<AttackPhaseProperty<?>, Object>> properties;
	
	public SpecialAttackSkill(Builder<? extends Skill> builder) {
		super(builder);
		this.properties = Lists.newArrayList();
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		if (executer.isLogicalClient()) {
			return executer.getSkill(this.getCategory()).isReady() || executer.getOriginal().isCreative();
		} else {
			return executer.getHoldingItemCapability(Hand.MAIN_HAND).getSpecialAttack(executer) == this && executer.getOriginal().getVehicle() == null && (!executer.getSkill(this.category).isActivated() || this.activateType == ActivateType.TOGGLE);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
		String traslatableText = this.getTranslatableText();
		
		list.add(new TranslationTextComponent(traslatableText).withStyle(TextFormatting.WHITE).append(new StringTextComponent(String.format("[%.0f]", this.consumption)).withStyle(TextFormatting.AQUA)));
		list.add(new TranslationTextComponent(traslatableText + ".tooltip").withStyle(TextFormatting.DARK_GRAY));
		return list;
	}
	
	protected void generateTooltipforPhase(List<ITextComponent> list, ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch, Map<AttackPhaseProperty<?>, Object> propertyMap, String title) {
		Multimap<Attribute, AttributeModifier> attributes = itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
		Multimap<Attribute, AttributeModifier> capAttributes = cap.getAttributeModifiers(EquipmentSlotType.MAINHAND, playerpatch);
		double damage = playerpatch.getOriginal().getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + EnchantmentHelper.getDamageBonus(itemStack, CreatureAttribute.UNDEFINED);
		double armorNegation = playerpatch.getOriginal().getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).getBaseValue();
		double impact = playerpatch.getOriginal().getAttribute(EpicFightAttributes.IMPACT.get()).getBaseValue();
		double maxStrikes = playerpatch.getOriginal().getAttribute(EpicFightAttributes.MAX_STRIKES.get()).getBaseValue();
		ValueCorrector damageCorrector = ValueCorrector.empty();
		ValueCorrector armorNegationCorrector = ValueCorrector.empty();
		ValueCorrector impactCorrector = ValueCorrector.empty();
		ValueCorrector maxStrikesCorrector = ValueCorrector.empty();
		
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
		
		impactCorrector.merge(ValueCorrector.multiplier(1.0F + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, itemStack) * 0.12F));
		
		damage = damageCorrector.getTotalValue(playerpatch.getDamageToEntity(null, null, (float)damage));
		armorNegation = armorNegationCorrector.getTotalValue((float)armorNegation);
		impact = impactCorrector.getTotalValue((float)impact);
		maxStrikes = maxStrikesCorrector.getTotalValue((float)maxStrikes);
		
		list.add(new StringTextComponent(title).withStyle(TextFormatting.UNDERLINE).withStyle(TextFormatting.GRAY));
		
		IFormattableTextComponent damageComponent = new TranslationTextComponent("skill.epicfight.damage",
				new StringTextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage)).withStyle(TextFormatting.RED)
		).withStyle(TextFormatting.DARK_GRAY);
		
		this.getProperty(AttackPhaseProperty.EXTRA_DAMAGE, propertyMap).ifPresent((extraDamage) -> {
			damageComponent.append(new TranslationTextComponent(extraDamage.toString(),
					new StringTextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(extraDamage.getArgument() * 100F) + "%").withStyle(TextFormatting.RED)))
				.withStyle(TextFormatting.DARK_GRAY);
		});
		
		list.add(damageComponent);
		
		if (armorNegation != 0.0D) {
			list.add(new TranslationTextComponent(EpicFightAttributes.ARMOR_NEGATION.get().getDescriptionId(),
					new StringTextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(armorNegation)).withStyle(TextFormatting.GOLD)
			).withStyle(TextFormatting.DARK_GRAY));
		}
		
		if (impact != 0.0D) {
			list.add(new TranslationTextComponent(EpicFightAttributes.IMPACT.get().getDescriptionId(),
					new StringTextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(impact)).withStyle(TextFormatting.AQUA)
			).withStyle(TextFormatting.DARK_GRAY));
		}
		
		list.add(new TranslationTextComponent(EpicFightAttributes.MAX_STRIKES.get().getDescriptionId(),
				new StringTextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(maxStrikes)).withStyle(TextFormatting.WHITE)
		).withStyle(TextFormatting.DARK_GRAY));
		
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