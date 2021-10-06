package yesman.epicfight.capabilities.item;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem.HoldOption;
import yesman.epicfight.capabilities.item.CapabilityItem.Style;
import yesman.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Colliders;
import yesman.epicfight.gamedata.Skills;
import yesman.epicfight.gamedata.Sounds;
import yesman.epicfight.skill.KatanaPassive;
import yesman.epicfight.skill.SkillCategory;

public class DefinedWeaponTypes {
	public static final Function<Item, CapabilityItem> AXE = item -> {
		ModWeaponCapability cap = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.AXE)
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.tools)
			.addStyleCombo(Style.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH,Animations.AXE_AIRSLASH)
			.addStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(Style.ONE_HAND, Skills.GUILLOTINE_AXE)
			.addLivingMotionModifier(Style.ONE_HAND, LivingMotion.BLOCK, Animations.SWORD_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> FIST = item -> new KnuckleCapability();
	public static final Function<Item, CapabilityItem> HOE = item -> {
		ModWeaponCapability cap = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.HOE)
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.tools).addStyleCombo(Style.ONE_HAND, Animations.TOOL_AUTO_1, Animations.TOOL_AUTO_2, Animations.TOOL_DASH, Animations.SWORD_AIR_SLASH)
			.addStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> PICKAXE = item -> {
		ModWeaponCapability cap = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.PICKAXE)
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.tools)
			.addStyleCombo(Style.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
			.addStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> SHOVEL = item -> {
		ModWeaponCapability cap = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.SHOVEL)
			.setWeaponCollider(Colliders.tools)
			.addStyleCombo(Style.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
			.addStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> SWORD = item -> {
		ModWeaponCapability cap = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.SWORD)
			.setStyleGetter((playerdata) -> playerdata.getHeldItemCapability(Hand.OFF_HAND).getWeaponCategory() == WeaponCategory.SWORD ? Style.TWO_HAND : Style.ONE_HAND)
			.setWeaponCollider(Colliders.sword)
			.setHitSound(Sounds.BLADE_HIT)
			.addStyleCombo(Style.ONE_HAND, Animations.SWORD_AUTO_1, Animations.SWORD_AUTO_2, Animations.SWORD_AUTO_3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH)
			.addStyleCombo(Style.TWO_HAND, Animations.SWORD_DUAL_AUTO_1, Animations.SWORD_DUAL_AUTO_2, Animations.SWORD_DUAL_AUTO_3, Animations.SWORD_DUAL_DASH, Animations.SWORD_DUAL_AIR_SLASH)
			.addStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(Style.ONE_HAND, Skills.SWEEPING_EDGE)
			.addStyleSpecialAttack(Style.TWO_HAND, Skills.DANCING_EDGE)
			.addLivingMotionModifier(Style.ONE_HAND, LivingMotion.BLOCK, Animations.SWORD_GUARD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.SWORD_DUAL_GUARD)
			.addOffhandPredicator((itemstack) -> ModCapabilities.getItemStackCapability(itemstack).weaponCategory == WeaponCategory.SWORD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> SPEAR = item -> {
		ModWeaponCapability cap = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.SPEAR)
			.setStyleGetter((playerdata) -> playerdata.getOriginalEntity().getHeldItemOffhand().isEmpty() ? Style.TWO_HAND : Style.ONE_HAND)
			.setWeaponCollider(Colliders.spear)
			.setHitSound(Sounds.BLADE_HIT)
			.setHoldOption(HoldOption.MAINHAND_ONLY)
			.addStyleCombo(Style.ONE_HAND, Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_DASH, Animations.SPEAR_ONEHAND_AIR_SLASH)
			.addStyleCombo(Style.TWO_HAND, Animations.SPEAR_TWOHAND_AUTO_1, Animations.SPEAR_TWOHAND_AUTO_2, Animations.SPEAR_DASH, Animations.SPEAR_TWOHAND_AIR_SLASH)
			.addStyleCombo(Style.MOUNT, Animations.SPEAR_MOUNT_ATTACK)
			.addStyleSpecialAttack(Style.ONE_HAND, Skills.HEARTPIERCER)
			.addStyleSpecialAttack(Style.TWO_HAND, Skills.SLAUGHTER_STANCE)
			.addLivingMotionModifier(Style.ONE_HAND, LivingMotion.RUN, Animations.BIPED_RUN_SPEAR)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_RUN_SPEAR)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.SPEAR_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> GREATSWORD = item -> {
		ModWeaponCapability cap = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.GREATSWORD)
			.setStyleGetter((playerdata) -> Style.TWO_HAND)
			.setWeaponCollider(Colliders.greatSword)
			.setSmashingSound(Sounds.WHOOSH_BIG)
			.setHitSound(Sounds.BLADE_HIT)
			.setHoldOption(HoldOption.TWO_HANDED)
			.addStyleCombo(Style.TWO_HAND, Animations.GREATSWORD_AUTO_1, Animations.GREATSWORD_AUTO_2, Animations.GREATSWORD_DASH, Animations.GREATSWORD_AIR_SLASH)
			.addStyleSpecialAttack(Style.TWO_HAND, Skills.GIANT_WHIRLWIND)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.WALK, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_HOLD_GREATSWORD)
	    	.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.JUMP, Animations.BIPED_HOLD_GREATSWORD)
	    	.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
	    	.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
	    	.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_HOLD_GREATSWORD)
	    	.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.INACTION, Animations.BIPED_HOLD_GREATSWORD)
	    	.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.GREATSWORD_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> KATANA = item -> {
		ModWeaponCapability cap = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.KATANA)
			.setStyleGetter((entitydata) -> {
				if (entitydata instanceof PlayerData) {
					PlayerData<?> playerdata = (PlayerData<?>)entitydata;
					if (playerdata.getSkill(SkillCategory.WEAPON_PASSIVE).getDataManager().hasData(KatanaPassive.SHEATH) && 
							playerdata.getSkill(SkillCategory.WEAPON_PASSIVE).getDataManager().getDataValue(KatanaPassive.SHEATH)) {
						return Style.SHEATH;
					}
				}
				return Style.TWO_HAND;
			})
			.setPassiveSkill(Skills.KATANA_PASSIVE)
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.katana)
			.setHoldOption(HoldOption.TWO_HANDED)
			.addStyleCombo(Style.SHEATH, Animations.KATANA_SHEATHING_AUTO, Animations.KATANA_SHEATHING_DASH, Animations.KATANA_SHEATH_AIR_SLASH)
			.addStyleCombo(Style.TWO_HAND, Animations.KATANA_AUTO_1, Animations.KATANA_AUTO_2, Animations.KATANA_AUTO_3, Animations.SWORD_DASH, Animations.KATANA_AIR_SLASH)
			.addStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(Style.SHEATH, Skills.FATAL_DRAW)
			.addStyleSpecialAttack(Style.TWO_HAND, Skills.FATAL_DRAW)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_UNSHEATHING)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_UNSHEATHING)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.WALK, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_RUN_UNSHEATHING)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.FLOAT, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.FALL, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(Style.SHEATH, LivingMotion.IDLE, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.addLivingMotionModifier(Style.SHEATH, LivingMotion.KNEEL, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.addLivingMotionModifier(Style.SHEATH, LivingMotion.WALK, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.addLivingMotionModifier(Style.SHEATH, LivingMotion.RUN, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.addLivingMotionModifier(Style.SHEATH, LivingMotion.SNEAK, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.addLivingMotionModifier(Style.SHEATH, LivingMotion.SWIM, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.addLivingMotionModifier(Style.SHEATH, LivingMotion.FLOAT, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.addLivingMotionModifier(Style.SHEATH, LivingMotion.FALL, Animations.BIPED_HOLD_KATANA_SHEATHING)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.KATANA_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> TACHI = item -> {
		ModWeaponCapability cap = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.TACHI)
			.setStyleGetter((playerdata) -> Style.TWO_HAND)
			.setWeaponCollider(Colliders.katana)
			.setHitSound(Sounds.BLADE_HIT)
			.setHoldOption(HoldOption.TWO_HANDED)
			.addStyleCombo(Style.TWO_HAND, Animations.LONGSWORD_AUTO_1, Animations.LONGSWORD_AUTO_2, Animations.LONGSWORD_AUTO_3, Animations.TACHI_DASH, Animations.LONGSWORD_AIR_SLASH)
			.addStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(Style.TWO_HAND, Skills.LETHAL_SLICING)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.WALK, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.FLOAT, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.FALL, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.INACTION, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.LONGSWORD_GUARD)
		);
		return cap;
	};
	public static final Function<Item, CapabilityItem> LONGSWORD = item -> {
		ModWeaponCapability weaponCapability = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.LONGSWORD)
			.setStyleGetter((playerdata) -> {
				if (playerdata instanceof PlayerData<?>) {
					if (((PlayerData<?>)playerdata).getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK).getRemainDuration() > 0) {
						return Style.LIECHTENAUER;
					}
				}
				return Style.TWO_HAND;
			})
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.longsword)
			.setHoldOption(HoldOption.TWO_HANDED)
			.addStyleCombo(Style.TWO_HAND, Animations.LONGSWORD_AUTO_1, Animations.LONGSWORD_AUTO_2, Animations.LONGSWORD_AUTO_3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
			.addStyleCombo(Style.LIECHTENAUER, Animations.LONGSWORD_AUTO_1, Animations.LONGSWORD_AUTO_2, Animations.LONGSWORD_AUTO_3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
			.addStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(Style.TWO_HAND, Skills.LIECHTENAUER)
			.addStyleSpecialAttack(Style.LIECHTENAUER, Skills.LIECHTENAUER)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.WALK, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.RUN, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.JUMP, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.INACTION, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.TWO_HAND, LivingMotion.BLOCK, Animations.LONGSWORD_GUARD)
			.addLivingMotionModifier(Style.LIECHTENAUER, LivingMotion.IDLE, Animations.BIPED_HOLD_LONGSWORD)
			.addLivingMotionModifier(Style.LIECHTENAUER, LivingMotion.WALK, Animations.BIPED_HOLD_LONGSWORD)
			.addLivingMotionModifier(Style.LIECHTENAUER, LivingMotion.RUN, Animations.BIPED_HOLD_LONGSWORD)
			.addLivingMotionModifier(Style.LIECHTENAUER, LivingMotion.SNEAK, Animations.BIPED_HOLD_LONGSWORD)
			.addLivingMotionModifier(Style.LIECHTENAUER, LivingMotion.KNEEL, Animations.BIPED_HOLD_LONGSWORD)
			.addLivingMotionModifier(Style.LIECHTENAUER, LivingMotion.JUMP, Animations.BIPED_HOLD_LONGSWORD)
			.addLivingMotionModifier(Style.LIECHTENAUER, LivingMotion.INACTION, Animations.BIPED_HOLD_LONGSWORD)
			.addLivingMotionModifier(Style.LIECHTENAUER, LivingMotion.SWIM, Animations.BIPED_HOLD_GREATSWORD)
			.addLivingMotionModifier(Style.LIECHTENAUER, LivingMotion.BLOCK, Animations.LONGSWORD_GUARD)
		);
		return weaponCapability;
	};
	public static final Function<Item, CapabilityItem> DAGGER = item -> {
		ModWeaponCapability weaponCapability = new ModWeaponCapability(ModWeaponCapability.builder()
			.setCategory(WeaponCategory.DAGGER)
			.setStyleGetter((playerdata) -> playerdata.getHeldItemCapability(Hand.OFF_HAND).getWeaponCategory() == WeaponCategory.DAGGER ? Style.TWO_HAND : Style.ONE_HAND)
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.dagger)
			.addStyleCombo(Style.ONE_HAND, Animations.DAGGER_AUTO_1, Animations.DAGGER_AUTO_2, Animations.DAGGER_AUTO_3, Animations.SWORD_DASH, Animations.DAGGER_AIR_SLASH)
			.addStyleCombo(Style.TWO_HAND, Animations.DAGGER_DUAL_AUTO_1, Animations.DAGGER_DUAL_AUTO_2, Animations.DAGGER_DUAL_AUTO_3, Animations.DAGGER_DUAL_AUTO_4, Animations.DAGGER_DUAL_DASH, Animations.DAGGER_DUAL_AIR_SLASH)
			.addStyleCombo(Style.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(Style.ONE_HAND, Skills.EVISCERATE)
			.addStyleSpecialAttack(Style.TWO_HAND, Skills.BLADE_RUSH)
		);
		return weaponCapability;
	};
	public static final Function<Item, CapabilityItem> BOW = BowCapability::new;
	public static final Function<Item, CapabilityItem> CROSSBOW = CrossbowCapability::new;
	public static final Function<Item, CapabilityItem> TRIDENT = TridentCapability::new;
	
	private static final Map<String, Function<Item, CapabilityItem>> TYPES = Maps.newHashMap();
	
	static {
		TYPES.put("axe", AXE);
		TYPES.put("fist", FIST);
		TYPES.put("hoe", HOE);
		TYPES.put("pickaxe", PICKAXE);
		TYPES.put("shovel", SHOVEL);
		TYPES.put("sword", SWORD);
		TYPES.put("spear", SPEAR);
		TYPES.put("greatsword", GREATSWORD);
		TYPES.put("katana", KATANA);
		TYPES.put("tachi", TACHI);
		TYPES.put("longsword", LONGSWORD);
		TYPES.put("dagger", DAGGER);
		TYPES.put("bow", BOW);
		TYPES.put("crossbow", CROSSBOW);
		TYPES.put("trident", TRIDENT);
	}
	
	public static Function<Item, CapabilityItem> get(String typeName) {
		return TYPES.get(typeName);
	}
}