package yesman.epicfight.world.capabilities.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import net.minecraft.world.item.Item;
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
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class CapabilityItem {
	public static CapabilityItem EMPTY = new CapabilityItem(WeaponCategory.FIST);
	protected static List<StaticAnimation> commonAutoAttackMotion;
	protected final WeaponCategory weaponCategory;
	
	static {
		commonAutoAttackMotion = new ArrayList<StaticAnimation> ();
		commonAutoAttackMotion.add(Animations.FIST_AUTO_1);
		commonAutoAttackMotion.add(Animations.FIST_AUTO_2);
		commonAutoAttackMotion.add(Animations.FIST_AUTO_3);
		commonAutoAttackMotion.add(Animations.FIST_DASH);
		commonAutoAttackMotion.add(Animations.FIST_AIR_SLASH);
	}
	
	public static List<StaticAnimation> getBasicAutoAttackMotion() {
		return commonAutoAttackMotion;
	}
	
	protected void loadClientThings() {
		
	}
	
	protected Map<Style, Map<Attribute, AttributeModifier>> attributeMap;
	
	public CapabilityItem(WeaponCategory category) {
		if (EpicFightMod.isPhysicalClient()) {
			loadClientThings();
		}
		this.attributeMap = Maps.<Style, Map<Attribute, AttributeModifier>>newHashMap();
		this.weaponCategory = category;
		registerAttribute();
	}
	
	public CapabilityItem(Item item, WeaponCategory category) {
		this.attributeMap = Maps.<Style, Map<Attribute, AttributeModifier>>newHashMap();
		this.weaponCategory = category;
	}

	protected void registerAttribute() {
		
	}
	
	public void modifyItemTooltip(ItemStack itemstack, List<Component> itemTooltip, LivingEntityPatch<?> entitypatch) {
		if (this.isTwoHanded()) {
			itemTooltip.add(1, new TextComponent(" ").append(new TranslatableComponent("attribute.name." + EpicFightMod.MODID + ".twohanded").withStyle(ChatFormatting.DARK_GRAY)));
		} else if(this.isMainhandOnly()) {
			itemTooltip.add(1, new TextComponent(" ").append(new TranslatableComponent("attribute.name." + EpicFightMod.MODID + ".mainhand_only").withStyle(ChatFormatting.DARK_GRAY)));
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

	public Skill getSpecialAttack(PlayerPatch<?> playerpatch) {
		return null;
	}

	public Skill getPassiveSkill() {
		return null;
	}
	
	public WeaponCategory getWeaponCategory() {
		return this.weaponCategory;
	}
	
	public void changeWeaponSpecialSkill(PlayerPatch<?> playerpatch) {
		Skill specialSkill = this.getSpecialAttack(playerpatch);
		String skillName = "empty";
		SPChangeSkill.State state = SPChangeSkill.State.ENABLE;
		SkillContainer specialSkillContainer = playerpatch.getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK);
		
		if (specialSkill != null) {
			if (specialSkillContainer.getSkill() != specialSkill) {
				specialSkillContainer.setSkill(specialSkill);
				skillName = specialSkill.getName();
			} else {
				specialSkillContainer.getSkill().onInitiate(specialSkillContainer);
			}
			specialSkillContainer.setDisabled(false);
		} else {
			state = SPChangeSkill.State.DISABLE;
			specialSkillContainer.setDisabled(true);
		}
		
		EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(SkillCategory.WEAPON_SPECIAL_ATTACK.getIndex(), skillName, state), (ServerPlayer)playerpatch.getOriginal());
		
		Skill skill = this.getPassiveSkill();
		SkillContainer passiveSkillContainer = playerpatch.getSkill(SkillCategory.WEAPON_PASSIVE);
		
		if (skill != null) {
			if (passiveSkillContainer.getSkill() != skill) {
				passiveSkillContainer.setSkill(skill);
				EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(skill.getCategory().getIndex(), skill.getName(), SPChangeSkill.State.ENABLE), (ServerPlayer)playerpatch.getOriginal());
			}
		} else {
			passiveSkillContainer.setSkill(null);
			EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(SkillCategory.WEAPON_PASSIVE.getIndex(), "empty", SPChangeSkill.State.ENABLE), (ServerPlayer)playerpatch.getOriginal());
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
	
	public void addStyleAttibute(Style style, Pair<Attribute, AttributeModifier> attributePair) {
		this.attributeMap.computeIfAbsent(style, (key) -> Maps.<Attribute, AttributeModifier>newHashMap());
		this.attributeMap.get(style).put(attributePair.getFirst(), attributePair.getSecond());
	}
	
	public void addStyleAttributeSimple(Style style, double armorNegation, double impact, int maxStrikes) {
		if (Double.compare(armorNegation, 0.0D) != 0) {
			this.addStyleAttibute(style, Pair.of(EpicFightAttributes.ARMOR_NEGATION.get(), EpicFightAttributes.getArmorNegationModifier(armorNegation)));
		}
		if (Double.compare(impact, 0.0D) != 0) {
			this.addStyleAttibute(style, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(impact)));
		}
		if (Double.compare(maxStrikes, 0.0D) != 0) {
			this.addStyleAttibute(style, Pair.of(EpicFightAttributes.MAX_STRIKES.get(), EpicFightAttributes.getMaxStrikesModifier(maxStrikes)));
		}
	}
	
	public final Map<Attribute, AttributeModifier> getDamageAttributesInCondition(Style style) {
		return this.attributeMap.getOrDefault(style, this.attributeMap.get(Style.COMMON));
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
		if (this.isTwoHanded()) {
			return Style.TWO_HAND;
		} else {
			if (this.isMainhandOnly()) {
				return entitypatch.getOriginal().getOffhandItem().isEmpty() ? Style.TWO_HAND : Style.ONE_HAND;
			} else {
				return Style.ONE_HAND;
			}
		}
	}
	
	public final boolean canUsedInOffhand() {
		return this.getHoldOption() == HoldingOption.GENERAL ? true : false;
	}

	public final boolean isTwoHanded() {
		return this.getHoldOption() == HoldingOption.TWO_HANDED;
	}
	
	public final boolean isMainhandOnly() {
		return this.getHoldOption() == HoldingOption.MAINHAND_ONLY;
	}
	
	public boolean isEmpty() {
		return this == CapabilityItem.EMPTY;
	}
	
	public CapabilityItem getResult(ItemStack item) {
		return this;
	}
	
	public boolean canUseOnMount() {
		return !this.isTwoHanded();
	}
	
	public HoldingOption getHoldOption() {
		return HoldingOption.GENERAL;
	}
	
	public void setConfigFileAttribute(double armorNegation1, double impact1, int maxStrikes1, double armorNegation2, double impact2, int maxStrikes2) {
		this.addStyleAttributeSimple(Style.ONE_HAND, armorNegation1, impact1, maxStrikes1);
		this.addStyleAttributeSimple(Style.TWO_HAND, armorNegation2, impact2, maxStrikes2);
	}
	
	public boolean checkOffhandUsable(ItemStack offhandItem) {
		return !this.isTwoHanded() && EpicFightCapabilities.getItemStackCapability(offhandItem).canUsedInOffhandAlone();
	}
	
	public boolean canUsedInOffhandAlone() {
		return true;
	}
	
	public UseAnim getUseAnimation(LivingEntityPatch<?> playerpatch) {
		return UseAnim.NONE;
	}
	
	public enum WeaponCategory {
		NOT_WEAON, AXE, FIST, GREATSWORD, HOE, PICKAXE, SHOVEL, SWORD, KATANA, SPEAR, TACHI, TRIDENT, LONGSWORD, DAGGER, SHIELD, RANGED
	}
	
	public enum HoldingOption {
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}
	
	public enum Style {
		COMMON, ONE_HAND, TWO_HAND, MOUNT, AIMING, SHEATH, LIECHTENAUER
	}
}