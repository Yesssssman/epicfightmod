package yesman.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Colliders;
import yesman.epicfight.gamedata.Sounds;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCChangeSkill;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.physics.Collider;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;

public class CapabilityItem {
	public static CapabilityItem EMPTY = new CapabilityItem(WeaponCategory.NOT_WEAON);
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
	
	public void modifyItemTooltip(List<ITextComponent> itemTooltip, LivingData<?> entitydata) {
		if (this.isTwoHanded()) {
			itemTooltip.add(1, new StringTextComponent(" ").appendSibling(
					new TranslationTextComponent("attribute.name." + EpicFightMod.MODID + ".twohanded").mergeStyle(TextFormatting.DARK_GRAY)));
		} else if(this.isMainhandOnly()) {
			itemTooltip.add(1, new StringTextComponent(" ").appendSibling(
					new TranslationTextComponent("attribute.name." + EpicFightMod.MODID + ".mainhand_only").mergeStyle(TextFormatting.DARK_GRAY)));
		}
		
		Map<Attribute, AttributeModifier> attribute = this.getDamageAttributesInCondition(this.getStyle(entitydata));
		
		if (attribute != null) {
			for (Map.Entry<Attribute, AttributeModifier> attr : attribute.entrySet()) {
				if (entitydata.getOriginalEntity().getAttributeManager().hasAttributeInstance(attr.getKey())) {
					double value = attr.getValue().getAmount() + entitydata.getOriginalEntity().getAttribute(attr.getKey()).getBaseValue();
					if (value != 0.0D) {
						itemTooltip.add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent(attr.getKey().getAttributeName(), ItemStack.DECIMALFORMAT.format(value))));
					}
				}
			}
			
			if (!attribute.keySet().contains(EpicFightAttributes.MAX_STRIKES.get())) {
				itemTooltip.add(new StringTextComponent(" ").appendSibling(new TranslationTextComponent(EpicFightAttributes.MAX_STRIKES.get().getAttributeName(), ItemStack.DECIMALFORMAT.format(EpicFightAttributes.MAX_STRIKES.get().getDefaultValue()))));
			}
		}
	}
	
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return getBasicAutoAttackMotion();
	}

	public List<StaticAnimation> getMountAttackMotion() {
		return null;
	}

	public Skill getSpecialAttack(PlayerData<?> playerdata) {
		return null;
	}

	public Skill getPassiveSkill() {
		return null;
	}
	
	public WeaponCategory getWeaponCategory() {
		return this.weaponCategory;
	}
	
	public void onHeld(PlayerData<?> playerdata) {
		if (!playerdata.isRemote()) {
			Skill specialSkill = this.getSpecialAttack(playerdata);
			String skillName = "empty";
			STCChangeSkill.State state = STCChangeSkill.State.ENABLE;
			
			if (specialSkill != null) {
				SkillContainer skillContainer = playerdata.getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK);
				if (skillContainer.getContaining() != specialSkill) {
					skillContainer.setSkill(specialSkill);
					skillName = specialSkill.getSkillName();
				}
			} else {
				state = STCChangeSkill.State.DISABLE;
			}
			
			ModNetworkManager.sendToPlayer(new STCChangeSkill(SkillCategory.WEAPON_SPECIAL_ATTACK.getIndex(), skillName, state),
					(ServerPlayerEntity)playerdata.getOriginalEntity());
			
			Skill skill = this.getPassiveSkill();
			SkillContainer skillContainer = playerdata.getSkill(SkillCategory.WEAPON_PASSIVE);
			
			if (skill != null) {
				if (skillContainer.getContaining() != skill) {
					skillContainer.setSkill(skill);
					ModNetworkManager.sendToPlayer(new STCChangeSkill(skill.getCategory().getIndex(), skill.getSkillName(), STCChangeSkill.State.ENABLE),
							(ServerPlayerEntity)playerdata.getOriginalEntity());
				}
			} else {
				skillContainer.setSkill(null);
				ModNetworkManager.sendToPlayer(new STCChangeSkill(SkillCategory.WEAPON_PASSIVE.getIndex(), "empty", STCChangeSkill.State.ENABLE),
						(ServerPlayerEntity)playerdata.getOriginalEntity());
			}
		}
	}
	
	public SoundEvent getSmashingSound() {
		return Sounds.WHOOSH;
	}

	public SoundEvent getHitSound() {
		return Sounds.BLUNT_HIT;
	}

	public Collider getWeaponCollider() {
		return Colliders.fist;
	}

	public HitParticleType getHitParticle() {
		return Particles.HIT_BLUNT.get();
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
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, LivingData<?> entitydata) {
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		if(entitydata != null) {
			Map<Attribute, AttributeModifier> modifierMap = this.getDamageAttributesInCondition(this.getStyle(entitydata));
			if (modifierMap != null) {
				for (Entry<Attribute, AttributeModifier> entry : modifierMap.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
		}
		
		return map;
    }
	
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(PlayerData<?> player) {
		return Maps.<LivingMotion, StaticAnimation>newHashMap();
	}
	
	public Style getStyle(LivingData<?> entitydata) {
		if (this.isTwoHanded()) {
			return Style.TWO_HAND;
		} else {
			if (this.isMainhandOnly()) {
				return entitydata.getOriginalEntity().getHeldItemOffhand().isEmpty() ? Style.TWO_HAND : Style.ONE_HAND;
			} else {
				return Style.ONE_HAND;
			}
		}
	}
	
	public final boolean canUsedInOffhand() {
		return this.getHoldOption() == HoldOption.GENERAL ? true : false;
	}

	public final boolean isTwoHanded() {
		return this.getHoldOption() == HoldOption.TWO_HANDED;
	}
	
	public final boolean isMainhandOnly() {
		return this.getHoldOption() == HoldOption.MAINHAND_ONLY;
	}
	
	public boolean canUseOnMount() {
		return !this.isTwoHanded();
	}
	
	public HoldOption getHoldOption() {
		return HoldOption.GENERAL;
	}
	
	public CapabilityItem get(ItemStack item) {
		return this;
	}
	
	public void setConfigFileAttribute(double armorNegation1, double impact1, int maxStrikes1, double armorNegation2, double impact2, int maxStrikes2) {
		this.addStyleAttributeSimple(Style.ONE_HAND, armorNegation1, impact1, maxStrikes1);
		this.addStyleAttributeSimple(Style.TWO_HAND, armorNegation2, impact2, maxStrikes2);
	}
	
	public boolean isValidOffhandItem(ItemStack item) {
		return !this.isTwoHanded() && (!this.isEmtpy() || ModCapabilities.getItemStackCapability(item).canUsedOffhandAlone());
	}
	
	public boolean isEmtpy() {
		return this == CapabilityItem.EMPTY;
	}
	
	public boolean canUsedOffhandAlone() {
		return true;
	}
	
	public UseAction getUseAction(PlayerData<?> player) {
		return UseAction.NONE;
	}
	
	public enum WeaponCategory {
		NOT_WEAON, AXE, FIST, GREATSWORD, HOE, PICKAXE, SHOVEL, SWORD, KATANA, SPEAR, TACHI, LONGSWORD, DAGGER, SHIELD, RANGED
	}
	
	public enum HoldOption {
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}
	
	public enum Style {
		COMMON, ONE_HAND, TWO_HAND, MOUNT, AIMING, SHEATH, LIECHTENAUER
	}
}