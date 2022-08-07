package yesman.epicfight.world.capabilities.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class CapabilityItem {
	public static CapabilityItem EMPTY = CapabilityItem.builder().build();
	protected static List<StaticAnimation> commonAutoAttackMotion;
	protected final WeaponCategory weaponCategory;
	
	static {
		commonAutoAttackMotion = new ArrayList<StaticAnimation> ();
		commonAutoAttackMotion.add(Animations.FIST_AUTO1);
		commonAutoAttackMotion.add(Animations.FIST_AUTO2);
		commonAutoAttackMotion.add(Animations.FIST_AUTO3);
		commonAutoAttackMotion.add(Animations.FIST_DASH);
		commonAutoAttackMotion.add(Animations.FIST_AIR_SLASH);
	}
	
	public static List<StaticAnimation> getBasicAutoAttackMotion() {
		return commonAutoAttackMotion;
	}
	
	protected Map<Style, Map<Attribute, AttributeModifier>> attributeMap;
	
	protected CapabilityItem(CapabilityItem.Builder builder) {
		this.weaponCategory = builder.category;
		this.attributeMap = builder.attributeMap;
	}
	
	public void modifyItemTooltip(ItemStack itemstack, List<Component> itemTooltip, LivingEntityPatch<?> entitypatch) {
		if (!this.getStyle(entitypatch).canUseOffhand()) {
			itemTooltip.add(1, new TextComponent(" ").append(new TranslatableComponent("attribute.name." + EpicFightMod.MODID + ".twohanded").withStyle(ChatFormatting.DARK_GRAY)));
		}
		
		Map<Attribute, AttributeModifier> attribute = this.getDamageAttributesInCondition(this.getStyle(entitypatch));
		
		if (attribute != null) {
			Attribute armorNegation = EpicFightAttributes.ARMOR_NEGATION.get();
			Attribute impact = EpicFightAttributes.IMPACT.get();
			Attribute maxStrikes = EpicFightAttributes.MAX_STRIKES.get();
			
			if (attribute.containsKey(armorNegation)) {
				double value = attribute.get(armorNegation).getAmount() + entitypatch.getOriginal().getAttribute(armorNegation).getBaseValue();
				if (value > 0.0D) {
					itemTooltip.add(new TextComponent(" ").append(new TranslatableComponent(armorNegation.getDescriptionId(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value))));
				}
			}
			
			if (attribute.containsKey(impact)) {
				double value = attribute.get(impact).getAmount() + entitypatch.getOriginal().getAttribute(impact).getBaseValue();
				if (value > 0.0D) {
					int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, itemstack);
					value *= (1.0F + i * 0.12F);
					itemTooltip.add(new TextComponent(" ").append(new TranslatableComponent(impact.getDescriptionId(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value))));
				}
			}
			
			if (attribute.containsKey(maxStrikes)) {
				double value = attribute.get(maxStrikes).getAmount() + entitypatch.getOriginal().getAttribute(maxStrikes).getBaseValue();
				if (value > 0.0D) {
					itemTooltip.add(new TextComponent(" ").append(new TranslatableComponent(maxStrikes.getDescriptionId(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value))));
				}
			} else {
				itemTooltip.add(new TextComponent(" ").append(new TranslatableComponent(maxStrikes.getDescriptionId(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(maxStrikes.getDefaultValue()))));
			}
		}
	}
	
	public List<StaticAnimation> getAutoAttckMotion(PlayerPatch<?> playerpatch) {
		return getBasicAutoAttackMotion();
	}

	public List<StaticAnimation> getMountAttackMotion() {
		return null;
	}
	
	@Nullable
	public Skill getSpecialAttack(PlayerPatch<?> playerpatch) {
		return null;
	}
	
	@Nullable
	public Skill getPassiveSkill() {
		return null;
	}
	
	public WeaponCategory getWeaponCategory() {
		return this.weaponCategory;
	}
	
	public void changeWeaponSpecialSkill(PlayerPatch<?> playerpatch) {
		Skill specialSkill = this.getSpecialAttack(playerpatch);
		String skillName = "";
		SPChangeSkill.State state = SPChangeSkill.State.ENABLE;
		SkillContainer specialSkillContainer = playerpatch.getSkill(SkillCategories.WEAPON_SPECIAL_ATTACK);
		
		if (specialSkill != null) {
			if (specialSkillContainer.getSkill() != specialSkill) {
				specialSkillContainer.setSkill(specialSkill);
			}
			
			skillName = specialSkill.toString();
		} else {
			state = SPChangeSkill.State.DISABLE;
		}
		
		specialSkillContainer.setDisabled(specialSkill == null);
		EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(SkillCategories.WEAPON_SPECIAL_ATTACK.universalOrdinal(), skillName, state), (ServerPlayer)playerpatch.getOriginal());
		
		Skill skill = this.getPassiveSkill();
		SkillContainer passiveSkillContainer = playerpatch.getSkill(SkillCategories.WEAPON_PASSIVE);
		
		if (skill != null) {
			if (passiveSkillContainer.getSkill() != skill) {
				passiveSkillContainer.setSkill(skill);
				EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(skill.getCategory().universalOrdinal(), skill.toString(), SPChangeSkill.State.ENABLE), (ServerPlayer)playerpatch.getOriginal());
			}
		} else {
			passiveSkillContainer.setSkill(null);
			EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(SkillCategories.WEAPON_PASSIVE.universalOrdinal(), "empty", SPChangeSkill.State.ENABLE), (ServerPlayer)playerpatch.getOriginal());
		}
	}
	
	public SoundEvent getSmashingSound() {
		return EpicFightSounds.WHOOSH;
	}

	public SoundEvent getHitSound() {
		return EpicFightSounds.BLUNT_HIT;
	}

	public Collider getWeaponCollider() {
		return ColliderPreset.FIST;
	}

	public HitParticleType getHitParticle() {
		return EpicFightParticles.HIT_BLUNT.get();
	}
	
	public void addStyleAttibutes(Style style, Pair<Attribute, AttributeModifier> attributePair) {
		Map<Attribute, AttributeModifier> map = this.attributeMap.computeIfAbsent(style, (key) -> Maps.<Attribute, AttributeModifier>newHashMap());
		map.put(attributePair.getFirst(), attributePair.getSecond());
	}
	
	public void addStyleAttributes(Style style, double armorNegation, double impact, int maxStrikes) {
		if (Double.compare(armorNegation, 0.0D) != 0) {
			this.addStyleAttibutes(style, Pair.of(EpicFightAttributes.ARMOR_NEGATION.get(), EpicFightAttributes.getArmorNegationModifier(armorNegation)));
		}
		if (Double.compare(impact, 0.0D) != 0) {
			this.addStyleAttibutes(style, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(impact)));
		}
		if (Double.compare(maxStrikes, 0.0D) != 0) {
			this.addStyleAttibutes(style, Pair.of(EpicFightAttributes.MAX_STRIKES.get(), EpicFightAttributes.getMaxStrikesModifier(maxStrikes)));
		}
	}
	
	public final Map<Attribute, AttributeModifier> getDamageAttributesInCondition(Style style) {
		return this.attributeMap.getOrDefault(style, this.attributeMap.get(Styles.COMMON));
	}
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, LivingEntityPatch<?> entitypatch) {
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		
		if (entitypatch != null) {
			Map<Attribute, AttributeModifier> modifierMap = this.getDamageAttributesInCondition(this.getStyle(entitypatch));
			
			if (modifierMap != null) {
				for (Entry<Attribute, AttributeModifier> entry : modifierMap.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
		}
		
		return map;
    }
	
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(LivingEntityPatch<?> playerpatch, InteractionHand hand) {
		return Maps.<LivingMotion, StaticAnimation>newHashMap();
	}
	
	public Style getStyle(LivingEntityPatch<?> entitypatch) {
		return this.canBePlacedOffhand() ? Styles.ONE_HAND : Styles.TWO_HAND;
	}
	
	public boolean canBePlacedOffhand() {
		return true;
	}
	
	public boolean isEmpty() {
		return this == CapabilityItem.EMPTY;
	}
	
	public CapabilityItem getResult(ItemStack item) {
		return this;
	}
	
	public boolean availableOnHorse() {
		return this.getMountAttackMotion() != null;
	}
	
	public void setConfigFileAttribute(double armorNegation1, double impact1, int maxStrikes1, double armorNegation2, double impact2, int maxStrikes2) {
		this.addStyleAttributes(Styles.ONE_HAND, armorNegation1, impact1, maxStrikes1);
		this.addStyleAttributes(Styles.TWO_HAND, armorNegation2, impact2, maxStrikes2);
	}
	
	public boolean checkOffhandValid(LivingEntityPatch<?> entitypatch) {
		return this.getStyle(entitypatch).canUseOffhand() && EpicFightCapabilities.getItemStackCapability(entitypatch.getOriginal().getOffhandItem()).canHoldInOffhandAlone();
	}
	
	public boolean canHoldInOffhandAlone() {
		return true;
	}
	
	public UseAnim getUseAnimation(LivingEntityPatch<?> entitypatch) {
		return UseAnim.NONE;
	}
	
	public enum WeaponCategories implements WeaponCategory {
		NOT_WEAON, AXE, FIST, GREATSWORD, HOE, PICKAXE, SHOVEL, SWORD, KATANA, SPEAR, TACHI, TRIDENT, LONGSWORD, DAGGER, SHIELD, RANGED;
		
		final int id;
		
		WeaponCategories() {
			this.id = WeaponCategory.ENUM_MANAGER.assign(this);
		}
		
		@Override
		public int universalOrdinal() {
			return this.id;
		}
	}
	
	public enum HoldingOption {
		TWO_HANDED, MAINHAND_ONLY, ONE_HANDED
	}
	
	public enum Styles implements Style {
		COMMON(true), ONE_HAND(true), TWO_HAND(false), MOUNT(true), RANGED(false), SHEATH(false), LIECHTENAUER(false);
		
		final boolean canUseOffhand;
		final int id;
		
		Styles(boolean canUseOffhand) {
			this.id = Style.ENUM_MANAGER.assign(this);
			this.canUseOffhand = canUseOffhand;
		}
		
		@Override
		public int universalOrdinal() {
			return this.id;
		}
		
		public boolean canUseOffhand() {
			return this.canUseOffhand;
		}
	}
	
	public static CapabilityItem.Builder builder() {
		return new CapabilityItem.Builder();
	}
	
	public static class Builder {
		Function<Builder, CapabilityItem> constructor;
		WeaponCategory category;
		Map<Style, Map<Attribute, AttributeModifier>> attributeMap;
		
		protected Builder() {
			this.constructor = CapabilityItem::new;
			this.category = WeaponCategories.FIST;
			this.attributeMap = Maps.newHashMap();
		}
		
		public Builder constructor(Function<Builder, CapabilityItem> constructor) {
			this.constructor = constructor;
			return this;
		}
		
		public Builder category(WeaponCategory category) {
			this.category = category;
			return this;
		}
		
		public Builder addStyleAttibutes(Style style, Pair<Attribute, AttributeModifier> attributePair) {
			Map<Attribute, AttributeModifier> map = this.attributeMap.computeIfAbsent(style, (key) -> Maps.<Attribute, AttributeModifier>newHashMap());
			map.put(attributePair.getFirst(), attributePair.getSecond());
			
			return this;
		}
		
		public final CapabilityItem build() {
			return this.constructor.apply(this);
		}
	}
}