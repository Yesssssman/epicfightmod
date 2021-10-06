package yesman.epicfight.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import yesman.epicfight.capabilities.entity.CapabilityEntity;
import yesman.epicfight.capabilities.entity.mob.CreeperData;
import yesman.epicfight.capabilities.entity.mob.SkeletonData;
import yesman.epicfight.capabilities.entity.mob.SpiderData;
import yesman.epicfight.capabilities.entity.mob.VindicatorData;
import yesman.epicfight.capabilities.entity.mob.ZombieData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.item.DefinedWeaponTypes;

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
		AXE(DefinedWeaponTypes.AXE),
		FIST(DefinedWeaponTypes.FIST),
		HOE(DefinedWeaponTypes.HOE),
		PICKAXE(DefinedWeaponTypes.PICKAXE),
		SHOVEL(DefinedWeaponTypes.SHOVEL),
		SWORD(DefinedWeaponTypes.SWORD),
		SPEAR(DefinedWeaponTypes.SPEAR),
		GREATSWORD(DefinedWeaponTypes.GREATSWORD),
		KATANA(DefinedWeaponTypes.KATANA),
		TACHI(DefinedWeaponTypes.TACHI),
		LONGSWORD(DefinedWeaponTypes.LONGSWORD),
		DAGGER(DefinedWeaponTypes.DAGGER),
		BOW(DefinedWeaponTypes.BOW),
		CROSSBOW(DefinedWeaponTypes.CROSSBOW),
		TRIDENT(DefinedWeaponTypes.TRIDENT);
		
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