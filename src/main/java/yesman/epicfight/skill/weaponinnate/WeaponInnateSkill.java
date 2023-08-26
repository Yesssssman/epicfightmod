package yesman.epicfight.skill.weaponinnate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public abstract class WeaponInnateSkill extends Skill {
	public static Skill.Builder<WeaponInnateSkill> createWeaponInnateBuilder() {
		return (new Skill.Builder<WeaponInnateSkill>()).setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.WEAPON_INNATE_ENERGY);
	}
	
	protected List<Map<AttackPhaseProperty<?>, Object>> properties;
	
	public WeaponInnateSkill(Builder<? extends Skill> builder) {
		super(builder);
		
		this.properties = Lists.newArrayList();
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		if (executer.isLogicalClient()) {
			return super.canExecute(executer);
		} else {
			ItemStack itemstack = executer.getOriginal().getMainHandItem();
			
			return super.canExecute(executer) && EpicFightCapabilities.getItemStackCapability(itemstack).getInnateSkill(executer, itemstack) == this
					&& executer.getOriginal().getVehicle() == null && (!executer.getSkill(this).isActivated() || this.activateType == ActivateType.TOGGLE);
		}
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemstack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = Lists.newArrayList();
		String traslatableText = this.getTranslationKey();
		
		list.add(new TranslatableComponent(traslatableText).withStyle(ChatFormatting.WHITE).append(new TextComponent(String.format("[%.0f]", this.consumption)).withStyle(ChatFormatting.AQUA)));
		list.add(new TranslatableComponent(traslatableText + ".tooltip").withStyle(ChatFormatting.DARK_GRAY));
		
		return list;
	}
	
	protected void generateTooltipforPhase(List<Component> list, ItemStack itemstack, CapabilityItem cap, PlayerPatch<?> playerpatch, Map<AttackPhaseProperty<?>, Object> propertyMap, String title) {
		Multimap<Attribute, AttributeModifier> attributes = itemstack.getAttributeModifiers(EquipmentSlot.MAINHAND);
		Multimap<Attribute, AttributeModifier> capAttributes = cap.getAttributeModifiers(EquipmentSlot.MAINHAND, playerpatch);
		double damage = playerpatch.getOriginal().getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + EnchantmentHelper.getDamageBonus(itemstack, MobType.UNDEFINED);
		double armorNegation = playerpatch.getOriginal().getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).getBaseValue();
		double impact = playerpatch.getOriginal().getAttribute(EpicFightAttributes.IMPACT.get()).getBaseValue();
		double maxStrikes = playerpatch.getOriginal().getAttribute(EpicFightAttributes.MAX_STRIKES.get()).getBaseValue();
		ValueModifier damageModifier = ValueModifier.empty();
		ValueModifier armorNegationModifier = ValueModifier.empty();
		ValueModifier impactModifier = ValueModifier.empty();
		ValueModifier maxStrikesModifier = ValueModifier.empty();
		
		Set<AttributeModifier> damageModifiers = Sets.newHashSet();
		damageModifiers.addAll(playerpatch.getOriginal().getAttribute(Attributes.ATTACK_DAMAGE).getModifiers());
		damageModifiers.addAll(attributes.get(Attributes.ATTACK_DAMAGE));
		
		for (AttributeModifier modifier : damageModifiers) {
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
		
		this.getProperty(AttackPhaseProperty.DAMAGE_MODIFIER, propertyMap).ifPresent(damageModifier::merge);
		this.getProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, propertyMap).ifPresent(armorNegationModifier::merge);
		this.getProperty(AttackPhaseProperty.IMPACT_MODIFIER, propertyMap).ifPresent(impactModifier::merge);
		this.getProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, propertyMap).ifPresent(maxStrikesModifier::merge);
		
		impactModifier.merge(ValueModifier.multiplier(1.0F + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, itemstack) * 0.12F));
		
		Double baseDamage = Double.valueOf(damage);
		damage = damageModifier.getTotalValue(playerpatch.getModifiedBaseDamage((float)damage));
		armorNegation = armorNegationModifier.getTotalValue((float)armorNegation);
		impact = impactModifier.getTotalValue((float)impact);
		maxStrikes = maxStrikesModifier.getTotalValue((float)maxStrikes);
		
		list.add(new TextComponent(title).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GRAY));
		
		MutableComponent damageComponent = new TranslatableComponent("damage_source.epicfight.damage",
					new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage)).withStyle(ChatFormatting.RED)
				).withStyle(ChatFormatting.DARK_GRAY);
		
		this.getProperty(AttackPhaseProperty.EXTRA_DAMAGE, propertyMap).ifPresent((extraDamageSet) -> {
			extraDamageSet.forEach((extraDamage) -> {
				extraDamage.setTooltips(itemstack, damageComponent, baseDamage);
			});
		});
		
		list.add(damageComponent);
		
		if (armorNegation != 0.0D) {
			list.add(new TranslatableComponent( EpicFightAttributes.ARMOR_NEGATION.get().getDescriptionId()
											  , new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(armorNegation) + "%"
											  ).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.DARK_GRAY));
		}
		
		if (impact != 0.0D) {
			list.add(new TranslatableComponent( EpicFightAttributes.IMPACT.get().getDescriptionId()
					                          , new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(impact)
					                          ).withStyle(ChatFormatting.AQUA)
			                                  ).withStyle(ChatFormatting.DARK_GRAY));
		}
		
		list.add(new TranslatableComponent(EpicFightAttributes.MAX_STRIKES.get().getDescriptionId(),
				new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(maxStrikes)).withStyle(ChatFormatting.WHITE)
		).withStyle(ChatFormatting.DARK_GRAY));
		
		Optional<StunType> stunOption = this.getProperty(AttackPhaseProperty.STUN_TYPE, propertyMap);
		
		stunOption.ifPresent((stunType) -> {
			list.add(new TranslatableComponent(stunType.toString()).withStyle(ChatFormatting.DARK_GRAY));
		});
		
		if (!stunOption.isPresent()) {
			list.add(new TranslatableComponent(StunType.SHORT.toString()).withStyle(ChatFormatting.DARK_GRAY));
		}	
	}
	
	@SuppressWarnings("unchecked")
	protected <V> Optional<V> getProperty(AttackPhaseProperty<V> propertyKey, Map<AttackPhaseProperty<?>, Object> map) {
		return (Optional<V>) Optional.ofNullable(map.get(propertyKey));
	}
	
	public WeaponInnateSkill newProperty() {
		this.properties.add(Maps.<AttackPhaseProperty<?>, Object>newHashMap());
		
		return this;
	}
	
	public <T> WeaponInnateSkill addProperty(AttackPhaseProperty<T> propertyKey, T object) {
		this.properties.get(properties.size() - 1).put(propertyKey, object);
		
		return this;
	}
	
	public abstract WeaponInnateSkill registerPropertiesToAnimation();
}