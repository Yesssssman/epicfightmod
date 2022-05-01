package yesman.epicfight.world.capabilities.item;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.KatanaPassive;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.HoldingOption;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Style;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@SuppressWarnings("deprecation")
public class WeaponCapabilityPresets {
	public static final Function<Item, CapabilityItem> AXE = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.AXE)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.TOOLS)
			.newStyleCombo(Style.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH,Animations.AXE_AIRSLASH)
			.newStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Style.ONE_HAND, Skills.GUILLOTINE_AXE)
			.livingMotionModifier(Style.ONE_HAND, LivingMotion.BLOCK, Animations.SWORD_GUARD)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			if (harvestLevel != 0) {
				cap.addStyleAttibute(CapabilityItem.Style.COMMON, Pair.of(EpicFightAttributes.ARMOR_NEGATION.get(), EpicFightAttributes.getArmorNegationModifier(10.0D * harvestLevel)));
			}
			cap.addStyleAttibute(CapabilityItem.Style.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(0.7D + 0.3D * harvestLevel)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> FIST = (item) -> new KnuckleCapability();
	public static final Function<Item, CapabilityItem> HOE = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.HOE)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.TOOLS).newStyleCombo(Style.ONE_HAND, Animations.TOOL_AUTO_1, Animations.TOOL_AUTO_2, Animations.TOOL_DASH, Animations.SWORD_AIR_SLASH)
			.newStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			cap.addStyleAttibute(CapabilityItem.Style.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(-0.4D + 0.1D * harvestLevel)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> PICKAXE = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.PICKAXE)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.TOOLS)
			.newStyleCombo(Style.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
			.newStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			if (harvestLevel != 0) {
				cap.addStyleAttibute(CapabilityItem.Style.COMMON, Pair.of(EpicFightAttributes.ARMOR_NEGATION.get(), EpicFightAttributes.getArmorNegationModifier(6.0D * harvestLevel)));
			}
			cap.addStyleAttibute(CapabilityItem.Style.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(0.4D + 0.1D * harvestLevel)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> SHOVEL = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.SHOVEL)
			.collider(ColliderPreset.TOOLS)
			.newStyleCombo(Style.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
			.newStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			cap.addStyleAttibute(CapabilityItem.Style.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(0.8D + 0.4D * harvestLevel)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> SWORD = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.SWORD)
			.styleProvider((playerpatch) -> playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategory.SWORD ? Style.TWO_HAND : Style.ONE_HAND)
			.collider(ColliderPreset.SWORD)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.newStyleCombo(Style.ONE_HAND, Animations.SWORD_AUTO_1, Animations.SWORD_AUTO_2, Animations.SWORD_AUTO_3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH)
			.newStyleCombo(Style.TWO_HAND, Animations.SWORD_DUAL_AUTO1, Animations.SWORD_DUAL_AUTO2, Animations.SWORD_DUAL_AUTO3, Animations.SWORD_DUAL_DASH, Animations.SWORD_DUAL_AIR_SLASH)
			.newStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Style.ONE_HAND, Skills.SWEEPING_EDGE)
			.specialAttack(Style.TWO_HAND, Skills.DANCING_EDGE)
			.livingMotionModifier(Style.ONE_HAND, LivingMotion.BLOCK, Animations.SWORD_GUARD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.SWORD_DUAL_GUARD)
			.offhandPredicator((itemstack) -> EpicFightCapabilities.getItemStackCapability(itemstack).weaponCategory == WeaponCategory.SWORD)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			cap.addStyleAttibute(CapabilityItem.Style.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(0.5D + 0.2D * harvestLevel)));
			cap.addStyleAttibute(CapabilityItem.Style.COMMON, Pair.of(EpicFightAttributes.MAX_STRIKES.get(), EpicFightAttributes.getMaxStrikesModifier(1)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> SPEAR = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.SPEAR)
			.styleProvider((playerpatch) -> playerpatch.getOriginal().getOffhandItem().isEmpty() ? Style.TWO_HAND : Style.ONE_HAND)
			.collider(ColliderPreset.SPEAR)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.holdingOption(HoldingOption.MAINHAND_ONLY)
			.newStyleCombo(Style.ONE_HAND, Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_DASH, Animations.SPEAR_ONEHAND_AIR_SLASH)
			.newStyleCombo(Style.TWO_HAND, Animations.SPEAR_TWOHAND_AUTO_1, Animations.SPEAR_TWOHAND_AUTO_2, Animations.SPEAR_DASH, Animations.SPEAR_TWOHAND_AIR_SLASH)
			.newStyleCombo(Style.MOUNT, Animations.SPEAR_MOUNT_ATTACK)
			.specialAttack(Style.ONE_HAND, Skills.HEARTPIERCER)
			.specialAttack(Style.TWO_HAND, Skills.SLAUGHTER_STANCE)
			.livingMotionModifier(Style.ONE_HAND, LivingMotion.RUN, Animations.BIPED_RUN_SPEAR)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.WALK, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.CHASE, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_RUN_SPEAR)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.SPEAR_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> GREATSWORD = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.GREATSWORD)
			.styleProvider((playerpatch) -> Style.TWO_HAND)
			.collider(ColliderPreset.GREATSWORD)
			.swingSound(EpicFightSounds.WHOOSH_BIG)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.holdingOption(HoldingOption.TWO_HANDED)
			.newStyleCombo(Style.TWO_HAND, Animations.GREATSWORD_AUTO1, Animations.GREATSWORD_AUTO2, Animations.GREATSWORD_DASH, Animations.GREATSWORD_AIR_SLASH)
			.specialAttack(Style.TWO_HAND, Skills.GIANT_WHIRLWIND)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.WALK, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.CHASE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Style.TWO_HAND, LivingMotion.JUMP, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Style.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Style.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Style.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Style.TWO_HAND, LivingMotion.INACTION, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.GREATSWORD_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> KATANA = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.KATANA)
			.styleProvider((entitypatch) -> {
				if (entitypatch instanceof PlayerPatch) {
					PlayerPatch<?> playerpatch = (PlayerPatch<?>)entitypatch;
					if (playerpatch.getSkill(SkillCategory.WEAPON_PASSIVE).getDataManager().hasData(KatanaPassive.SHEATH) && 
							playerpatch.getSkill(SkillCategory.WEAPON_PASSIVE).getDataManager().getDataValue(KatanaPassive.SHEATH)) {
						return Style.SHEATH;
					}
				}
				return Style.TWO_HAND;
			})
			.passiveSkill(Skills.KATANA_PASSIVE)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.KATANA)
			.holdingOption(HoldingOption.TWO_HANDED)
			.newStyleCombo(Style.SHEATH, Animations.KATANA_SHEATHING_AUTO, Animations.KATANA_SHEATHING_DASH, Animations.KATANA_SHEATH_AIR_SLASH)
			.newStyleCombo(Style.TWO_HAND, Animations.KATANA_AUTO_1, Animations.KATANA_AUTO_2, Animations.KATANA_AUTO_3, Animations.SWORD_DASH, Animations.KATANA_AIR_SLASH)
			.newStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Style.SHEATH, Skills.FATAL_DRAW)
			.specialAttack(Style.TWO_HAND, Skills.FATAL_DRAW)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_HOLD_KATANA)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_HOLD_KATANA)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.WALK, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.CHASE, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_RUN_UNSHEATHING)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.FLOAT, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.FALL, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Style.SHEATH, LivingMotion.IDLE, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Style.SHEATH, LivingMotion.KNEEL, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Style.SHEATH, LivingMotion.WALK, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Style.SHEATH, LivingMotion.CHASE, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Style.SHEATH, LivingMotion.RUN, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Style.SHEATH, LivingMotion.SNEAK, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Style.SHEATH, LivingMotion.SWIM, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Style.SHEATH, LivingMotion.FLOAT, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Style.SHEATH, LivingMotion.FALL, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.KATANA_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> TACHI = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.TACHI)
			.styleProvider((playerpatch) -> Style.TWO_HAND)
			.collider(ColliderPreset.KATANA)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.holdingOption(HoldingOption.TWO_HANDED)
			.newStyleCombo(Style.TWO_HAND, Animations.LONGSWORD_AUTO1, Animations.LONGSWORD_AUTO2, Animations.LONGSWORD_AUTO3, Animations.TACHI_DASH, Animations.LONGSWORD_AIR_SLASH)
			.newStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Style.TWO_HAND, Skills.LETHAL_SLICING)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.WALK, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.CHASE, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.FLOAT, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.FALL, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.INACTION, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.LONGSWORD_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> LONGSWORD = (item) -> {
		WeaponCapability weaponCapability = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.LONGSWORD)
			.styleProvider((entitypatch) -> {
				if (entitypatch instanceof PlayerPatch<?>) {
					if (((PlayerPatch<?>)entitypatch).getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK).getRemainDuration() > 0) {
						return Style.LIECHTENAUER;
					}
				}
				return Style.TWO_HAND;
			})
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.LONGSWORD)
			.holdingOption(HoldingOption.TWO_HANDED)
			.newStyleCombo(Style.TWO_HAND, Animations.LONGSWORD_AUTO1, Animations.LONGSWORD_AUTO2, Animations.LONGSWORD_AUTO3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
			.newStyleCombo(Style.LIECHTENAUER, Animations.LONGSWORD_AUTO1, Animations.LONGSWORD_AUTO2, Animations.LONGSWORD_AUTO3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
			.newStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Style.TWO_HAND, Skills.LIECHTENAUER)
			.specialAttack(Style.LIECHTENAUER, Skills.LIECHTENAUER)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.WALK, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.CHASE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.JUMP, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.INACTION, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.LONGSWORD_GUARD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.IDLE, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.WALK, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.CHASE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.RUN, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.SNEAK, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.KNEEL, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.JUMP, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.INACTION, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.SWIM, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Style.LIECHTENAUER, LivingMotion.BLOCK, Animations.LONGSWORD_GUARD)
		);
		return weaponCapability;
	};
	public static final Function<Item, CapabilityItem> DAGGER = (item) -> {
		WeaponCapability weaponCapability = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategory.DAGGER)
			.styleProvider((playerpatch) -> playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategory.DAGGER ? Style.TWO_HAND : Style.ONE_HAND)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.DAGGER)
			.offhandPredicator((itemstack) -> EpicFightCapabilities.getItemStackCapability(itemstack).weaponCategory == WeaponCategory.DAGGER)
			.newStyleCombo(Style.ONE_HAND, Animations.DAGGER_AUTO_1, Animations.DAGGER_AUTO_2, Animations.DAGGER_AUTO_3, Animations.SWORD_DASH, Animations.DAGGER_AIR_SLASH)
			.newStyleCombo(Style.TWO_HAND, Animations.DAGGER_DUAL_AUTO_1, Animations.DAGGER_DUAL_AUTO_2, Animations.DAGGER_DUAL_AUTO_3, Animations.DAGGER_DUAL_AUTO_4, Animations.DAGGER_DUAL_DASH, Animations.DAGGER_DUAL_AIR_SLASH)
			.newStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Style.ONE_HAND, Skills.EVISCERATE)
			.specialAttack(Style.TWO_HAND, Skills.BLADE_RUSH)
		);
		return weaponCapability;
	};
	public static final Function<Item, CapabilityItem> BOW = BowCapability::new;
	public static final Function<Item, CapabilityItem> CROSSBOW = CrossbowCapability::new;
	public static final Function<Item, CapabilityItem> TRIDENT = TridentCapability::new;
	public static final Function<Item, CapabilityItem> SHIELD = ShieldCapability::new;
	
	private static final Map<String, Function<Item, CapabilityItem>> PRESETS = Maps.newHashMap();
	
	public static void register() {
		Map<String, Function<Item, CapabilityItem>> typeEntry = Maps.newHashMap();
		typeEntry.put("axe", AXE);
		typeEntry.put("fist", FIST);
		typeEntry.put("hoe", HOE);
		typeEntry.put("pickaxe", PICKAXE);
		typeEntry.put("shovel", SHOVEL);
		typeEntry.put("sword", SWORD);
		typeEntry.put("spear", SPEAR);
		typeEntry.put("greatsword", GREATSWORD);
		typeEntry.put("katana", KATANA);
		typeEntry.put("tachi", TACHI);
		typeEntry.put("longsword", LONGSWORD);
		typeEntry.put("dagger", DAGGER);
		typeEntry.put("bow", BOW);
		typeEntry.put("crossbow", CROSSBOW);
		typeEntry.put("trident", TRIDENT);
		typeEntry.put("shield", SHIELD);
		
		WeaponCapabilityPresetRegistryEvent weaponCapabilityPresetRegistryEvent = new WeaponCapabilityPresetRegistryEvent(typeEntry);
		ModLoader.get().postEvent(weaponCapabilityPresetRegistryEvent);
		weaponCapabilityPresetRegistryEvent.getTypeEntry().forEach(PRESETS::put);
	}
	
	public static Function<Item, CapabilityItem> get(String typeName) {
		return PRESETS.get(typeName);
	}
}