package yesman.epicfight.skill.weaponinnate;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class GuillotineAxeSkill extends SimpleWeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("b84e577a-c653-11ed-afa1-0242ac120002");
	
	public GuillotineAxeSkill(SimpleWeaponInnateSkill.Builder builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID, (event) -> {
			if (event.getDamageSource().getAnimation() == Animations.THE_GUILLOTINE) {
				ValueModifier damageModifier = ValueModifier.empty();
				this.getProperty(AttackPhaseProperty.DAMAGE_MODIFIER, this.properties.get(0)).ifPresent(damageModifier::merge);
				damageModifier.merge(ValueModifier.multiplier(0.8F));
				float health = event.getTarget().getHealth();
				float executionHealth = damageModifier.getTotalValue((float)event.getPlayerPatch().getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE));
				
				if (health < executionHealth) {
					if (event.getDamageSource() != null) {
						event.getDamageSource().addRuntimeTag(EpicFightDamageType.EXECUTION);
					}
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemstack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
		List<Component> list = Lists.newArrayList();
		List<Object> tooltipArgs = Lists.newArrayList();
		String traslatableText = this.getTranslationKey();
		Multimap<Attribute, AttributeModifier> attributes = itemstack.getAttributeModifiers(EquipmentSlot.MAINHAND);
		double damage = playerpatch.getOriginal().getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + EnchantmentHelper.getDamageBonus(itemstack, MobType.UNDEFINED);
		ValueModifier damageModifier = ValueModifier.empty();
		
		Set<AttributeModifier> damageModifiers = Sets.newHashSet();
		damageModifiers.addAll(playerpatch.getOriginal().getAttribute(Attributes.ATTACK_DAMAGE).getModifiers());
		damageModifiers.addAll(attributes.get(Attributes.ATTACK_DAMAGE));
		
		for (AttributeModifier modifier : damageModifiers) {
			damage += modifier.getAmount();
		}
		
		this.getProperty(AttackPhaseProperty.DAMAGE_MODIFIER, this.properties.get(0)).ifPresent(damageModifier::merge);
		damageModifier.merge(ValueModifier.multiplier(0.8F));
		tooltipArgs.add(ChatFormatting.RED + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damageModifier.getTotalValue((float)damage)));
		
		list.add(Component.translatable(traslatableText).withStyle(ChatFormatting.WHITE).append(Component.literal(String.format("[%.0f]", this.consumption)).withStyle(ChatFormatting.AQUA)));
		list.add(Component.translatable(traslatableText + ".tooltip", tooltipArgs.toArray(new Object[0])).withStyle(ChatFormatting.DARK_GRAY));
		
		this.generateTooltipforPhase(list, itemstack, cap, playerpatch, this.properties.get(0), "Each Strike:");
		
		return list;
	}
}