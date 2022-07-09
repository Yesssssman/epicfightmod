package yesman.epicfight.world.capabilities.item;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.KatanaPassive;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@SuppressWarnings("deprecation")
public class WeaponCapabilityPresets {
	public static final Function<Item, CapabilityItem> AXE = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.AXE)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.TOOLS)
			.newStyleCombo(Styles.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Styles.ONE_HAND, Skills.GUILLOTINE_AXE)
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.BLOCK, Animations.SWORD_GUARD)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			if (harvestLevel != 0) {
				cap.addStyleAttibute(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.ARMOR_NEGATION.get(), EpicFightAttributes.getArmorNegationModifier(10.0D * harvestLevel)));
			}
			cap.addStyleAttibute(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(0.7D + 0.3D * harvestLevel)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> FIST = (item) -> new KnuckleCapability();
	public static final Function<Item, CapabilityItem> HOE = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.HOE)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.TOOLS).newStyleCombo(Styles.ONE_HAND, Animations.TOOL_AUTO_1, Animations.TOOL_AUTO_2, Animations.TOOL_DASH, Animations.SWORD_AIR_SLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			cap.addStyleAttibute(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(-0.4D + 0.1D * harvestLevel)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> PICKAXE = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.PICKAXE)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.TOOLS)
			.newStyleCombo(Styles.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			if (harvestLevel != 0) {
				cap.addStyleAttibute(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.ARMOR_NEGATION.get(), EpicFightAttributes.getArmorNegationModifier(6.0D * harvestLevel)));
			}
			cap.addStyleAttibute(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(0.4D + 0.1D * harvestLevel)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> SHOVEL = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.SHOVEL)
			.collider(ColliderPreset.TOOLS)
			.newStyleCombo(Styles.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			cap.addStyleAttibute(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(0.8D + 0.4D * harvestLevel)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> SWORD = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.SWORD)
			.styleProvider((playerpatch) -> playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.SWORD ? Styles.TWO_HAND : Styles.ONE_HAND)
			.collider(ColliderPreset.SWORD)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.newStyleCombo(Styles.ONE_HAND, Animations.SWORD_AUTO_1, Animations.SWORD_AUTO_2, Animations.SWORD_AUTO_3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH)
			.newStyleCombo(Styles.TWO_HAND, Animations.SWORD_DUAL_AUTO1, Animations.SWORD_DUAL_AUTO2, Animations.SWORD_DUAL_AUTO3, Animations.SWORD_DUAL_DASH, Animations.SWORD_DUAL_AIR_SLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Styles.ONE_HAND, Skills.SWEEPING_EDGE)
			.specialAttack(Styles.TWO_HAND, Skills.DANCING_EDGE)
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.BLOCK, Animations.SWORD_GUARD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.SWORD_DUAL_GUARD)
			.weaponCombinationPredicator((entitypatch) -> EpicFightCapabilities.getItemStackCapability(entitypatch.getOriginal().getOffhandItem()).weaponCategory == WeaponCategories.SWORD)
		);
		
		if (item instanceof TieredItem) {
			int harvestLevel = ((TieredItem)item).getTier().getLevel();
			cap.addStyleAttibute(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(0.5D + 0.2D * harvestLevel)));
			cap.addStyleAttibute(CapabilityItem.Styles.COMMON, Pair.of(EpicFightAttributes.MAX_STRIKES.get(), EpicFightAttributes.getMaxStrikesModifier(1)));
		}
		
		return cap;
	};
	public static final Function<Item, CapabilityItem> SPEAR = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.SPEAR)
			.styleProvider((playerpatch) -> playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.SHIELD ? Styles.ONE_HAND : Styles.TWO_HAND)
			.collider(ColliderPreset.SPEAR)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.canBePlacedOffhand(false)
			.newStyleCombo(Styles.ONE_HAND, Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_DASH, Animations.SPEAR_ONEHAND_AIR_SLASH)
			.newStyleCombo(Styles.TWO_HAND, Animations.SPEAR_TWOHAND_AUTO_1, Animations.SPEAR_TWOHAND_AUTO_2, Animations.SPEAR_DASH, Animations.SPEAR_TWOHAND_AIR_SLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SPEAR_MOUNT_ATTACK)
			.specialAttack(Styles.ONE_HAND, Skills.HEARTPIERCER)
			.specialAttack(Styles.TWO_HAND, Skills.SLAUGHTER_STANCE)
			.livingMotionModifier(Styles.ONE_HAND, LivingMotions.RUN, Animations.BIPED_RUN_SPEAR)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_RUN_SPEAR)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.SPEAR_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> GREATSWORD = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.GREATSWORD)
			.styleProvider((playerpatch) -> Styles.TWO_HAND)
			.collider(ColliderPreset.GREATSWORD)
			.swingSound(EpicFightSounds.WHOOSH_BIG)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.canBePlacedOffhand(false)
			.newStyleCombo(Styles.TWO_HAND, Animations.GREATSWORD_AUTO1, Animations.GREATSWORD_AUTO2, Animations.GREATSWORD_DASH, Animations.GREATSWORD_AIR_SLASH)
			.specialAttack(Styles.TWO_HAND, Skills.GIANT_WHIRLWIND)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.INACTION, Animations.BIPED_HOLD_GREATSWORD)
	    	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.GREATSWORD_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> KATANA = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.KATANA)
			.styleProvider((entitypatch) -> {
				if (entitypatch instanceof PlayerPatch) {
					PlayerPatch<?> playerpatch = (PlayerPatch<?>)entitypatch;
					if (playerpatch.getSkill(SkillCategories.WEAPON_PASSIVE).getDataManager().hasData(KatanaPassive.SHEATH) && 
							playerpatch.getSkill(SkillCategories.WEAPON_PASSIVE).getDataManager().getDataValue(KatanaPassive.SHEATH)) {
						return Styles.SHEATH;
					}
				}
				return Styles.TWO_HAND;
			})
			.passiveSkill(Skills.KATANA_PASSIVE)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.KATANA)
			.canBePlacedOffhand(false)
			.newStyleCombo(Styles.SHEATH, Animations.KATANA_SHEATHING_AUTO, Animations.KATANA_SHEATHING_DASH, Animations.KATANA_SHEATH_AIR_SLASH)
			.newStyleCombo(Styles.TWO_HAND, Animations.KATANA_AUTO_1, Animations.KATANA_AUTO_2, Animations.KATANA_AUTO_3, Animations.SWORD_DASH, Animations.KATANA_AIR_SLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Styles.SHEATH, Skills.FATAL_DRAW)
			.specialAttack(Styles.TWO_HAND, Skills.FATAL_DRAW)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, Animations.BIPED_HOLD_KATANA)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, Animations.BIPED_HOLD_KATANA)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_RUN_UNSHEATHING)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.FLOAT, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.FALL, Animations.BIPED_WALK_UNSHEATHING)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.IDLE, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.KNEEL, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.WALK, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.CHASE, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.RUN, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.SNEAK, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.SWIM, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.FLOAT, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Styles.SHEATH, LivingMotions.FALL, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.KATANA_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> TACHI = (item) -> {
		WeaponCapability cap = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.TACHI)
			.styleProvider((playerpatch) -> Styles.TWO_HAND)
			.collider(ColliderPreset.KATANA)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.canBePlacedOffhand(false)
			.newStyleCombo(Styles.TWO_HAND, Animations.LONGSWORD_AUTO1, Animations.LONGSWORD_AUTO2, Animations.LONGSWORD_AUTO3, Animations.TACHI_DASH, Animations.LONGSWORD_AIR_SLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Styles.TWO_HAND, Skills.LETHAL_SLICING)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.FLOAT, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.FALL, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.INACTION, Animations.BIPED_HOLD_TACHI)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.LONGSWORD_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> LONGSWORD = (item) -> {
		WeaponCapability weaponCapability = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.LONGSWORD)
			.styleProvider((entitypatch) -> {
				if (entitypatch instanceof PlayerPatch<?>) {
					if (((PlayerPatch<?>)entitypatch).getSkill(SkillCategories.WEAPON_SPECIAL_ATTACK).getRemainDuration() > 0) {
						return Styles.LIECHTENAUER;
					}
				}
				return Styles.TWO_HAND;
			})
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.LONGSWORD)
			.canBePlacedOffhand(false)
			.newStyleCombo(Styles.TWO_HAND, Animations.LONGSWORD_AUTO1, Animations.LONGSWORD_AUTO2, Animations.LONGSWORD_AUTO3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
			.newStyleCombo(Styles.LIECHTENAUER, Animations.LONGSWORD_AUTO1, Animations.LONGSWORD_AUTO2, Animations.LONGSWORD_AUTO3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Styles.TWO_HAND, Skills.LIECHTENAUER)
			.specialAttack(Styles.LIECHTENAUER, Skills.LIECHTENAUER)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.INACTION, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.LONGSWORD_GUARD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.IDLE, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.WALK, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.CHASE, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.RUN, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.SNEAK, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.KNEEL, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.JUMP, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.INACTION, Animations.BIPED_HOLD_LONGSWORD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.SWIM, Animations.BIPED_HOLD_GREATSWORD)
			.livingMotionModifier(Styles.LIECHTENAUER, LivingMotions.BLOCK, Animations.LONGSWORD_GUARD)
		);
		return weaponCapability;
	};
	public static final Function<Item, CapabilityItem> DAGGER = (item) -> {
		WeaponCapability weaponCapability = new WeaponCapability(WeaponCapability.builder()
			.category(WeaponCategories.DAGGER)
			.styleProvider((playerpatch) -> playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.DAGGER ? Styles.TWO_HAND : Styles.ONE_HAND)
			.hitSound(EpicFightSounds.BLADE_HIT)
			.collider(ColliderPreset.DAGGER)
			.weaponCombinationPredicator((entitypatch) -> EpicFightCapabilities.getItemStackCapability(entitypatch.getOriginal().getOffhandItem()).weaponCategory == WeaponCategories.DAGGER)
			.newStyleCombo(Styles.ONE_HAND, Animations.DAGGER_AUTO_1, Animations.DAGGER_AUTO_2, Animations.DAGGER_AUTO_3, Animations.SWORD_DASH, Animations.DAGGER_AIR_SLASH)
			.newStyleCombo(Styles.TWO_HAND, Animations.DAGGER_DUAL_AUTO_1, Animations.DAGGER_DUAL_AUTO_2, Animations.DAGGER_DUAL_AUTO_3, Animations.DAGGER_DUAL_AUTO_4, Animations.DAGGER_DUAL_DASH, Animations.DAGGER_DUAL_AIR_SLASH)
			.newStyleCombo(Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.specialAttack(Styles.ONE_HAND, Skills.EVISCERATE)
			.specialAttack(Styles.TWO_HAND, Skills.BLADE_RUSH)
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