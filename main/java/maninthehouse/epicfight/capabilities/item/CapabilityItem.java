package maninthehouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Colliders;
import maninthehouse.epicfight.gamedata.Sounds;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.skill.Skill;
import maninthehouse.epicfight.skill.SkillContainer;
import maninthehouse.epicfight.skill.SkillSlot;
import maninthehouse.epicfight.utils.game.Pair;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CapabilityItem {
	protected static List<StaticAnimation> commonAutoAttackMotion;
	protected final WeaponCategory weaponCategory;
	
	static {
		commonAutoAttackMotion = new ArrayList<StaticAnimation> ();
		commonAutoAttackMotion.add(Animations.FIST_AUTO_1);
		commonAutoAttackMotion.add(Animations.FIST_AUTO_2);
		commonAutoAttackMotion.add(Animations.FIST_AUTO_3);
		commonAutoAttackMotion.add(Animations.FIST_DASH);
	}
	
	public static List<StaticAnimation> getBasicAutoAttackMotion() {
		return commonAutoAttackMotion;
	}

	protected void loadClientThings() {
		
	}
	
	protected Map<WieldStyle, Map<IAttribute, AttributeModifier>> attributeMap;
	
	public CapabilityItem(WeaponCategory category) {
		if (EpicFightMod.isPhysicalClient()) {
			loadClientThings();
		}
		this.attributeMap = Maps.<WieldStyle, Map<IAttribute, AttributeModifier>>newHashMap();
		this.weaponCategory = category;
		registerAttribute();
	}
	
	public CapabilityItem(Item material, WeaponCategory category) {
		this.attributeMap = Maps.<WieldStyle, Map<IAttribute, AttributeModifier>>newHashMap();
		this.weaponCategory = category;
	}

	protected void registerAttribute() {

	}

	public void modifyItemTooltip(List<String> itemTooltip, LivingData<?> entitydata) {
		if(this.isTwoHanded()) {
			itemTooltip.add(1, TextFormatting.DARK_GRAY + new TextComponentTranslation("attribute.name."+EpicFightMod.MODID+".twohanded").getFormattedText());
		} else if(!this.canUsedInOffhand()) {
			itemTooltip.add(1, TextFormatting.DARK_GRAY + new TextComponentTranslation("attribute.name."+EpicFightMod.MODID+".mainhand_only").getFormattedText());
		}
		
		Map<IAttribute, AttributeModifier> attribute = this.getDamageAttributesInCondition(this.getStyle(entitydata));
		
		if(attribute != null) {
			boolean flag = false;
			
			for(Map.Entry<IAttribute, AttributeModifier> attr : attribute.entrySet()) {
				itemTooltip.add(new TextComponentTranslation("attribute.name." + EpicFightMod.MODID +"."+attr.getKey().getName(), 
						ItemStack.DECIMALFORMAT.format(attr.getValue().getAmount() + attr.getKey().getDefaultValue()))
						.setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText());
				
				if (attr.getKey().equals(ModAttributes.MAX_STRIKES)) {
					flag = true;
				}
			}
			
			if(!flag) {
				itemTooltip.add(TextFormatting.DARK_GRAY + new TextComponentTranslation(ModAttributes.MAX_STRIKES.getName(), 
						ItemStack.DECIMALFORMAT.format(ModAttributes.MAX_STRIKES.getDefaultValue())).getFormattedText());
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
	
	public void onHeld(PlayerData<?> playerdata) {
		Skill specialSkill = this.getSpecialAttack(playerdata);
		if (specialSkill != null) {
			SkillContainer skillContainer = playerdata.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK);
			
			if(skillContainer.getContaining() != specialSkill) {
				skillContainer.setSkill(specialSkill);
			}
		}
		
		Skill skill = this.getPassiveSkill();
		SkillContainer skillContainer = playerdata.getSkill(SkillSlot.WEAPON_GIMMICK);
		
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
	
	public WeaponCategory getWeaponCategory() {
		return this.weaponCategory;
	}
	
	public void addStyleAttibute(WieldStyle style, Pair<IAttribute, AttributeModifier> attributePair) {
		this.attributeMap.computeIfAbsent(style, (key) -> Maps.<IAttribute, AttributeModifier>newHashMap());
		this.attributeMap.get(style).put(attributePair.first(), attributePair.second());
	}
	
	public void addStyleAttributeSimple(WieldStyle style, double armorNegation, double impact, int hitAtOnce) {
		this.addStyleAttibute(style, Pair.of(ModAttributes.ARMOR_NEGATION, ModAttributes.getArmorNegationModifier(armorNegation)));
		this.addStyleAttibute(style, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(impact)));
		this.addStyleAttibute(style, Pair.of(ModAttributes.MAX_STRIKES, ModAttributes.getMaxStrikesModifier(hitAtOnce)));
	}
	
	public final Map<IAttribute, AttributeModifier> getDamageAttributesInCondition(WieldStyle style) {
		return this.attributeMap.get(style);
	}
	
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, LivingData<?> entitydata) {
		Multimap<String, AttributeModifier> map = HashMultimap.<String, AttributeModifier>create();
		
		if(entitydata != null) {
			Map<IAttribute, AttributeModifier> modifierMap = this.getDamageAttributesInCondition(this.getStyle(entitydata));
			if(modifierMap != null) {
				for(Entry<IAttribute, AttributeModifier> entry : modifierMap.entrySet()) {
					map.put(entry.getKey().getName(), entry.getValue());
				}
			}
		}
		
		return map;
    }
	
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player) {
		return null;
	}
	
	public WieldStyle getStyle(LivingData<?> entitydata) {
		if (this.isTwoHanded()) {
			return WieldStyle.TWO_HAND;
		} else {
			if (this.isMainhandOnly()) {
				return entitydata.getOriginalEntity().getHeldItemOffhand().isEmpty() ? WieldStyle.TWO_HAND : WieldStyle.ONE_HAND;
			} else {
				return WieldStyle.ONE_HAND;
			}
		}
	}
	
	public final boolean canUsedInOffhand() {
		return this.getHandProperty() == HandProperty.GENERAL ? true : false;
	}

	public final boolean isTwoHanded() {
		return this.getHandProperty() == HandProperty.TWO_HANDED;
	}
	
	public final boolean isMainhandOnly() {
		return this.getHandProperty() == HandProperty.MAINHAND_ONLY;
	}
	
	public boolean canUseOnMount() {
		return !this.isTwoHanded();
	}
	
	public HandProperty getHandProperty() {
		return HandProperty.GENERAL;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean canBeRenderedBoth(ItemStack item) {
		return !isTwoHanded() && !item.isEmpty();
	}
	
	public enum WeaponCategory {
		NONE_WEAON, AXE, FIST, GREATSWORD, HOE, PICKAXE, SHOVEL, SWORD, KATANA, SPEAR, BOW, CROSSBOW
	}
	
	public enum HandProperty {
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}
	
	public enum WieldStyle {
		ONE_HAND, TWO_HAND, SHEATH, MOUNT
	}
}