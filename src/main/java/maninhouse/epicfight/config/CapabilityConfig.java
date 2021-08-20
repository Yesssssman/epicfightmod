package maninhouse.epicfight.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.entity.CapabilityEntity;
import maninhouse.epicfight.capabilities.entity.mob.CreeperData;
import maninhouse.epicfight.capabilities.entity.mob.SkeletonData;
import maninhouse.epicfight.capabilities.entity.mob.SpiderData;
import maninhouse.epicfight.capabilities.entity.mob.VindicatorData;
import maninhouse.epicfight.capabilities.entity.mob.ZombieData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.item.BowCapability;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldOption;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldStyle;
import maninhouse.epicfight.capabilities.item.CrossbowCapability;
import maninhouse.epicfight.capabilities.item.KnuckleCapability;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.skill.SkillCategory;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CapabilityConfig {
	public static final List<CustomWeaponConfig> CUSTOM_WEAPON_LIST = Lists.<CustomWeaponConfig>newArrayList();
	public static final List<CustomArmorConfig> CUSTOM_ARMOR_LIST = Lists.<CustomArmorConfig>newArrayList();
	public static final List<CustomEntityConfig> CUSTOM_ENTITY_LIST = Lists.<CustomEntityConfig>newArrayList();
	public static final Map<ResourceLocation, CustomEntityConfig> CUSTOM_ENTITY_MAP = Maps.<ResourceLocation, CustomEntityConfig>newHashMap();
	
	public static void init(ForgeConfigSpec.Builder config, Map<String, Object> dynamicConfigMap) {
		String weaponKey = "custom_weaponry";
		
		if (dynamicConfigMap.get(weaponKey) != null) {
			List<Map.Entry<String, Object>> entries = new LinkedList<>(((AbstractCommentedConfig)dynamicConfigMap.get(weaponKey)).valueMap().entrySet());
		    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
		    
			for (Map.Entry<String, Object> entry : entries) {
				ConfigValue<String> registryName = config.define(String.format("%s.%s.registry_name", weaponKey, entry.getKey()), "broken_item");
				ConfigValue<WeaponType> weaponType = config.defineEnum(String.format("%s.%s.weapon_type", weaponKey, entry.getKey()), WeaponType.SWORD);
				ConfigValue<Double> impactOnehand;
				ConfigValue<Double> armorNegationOnehand;
				ConfigValue<Integer> maxStrikesOnehand;
				ConfigValue<Double> impactTwohand;
				ConfigValue<Double> armorNegationTwohand;
				ConfigValue<Integer> maxStrikesTwohand;
				
				boolean containOnehand = ((AbstractCommentedConfig)entry.getValue()).contains("onehand");
				boolean containTwohand = ((AbstractCommentedConfig)entry.getValue()).contains("twohand");
				
				if (!(containOnehand || containTwohand)) {
					impactOnehand = config.define(String.format("%s.%s.impact", weaponKey, entry.getKey()), 0.5D);
					armorNegationOnehand = config.define(String.format("%s.%s.armor_ignorance", weaponKey, entry.getKey()), 0.0D);
					maxStrikesOnehand = config.define(String.format("%s.%s.hit_at_once", weaponKey, entry.getKey()), 1);
					impactTwohand = config.define(String.format("%s.%s.impact", weaponKey, entry.getKey()), 0.5D);
					armorNegationTwohand = config.define(String.format("%s.%s.armor_ignorance", weaponKey, entry.getKey()), 0.0D);
					maxStrikesTwohand = config.define(String.format("%s.%s.hit_at_once", weaponKey, entry.getKey()), 1);
				} else {
					impactOnehand = config.define(String.format("%s.%s.onehand.impact", weaponKey, entry.getKey()), 0.5D);
					armorNegationOnehand = config.define(String.format("%s.%s.onehand.armor_ignorance", weaponKey, entry.getKey()), 0.0D);
					maxStrikesOnehand = config.define(String.format("%s.%s.onehand.hit_at_once", weaponKey, entry.getKey()), 1);
					impactTwohand = config.define(String.format("%s.%s.twohand.impact", weaponKey, entry.getKey()), 0.5D);
					armorNegationTwohand = config.define(String.format("%s.%s.twohand.armor_ignorance", weaponKey, entry.getKey()), 0.0D);
					maxStrikesTwohand = config.define(String.format("%s.%s.twohand.hit_at_once", weaponKey, entry.getKey()), 1);
				}
				
				if(!entry.getKey().equals("sample")) {
					CUSTOM_WEAPON_LIST.add(new CustomWeaponConfig(
							registryName, weaponType, impactOnehand, armorNegationOnehand, maxStrikesOnehand, impactTwohand, armorNegationTwohand, maxStrikesTwohand
					));
				}
			}
		}
		
		String armorKey = "custom_armor";
		if (dynamicConfigMap.get(armorKey) != null) {
			List<Map.Entry<String, Object>> entries = new LinkedList<>(((AbstractCommentedConfig)dynamicConfigMap.get(armorKey)).valueMap().entrySet());
		    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
			for (Map.Entry<String, Object> entry : entries) {
				ConfigValue<String> registryName = config.define(String.format("%s.%s.registry_name", armorKey, entry.getKey()), "broken_item");
				ConfigValue<Double> weight = config.define(String.format("%s.%s.weight", armorKey, entry.getKey()), 0.0D);
				ConfigValue<Double> stunArmor = config.define(String.format("%s.%s.stun_armor", armorKey, entry.getKey()), 0.0D);
				
				if(!entry.getKey().equals("sample")) {
					CUSTOM_ARMOR_LIST.add(new CustomArmorConfig(registryName, stunArmor, weight));
				}
			}
		}
		
		String entityKey = "custom_entity";
		if (dynamicConfigMap.get(entityKey) != null) {
			List<Map.Entry<String, Object>> entries = new LinkedList<>(((AbstractCommentedConfig)dynamicConfigMap.get(entityKey)).valueMap().entrySet());
		    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
			for (Map.Entry<String, Object> entry : entries) {
				ConfigValue<String> entityType = config.define(String.format("%s.%s.type_name", entityKey, entry.getKey()), "broken_entity");
				ConfigValue<String> entityTextureLocation = config.define(String.format("%s.%s.texture_location", entityKey, entry.getKey()), "broken_entity_texture");
				ConfigValue<EntityAI> entityAI = config.defineEnum(String.format("%s.%s.ai_type", entityKey, entry.getKey()), EntityAI.ZOMBIE);
				ConfigValue<Double> impact = config.define(String.format("%s.%s.impact", entityKey, entry.getKey()), 0.5D);
				ConfigValue<Double> armorNegation = config.define(String.format("%s.%s.armor_negation", entityKey, entry.getKey()), 0.0D);
				ConfigValue<Integer> maxStrikes = config.define(String.format("%s.%s.max_strikes", entityKey, entry.getKey()), 1);
				
				if(!entry.getKey().equals("sample")) {
					CUSTOM_ENTITY_LIST.add(new CustomEntityConfig(entityType, entityTextureLocation, entityAI, impact, armorNegation, maxStrikes));
				}
			}
		}
	}
	
	public static void buildEntityMap() {
		for (CustomEntityConfig config : CUSTOM_ENTITY_LIST) {
			CUSTOM_ENTITY_MAP.put(new ResourceLocation(config.entityTypeName.get()), config);
		}
	}
	
	public static class CustomArmorConfig {
		private ConfigValue<String> registryName;
		private ConfigValue<Double> stunArmor;
		private ConfigValue<Double> weight;
		
		public CustomArmorConfig(ConfigValue<String> registryName, ConfigValue<Double> stunArmor, ConfigValue<Double> weight) {
			this.registryName = registryName;
			this.stunArmor = stunArmor;
			this.weight = weight;
		}
		
		public String getRegistryName() {
			return this.registryName.get();
		}
		
		public Double getStunArmor() {
			return this.stunArmor.get();
		}
		
		public Double getWeight() {
			return this.weight.get();
		}
	}
	
	public static class CustomWeaponConfig {
		private ConfigValue<String> registryName;
		private ConfigValue<WeaponType> weaponType;
		private ConfigValue<Double> impactOnehand;
		private ConfigValue<Double> armorIgnoranceOnehand;
		private ConfigValue<Integer> hitAtOnceOnehand;
		private ConfigValue<Double> impactTwohand;
		private ConfigValue<Double> armorIgnoranceTwohand;
		private ConfigValue<Integer> hitAtOnceTwohand;
		
		public CustomWeaponConfig(ConfigValue<String> registryName, ConfigValue<WeaponType> weaponType,
				ConfigValue<Double> impactOnehand, ConfigValue<Double> armorIgnoranceOnehand, ConfigValue<Integer> hitAtOnceOnehand,
				ConfigValue<Double> impactTwohand, ConfigValue<Double> armorIgnoranceTwohand, ConfigValue<Integer> hitAtOnceTwohand) {
			this.registryName = registryName;
			this.weaponType = weaponType;
			this.impactOnehand = impactOnehand;
			this.armorIgnoranceOnehand = armorIgnoranceOnehand;
			this.hitAtOnceOnehand = hitAtOnceOnehand;
			this.impactTwohand = impactTwohand;
			this.armorIgnoranceTwohand = armorIgnoranceTwohand;
			this.hitAtOnceTwohand = hitAtOnceTwohand;
		}
		
		public String getRegistryName() {
			return this.registryName.get();
		}

		public WeaponType getWeaponType() {
			return this.weaponType.get();
		}

		public Double getImpactOnehand() {
			return this.impactOnehand.get();
		}

		public Double getArmorIgnoranceOnehand() {
			return this.armorIgnoranceOnehand.get();
		}

		public Integer getMaxStrikesOnehand() {
			return this.hitAtOnceOnehand.get();
		}

		public Double getImpactTwohand() {
			return this.impactTwohand.get();
		}
		
		public Double getArmorIgnoranceTwohand() {
			return this.armorIgnoranceTwohand.get();
		}

		public Integer getMaxStrikesTwohand() {
			return this.hitAtOnceTwohand.get();
		}
	}
	
	public static class CustomEntityConfig {
		private ConfigValue<String> entityTypeName;
		private ConfigValue<String> entityTextureLocation;
		private ConfigValue<EntityAI> entityAIType;
		private ConfigValue<Double> impact;
		private ConfigValue<Double> armorNegation;
		private ConfigValue<Integer> makStrikes;
		
		public CustomEntityConfig(ConfigValue<String> entityTypeName, ConfigValue<String> entityTextureLocation, ConfigValue<EntityAI> entityAIType,
				ConfigValue<Double> impact, ConfigValue<Double> armorNegation, ConfigValue<Integer> maxStrikes) {
			this.entityTypeName = entityTypeName;
			this.entityTextureLocation = entityTextureLocation;
			this.entityAIType = entityAIType;
			this.impact = impact;
			this.armorNegation = armorNegation;
			this.makStrikes = maxStrikes;
		}
		
		public String getEntityTypeName() {
			return this.entityTypeName.get();
		}
		
		public String getEntityTextureLocation() {
			return this.entityTextureLocation.get();
		}
		
		public EntityAI getEntityAIType() {
			return this.entityAIType.get();
		}
		
		public Double getImpact() {
			return this.impact.get();
		}
		
		public Double getArmorNegation() {
			return this.armorNegation.get();
		}
		
		public Integer getMaxStrikes() {
			return this.makStrikes.get();
		}
	}
	
	public static enum WeaponType {
		AXE((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(new ModWeaponCapability.Builder()
				.setCategory(WeaponCategory.AXE)
				.setHitSound(Sounds.BLADE_HIT)
				.setWeaponCollider(Colliders.tools)
				.addStyleCombo(HoldStyle.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
				.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.addStyleSpecialAttack(HoldStyle.ONE_HAND, Skills.GUILLOTINE_AXE)
			);
			return cap;
		}),
		FIST((item) -> new KnuckleCapability()),
		HOE((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(new ModWeaponCapability.Builder()
				.setCategory(WeaponCategory.HOE)
				.setHitSound(Sounds.BLADE_HIT)
				.setWeaponCollider(Colliders.tools)
				.addStyleCombo(HoldStyle.ONE_HAND, Animations.TOOL_AUTO_1, Animations.TOOL_AUTO_2, Animations.TOOL_DASH, Animations.SWORD_AIR_SLASH)
				.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			);
			return cap;
		}),
		PICKAXE((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(new ModWeaponCapability.Builder()
					.setCategory(WeaponCategory.PICKAXE)
					.setHitSound(Sounds.BLADE_HIT)
					.setWeaponCollider(Colliders.tools)
					.addStyleCombo(HoldStyle.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
					.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			);
			return cap;
		}),
		SHOVEL((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(new ModWeaponCapability.Builder()
					.setCategory(WeaponCategory.SHOVEL)
					.setWeaponCollider(Colliders.tools)
					.addStyleCombo(HoldStyle.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH, Animations.AXE_AIRSLASH)
					.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			);
			return cap;
		}),
		SWORD((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(new ModWeaponCapability.Builder()
				.setCategory(WeaponCategory.SWORD)
				.setStyleGetter((playerdata) -> playerdata.getHeldItemCapability(Hand.OFF_HAND).getWeaponCategory() == WeaponCategory.SWORD ? HoldStyle.TWO_HAND : HoldStyle.ONE_HAND)
				.setWeaponCollider(Colliders.sword)
				.setHitSound(Sounds.BLADE_HIT)
				.addStyleCombo(HoldStyle.ONE_HAND, Animations.SWORD_AUTO_1, Animations.SWORD_AUTO_2, Animations.SWORD_AUTO_3, Animations.SWORD_DASH)
				.addStyleCombo(HoldStyle.TWO_HAND, Animations.SWORD_DUAL_AUTO_1, Animations.SWORD_DUAL_AUTO_2, Animations.SWORD_DUAL_AUTO_3, Animations.SWORD_DUAL_DASH)
				.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.addStyleSpecialAttack(HoldStyle.ONE_HAND, Skills.SWEEPING_EDGE)
				.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.DANCING_EDGE)
			);
			return cap;
		}),
		SPEAR((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(new ModWeaponCapability.Builder()
				.setCategory(WeaponCategory.SPEAR)
				.setStyleGetter((playerdata) -> playerdata.getOriginalEntity().getHeldItemOffhand().isEmpty() ? HoldStyle.TWO_HAND : HoldStyle.ONE_HAND)
				.setWeaponCollider(Colliders.spear)
				.setHitSound(Sounds.BLADE_HIT)
				.setHoldOption(HoldOption.MAINHAND_ONLY)
				.addStyleCombo(HoldStyle.ONE_HAND, Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_DASH)
				.addStyleCombo(HoldStyle.TWO_HAND, Animations.SPEAR_TWOHAND_AUTO_1, Animations.SPEAR_TWOHAND_AUTO_2, Animations.SPEAR_DASH)
				.addStyleCombo(HoldStyle.MOUNT, Animations.SPEAR_MOUNT_ATTACK)
				.addStyleSpecialAttack(HoldStyle.ONE_HAND, Skills.HEARTPIERCER)
				.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.SLAUGHTER_STANCE)
				.addLivingMotionModifier(HoldStyle.ONE_HAND, LivingMotion.RUN, Animations.BIPED_RUN_SPEAR)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_RUN_SPEAR)
			);
			return cap;
		}),
		GREATSWORD((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(new ModWeaponCapability.Builder()
				.setCategory(WeaponCategory.GREATSWORD)
				.setStyleGetter((playerdata) -> HoldStyle.TWO_HAND)
				.setWeaponCollider(Colliders.greatSword)
				.setSmashingSound(Sounds.WHOOSH_BIG)
				.setHitSound(Sounds.BLADE_HIT)
				.setHoldOption(HoldOption.TWO_HANDED)
				.addStyleCombo(HoldStyle.TWO_HAND, Animations.GREATSWORD_AUTO_1, Animations.GREATSWORD_AUTO_2, Animations.GREATSWORD_DASH)
				.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.GIANT_WHIRLWIND)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_GREATSWORD)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.WALK, Animations.BIPED_IDLE_GREATSWORD)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_IDLE_GREATSWORD)
	    		.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.JUMP, Animations.BIPED_IDLE_GREATSWORD)
	    		.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_GREATSWORD)
	    		.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_IDLE_GREATSWORD)
			);
			return cap;
		}),
		KATANA((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(new ModWeaponCapability.Builder()
				.setCategory(WeaponCategory.KATANA)
				.setStyleGetter((playerdata) -> HoldStyle.TWO_HAND)
				.setPassiveSkill(Skills.KATANA_PASSIVE)
				.setHitSound(Sounds.BLADE_HIT)
				.setWeaponCollider(Colliders.katana)
				.setHoldOption(HoldOption.TWO_HANDED)
				.addStyleCombo(HoldStyle.SHEATH, Animations.KATANA_SHEATHING_AUTO, Animations.KATANA_SHEATHING_DASH, Animations.KATANA_SHEATH_AIR_SLASH)
				.addStyleCombo(HoldStyle.TWO_HAND, Animations.KATANA_AUTO_1, Animations.KATANA_AUTO_2, Animations.KATANA_AUTO_3, Animations.SWORD_DASH, Animations.KATANA_AIR_SLASH)
				.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.addStyleSpecialAttack(HoldStyle.SHEATH, Skills.FATAL_DRAW)
				.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.FATAL_DRAW)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_UNSHEATHING)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_UNSHEATHING)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.WALK, Animations.BIPED_WALK_UNSHEATHING)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_RUN_UNSHEATHING)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_WALK_UNSHEATHING)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_WALK_UNSHEATHING)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FLOAT, Animations.BIPED_WALK_UNSHEATHING)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FALL, Animations.BIPED_WALK_UNSHEATHING)
				.addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.IDLE, Animations.BIPED_IDLE_SHEATHING)
				.addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.KNEEL, Animations.BIPED_IDLE_SHEATHING_MIX)
				.addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.WALK, Animations.BIPED_MOVE_SHEATHING)
			    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.RUN, Animations.BIPED_MOVE_SHEATHING)
			    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.SNEAK, Animations.BIPED_MOVE_SHEATHING)
			    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.SWIM, Animations.BIPED_MOVE_SHEATHING)
			    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.FLOAT, Animations.BIPED_MOVE_SHEATHING)
			    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.FALL, Animations.BIPED_MOVE_SHEATHING)
			);
			return cap;
		}),
		TACHI((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(new ModWeaponCapability.Builder()
				.setCategory(WeaponCategory.TACHI)
				.setStyleGetter((playerdata) -> HoldStyle.TWO_HAND)
				.setWeaponCollider(Colliders.katana)
				.setHitSound(Sounds.BLADE_HIT)
				.setHoldOption(HoldOption.TWO_HANDED)
				.addStyleCombo(HoldStyle.TWO_HAND, Animations.LONGSWORD_AUTO_1, Animations.LONGSWORD_AUTO_2, Animations.LONGSWORD_AUTO_3, Animations.TACHI_DASH)
				.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.LETHAL_SLICING)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_TACHI)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_TACHI)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.WALK, Animations.BIPED_IDLE_TACHI)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_IDLE_TACHI)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_IDLE_TACHI)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_IDLE_TACHI)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FLOAT, Animations.BIPED_IDLE_TACHI)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FALL, Animations.BIPED_IDLE_TACHI)
			);
			return cap;
		}),
		KNUCKLE((item) -> new KnuckleCapability()),
		LONGSWORD((item) -> {
			ModWeaponCapability weaponCapability = new ModWeaponCapability(new ModWeaponCapability.Builder()
				.setCategory(WeaponCategory.LONGSWORD)
				.setStyleGetter((playerdata) -> {
					if (playerdata instanceof PlayerData<?>) {
						if (((PlayerData<?>)playerdata).getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK).getRemainDuration() > 0) {
							return HoldStyle.LIECHTENHAUER;
						}
					}
					return HoldStyle.TWO_HAND;
				})
				.setHitSound(Sounds.BLADE_HIT)
				.setWeaponCollider(Colliders.longsword)
				.setHoldOption(HoldOption.TWO_HANDED)
				.addStyleCombo(HoldStyle.TWO_HAND, Animations.LONGSWORD_AUTO_1, Animations.LONGSWORD_AUTO_2, Animations.LONGSWORD_AUTO_3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
				.addStyleCombo(HoldStyle.LIECHTENHAUER, Animations.LONGSWORD_AUTO_1, Animations.LONGSWORD_AUTO_2, Animations.LONGSWORD_AUTO_3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
				.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.LIECHTENAUER)
				.addStyleSpecialAttack(HoldStyle.LIECHTENHAUER, Skills.LIECHTENAUER)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_GREATSWORD)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.WALK, Animations.BIPED_IDLE_GREATSWORD)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_IDLE_GREATSWORD)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_IDLE_GREATSWORD)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_GREATSWORD)
				.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.JUMP, Animations.BIPED_IDLE_GREATSWORD)
				.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.IDLE, Animations.BIPED_IDLE_LONGSWORD)
				.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.WALK, Animations.BIPED_WALK_LONGSWORD)
				.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.RUN, Animations.BIPED_WALK_LONGSWORD)
				.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.SNEAK, Animations.BIPED_WALK_LONGSWORD)
				.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.KNEEL, Animations.BIPED_WALK_LONGSWORD)
				.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.JUMP, Animations.BIPED_WALK_LONGSWORD)
			);
			return weaponCapability;
		}),
		DAGGER((item) -> {
			ModWeaponCapability weaponCapability = new ModWeaponCapability(new ModWeaponCapability.Builder()
				.setCategory(WeaponCategory.DAGGER)
				.setStyleGetter((playerdata) -> playerdata.getHeldItemCapability(Hand.OFF_HAND).getWeaponCategory() == WeaponCategory.DAGGER ? HoldStyle.TWO_HAND : HoldStyle.ONE_HAND)
				.setHitSound(Sounds.BLADE_HIT)
				.setWeaponCollider(Colliders.dagger)
				.addStyleCombo(HoldStyle.ONE_HAND, Animations.DAGGER_AUTO_1, Animations.DAGGER_AUTO_2, Animations.DAGGER_AUTO_3, Animations.SWORD_DASH, Animations.DAGGER_AIR_SLASH)
				.addStyleCombo(HoldStyle.TWO_HAND, Animations.DAGGER_DUAL_AUTO_1, Animations.DAGGER_DUAL_AUTO_2, Animations.DAGGER_DUAL_AUTO_3, Animations.DAGGER_DUAL_AUTO_4, Animations.DAGGER_DUAL_DASH, Animations.DAGGER_DUAL_AIR_SLASH)
				.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
				.addStyleSpecialAttack(HoldStyle.ONE_HAND, Skills.EVISCERATE)
				.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.BLADE_RUSH)
			);
			return weaponCapability;
		}),
		BOW(BowCapability::new),
		CROSSBOW(CrossbowCapability::new);
		
		Function<Item, CapabilityItem> capabilitySupplier;
		
		WeaponType(Function<Item, CapabilityItem> capabilitySupplier) {
			this.capabilitySupplier = capabilitySupplier;
		}
		
		public CapabilityItem get(Item item) {
			return this.capabilitySupplier.apply(item);
		}
	}
	
	public static enum EntityAI {
		ZOMBIE(ZombieData::new), SKELETON(SkeletonData::new), CREEPER(CreeperData::new), SPIDER(SpiderData::new), VINDICATOR(VindicatorData::new);
		
		Supplier<CapabilityEntity<?>> capabilitySupplier;
		
		EntityAI(Supplier<CapabilityEntity<?>> capabilitySupplier) {
			this.capabilitySupplier = capabilitySupplier;
		}
		
		public Supplier<CapabilityEntity<?>> getCapability() {
			return this.capabilitySupplier;
		}
	}
}