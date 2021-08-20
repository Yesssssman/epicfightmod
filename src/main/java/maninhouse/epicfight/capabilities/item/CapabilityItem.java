package maninhouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.skill.Skill;
import maninhouse.epicfight.skill.SkillCategory;
import maninhouse.epicfight.skill.SkillContainer;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

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
	
	protected Map<HoldStyle, Map<Supplier<Attribute>, AttributeModifier>> attributeMap;
	
	public CapabilityItem(WeaponCategory category) {
		if (EpicFightMod.isPhysicalClient()) {
			loadClientThings();
		}
		this.attributeMap = Maps.<HoldStyle, Map<Supplier<Attribute>, AttributeModifier>>newHashMap();
		this.weaponCategory = category;
		registerAttribute();
	}
	
	public CapabilityItem(Item item, WeaponCategory category) {
		this.attributeMap = Maps.<HoldStyle, Map<Supplier<Attribute>, AttributeModifier>>newHashMap();
		this.weaponCategory = category;
	}

	protected void registerAttribute() {
		
	}
	
	public void modifyItemTooltip(List<ITextComponent> itemTooltip, LivingData<?> entitydata) {
		if(this.isTwoHanded()) {
			itemTooltip.add(1, new StringTextComponent(" ").append(
					new TranslationTextComponent("attribute.name."+EpicFightMod.MODID+".twohanded").mergeStyle(TextFormatting.DARK_GRAY)));
		} else if(this.isMainhandOnly()) {
			itemTooltip.add(1, new StringTextComponent(" ").append(
					new TranslationTextComponent("attribute.name."+EpicFightMod.MODID+".mainhand_only").mergeStyle(TextFormatting.DARK_GRAY)));
		}
		
		Map<Supplier<Attribute>, AttributeModifier> attribute = this.getDamageAttributesInCondition(this.getStyle(entitydata));
		
		if(attribute != null) {
			for(Map.Entry<Supplier<Attribute>, AttributeModifier> attr : attribute.entrySet()) {
				if (entitydata.getOriginalEntity().getAttributeManager().hasAttributeInstance(attr.getKey().get())) {
					double value = attr.getValue().getAmount() + entitydata.getOriginalEntity().getAttribute(attr.getKey().get()).getBaseValue();
					if (value != 0.0D) {
						itemTooltip.add(new StringTextComponent(" ").append(
							new TranslationTextComponent(attr.getKey().get().getAttributeName(), ItemStack.DECIMALFORMAT.format(value))));
					}
				}
			}
			
			if(!attribute.keySet().contains(ModAttributes.MAX_STRIKES)) {
				itemTooltip.add(new StringTextComponent(" ").append(new TranslationTextComponent(ModAttributes.MAX_STRIKES.get().getAttributeName(), 
						ItemStack.DECIMALFORMAT.format(ModAttributes.MAX_STRIKES.get().getDefaultValue()))));
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
		Skill specialSkill = this.getSpecialAttack(playerdata);
		if (specialSkill != null) {
			SkillContainer skillContainer = playerdata.getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK);
			if(skillContainer.getContaining() != specialSkill) {
				skillContainer.setSkill(specialSkill);
			}
		}
		
		Skill skill = this.getPassiveSkill();
		SkillContainer skillContainer = playerdata.getSkill(SkillCategory.WEAPON_PASSIVE);
		
		if(skill == null) {
			skillContainer.setSkill(null);
		} else {
			if(skillContainer.getContaining() != skill) {
				skillContainer.setSkill(skill);
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
	
	public void addStyleAttibute(HoldStyle style, Pair<Supplier<Attribute>, AttributeModifier> attributePair) {
		this.attributeMap.computeIfAbsent(style, (key) -> Maps.<Supplier<Attribute>, AttributeModifier>newHashMap());
		this.attributeMap.get(style).put(attributePair.getFirst(), attributePair.getSecond());
	}
	
	public void addStyleAttributeSimple(HoldStyle style, double armorNegation, double impact, int maxStrikes) {
		this.addStyleAttibute(style, Pair.of(ModAttributes.ARMOR_NEGATION, ModAttributes.getArmorNegationModifier(armorNegation)));
		this.addStyleAttibute(style, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(impact)));
		this.addStyleAttibute(style, Pair.of(ModAttributes.MAX_STRIKES, ModAttributes.getMaxStrikesModifier(maxStrikes)));
	}
	
	public final Map<Supplier<Attribute>, AttributeModifier> getDamageAttributesInCondition(HoldStyle style) {
		return this.attributeMap.get(style);
	}
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, LivingData<?> entitydata) {
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		if(entitydata != null) {
			Map<Supplier<Attribute>, AttributeModifier> modifierMap = this.getDamageAttributesInCondition(this.getStyle(entitydata));
			if (modifierMap != null) {
				for(Entry<Supplier<Attribute>, AttributeModifier> entry : modifierMap.entrySet()) {
					map.put(entry.getKey().get(), entry.getValue());
				}
			}
		}
		
		return map;
    }
	
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player) {
		return Maps.<LivingMotion, StaticAnimation>newHashMap();
	}
	
	public HoldStyle getStyle(LivingData<?> entitydata) {
		if (this.isTwoHanded()) {
			return HoldStyle.TWO_HAND;
		} else {
			if (this.isMainhandOnly()) {
				return entitydata.getOriginalEntity().getHeldItemOffhand().isEmpty() ? HoldStyle.TWO_HAND : HoldStyle.ONE_HAND;
			} else {
				return HoldStyle.ONE_HAND;
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
	
	public void setCustomWeapon(double armorNegation1, double impact1, int maxStrikes1, double armorNegation2, double impact2, int maxStrikes2) {
		this.addStyleAttributeSimple(HoldStyle.ONE_HAND, armorNegation1, impact1, maxStrikes1);
		this.addStyleAttributeSimple(HoldStyle.TWO_HAND, armorNegation2, impact2, maxStrikes2);
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
		NOT_WEAON, AXE, FIST, GREATSWORD, HOE, PICKAXE, SHOVEL, SWORD, KATANA, SPEAR, TACHI, LONGSWORD, DAGGER, RANGED
	}
	
	public enum HoldOption {
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}
	
	public enum HoldStyle {
		ONE_HAND, TWO_HAND, MOUNT, AIMING, SHEATH, LIECHTENHAUER
	}
}