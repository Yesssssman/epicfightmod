package yesman.epicfight.skill;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
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
			return executer.getHoldingItemCapability(InteractionHand.MAIN_HAND).getSpecialAttack(executer) == this && executer.getOriginal().getVehicle() == null && (!executer.getSkill(this.category).isActivated() || this.activateType == ActivateType.TOGGLE);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = Lists.<Component>newArrayList();
		String traslatableText = this.getTranslatableText();
		
		list.add(new TranslatableComponent(traslatableText).withStyle(ChatFormatting.WHITE).append(new TextComponent(String.format("[%.0f]", this.consumption)).withStyle(ChatFormatting.AQUA)));
		list.add(new TranslatableComponent(traslatableText + ".tooltip").withStyle(ChatFormatting.DARK_GRAY));
		return list;
	}
	
	protected void generateTooltipforPhase(List<Component> list, ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch, Map<AttackPhaseProperty<?>, Object> propertyMap, String title) {
		Multimap<Attribute, AttributeModifier> attributes = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
		Multimap<Attribute, AttributeModifier> capAttributes = cap.getAttributeModifiers(EquipmentSlot.MAINHAND, playerpatch);
		double damage = playerpatch.getOriginal().getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + EnchantmentHelper.getDamageBonus(itemStack, MobType.UNDEFINED);
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
		
		damage = damageCorrector.getTotalValue(playerpatch.getModifiedDamage(null, null, (float)damage));
		armorNegation = armorNegationCorrector.getTotalValue((float)armorNegation);
		impact = impactCorrector.getTotalValue((float)impact);
		maxStrikes = maxStrikesCorrector.getTotalValue((float)maxStrikes);
		
		list.add(new TextComponent(title).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GRAY));
		
		MutableComponent damageComponent = new TranslatableComponent("skill.epicfight.damage",
				new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage)).withStyle(ChatFormatting.RED)
		).withStyle(ChatFormatting.DARK_GRAY);
		
		this.getProperty(AttackPhaseProperty.EXTRA_DAMAGE, propertyMap).ifPresent((extraDamage) -> {
			damageComponent.append(new TranslatableComponent(extraDamage.toString(),
					new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(extraDamage.getArgument() * 100F) + "%").withStyle(ChatFormatting.RED)))
				.withStyle(ChatFormatting.DARK_GRAY);
		});
		
		list.add(damageComponent);
		
		if (armorNegation != 0.0D) {
			list.add(new TranslatableComponent(EpicFightAttributes.ARMOR_NEGATION.get().getDescriptionId(),
					new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(armorNegation)).withStyle(ChatFormatting.GOLD)
			).withStyle(ChatFormatting.DARK_GRAY));
		}
		
		if (impact != 0.0D) {
			list.add(new TranslatableComponent(EpicFightAttributes.IMPACT.get().getDescriptionId(),
					new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(impact)).withStyle(ChatFormatting.AQUA)
			).withStyle(ChatFormatting.DARK_GRAY));
		}
		
		list.add(new TranslatableComponent(EpicFightAttributes.MAX_STRIKES.get().getDescriptionId(),
				new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(maxStrikes)).withStyle(ChatFormatting.WHITE)
		).withStyle(ChatFormatting.DARK_GRAY));
		
		Optional<StunType> stunOption = this.getProperty(AttackPhaseProperty.STUN_TYPE, propertyMap);
		
		stunOption.ifPresent((stunType) -> {
			list.add(new TextComponent(ChatFormatting.DARK_GRAY + "Apply " + stunType.toString()));
		});
		if (!stunOption.isPresent()) {
			list.add(new TextComponent(ChatFormatting.DARK_GRAY + "Apply " + StunType.SHORT.toString()));
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