package maninthehouse.epicfight.config;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.capabilities.item.CapabilityItem.HandProperty;
import maninthehouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninthehouse.epicfight.capabilities.item.CapabilityItem.WieldStyle;
import maninthehouse.epicfight.capabilities.item.ModWeaponCapability;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Colliders;
import maninthehouse.epicfight.gamedata.Skills;
import maninthehouse.epicfight.gamedata.Sounds;
import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;

@Config(modid = EpicFightMod.MODID, name = EpicFightMod.MODID, category="custom")
public class ConfigurationCapability {
	@Name("custom_weaponry")
	public static CustomWeaponConfig weaponConfig = new CustomWeaponConfig();
	@Name("custom_armor")
	public static CustomArmorConfig armorConfig = new CustomArmorConfig();
	
	public static class CustomArmorConfig {
		@Name("sample_armor")
		public ArmorConfig sampleArmor = new ArmorConfig();
	}
	
	public static class CustomWeaponConfig {
		@Name("sample_weapon1")
		public SampleSimpleWeapon sampleWeapon1 = new SampleSimpleWeapon();
		@Name("sample_weapon2")
		public WeaponConfig sampleWeapon2 = new WeaponConfig();
	}
	
	public static class ArmorConfig {
		@Name("registry_name")
		public String registryName = "modid:registryname";
		@Name("stun_armor")
		public double stunArmor = 0.0D;
		@Name("weight")
		public double weight = 0.0D;
	}
	
	public static class SampleSimpleWeapon {
		@Name("registry_name")
		public String registryName = "modid:registryname";
		@Name("armor_negation")
		public double armorNegation = 0.0D;
		@Name("max_strikes")
		public int makStrikes = 1;
		@Name("impact")
		public double impact = 0.5D;
		@Name("weapon_type")
		public WeaponType weaponType = WeaponType.SWORD;
	}
	
	public static class WeaponConfig {
		@Name("registry_name")
		public String registryName = "modid:registryname";
		@Name("onehand")
		public Hand onehand = new Hand();
		@Name("twohand")
		public Hand twohand = new Hand();
		@Name("weapon_type")
		public WeaponType weaponType = WeaponType.SWORD;
	}
	
	public static class Hand {
		@Name("armor_negation")
		public double armorNegation = 0.0D;
		@Name("max_strikes")
		public int maxStrikes = 1;
		@Name("impact")
		public double impact = 0.5D;
	}
	
	public static enum WeaponType {
		AXE(()->{
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.AXE, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLADE_HIT,
					Colliders.tools, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			cap.addStyleSpecialAttack(WieldStyle.ONE_HAND, Skills.GUILLOTINE_AXE);
			return cap;
		}),
		FIST(()->{
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.FIST, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLUNT_HIT,
					Colliders.fist, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.FIST_AUTO_1, Animations.FIST_AUTO_2, Animations.FIST_AUTO_3, Animations.FIST_DASH);
			return cap;
		}),
		HOE(()->{
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.HOE, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLADE_HIT,
					Colliders.tools, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.TOOL_AUTO_1, Animations.TOOL_AUTO_2, Animations.TOOL_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			return cap;
		}),
		PICKAXE(()->{
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.PICKAXE, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLADE_HIT,
					Colliders.tools, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			return cap;
		}),
		SHOVEL(()->{
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.SHOVEL, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLUNT_HIT,
					Colliders.tools, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			return cap;
		}),
		SWORD(()->{
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.SWORD, (playerdata)->{
				CapabilityItem item = playerdata.getHeldItemCapability(EnumHand.OFF_HAND);
				if(item != null && item.getWeaponCategory() == WeaponCategory.SWORD) {
					return WieldStyle.TWO_HAND;
				} else {
					return WieldStyle.ONE_HAND;
				}
			}, null, Sounds.WHOOSH, Sounds.BLADE_HIT, Colliders.sword, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.SWORD_AUTO_1, Animations.SWORD_AUTO_2, Animations.SWORD_AUTO_3, Animations.SWORD_DASH);
			cap.addStyleCombo(WieldStyle.TWO_HAND, Animations.SWORD_DUAL_AUTO_1, Animations.SWORD_DUAL_AUTO_2, Animations.SWORD_DUAL_AUTO_3, Animations.SWORD_DUAL_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			cap.addStyleSpecialAttack(WieldStyle.ONE_HAND, Skills.SWEEPING_EDGE);
			cap.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.DANCING_EDGE);
			return cap;
		}),
		SPEAR(()->{
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.SPEAR, (playerdata)-> 
				playerdata.getOriginalEntity().getHeldItemOffhand().isEmpty() ? WieldStyle.TWO_HAND : WieldStyle.ONE_HAND,
				null, Sounds.WHOOSH, Sounds.BLADE_HIT, Colliders.spearNarrow, HandProperty.MAINHAND_ONLY);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_DASH);
			cap.addStyleCombo(WieldStyle.TWO_HAND, Animations.SPEAR_TWOHAND_AUTO_1, Animations.SPEAR_TWOHAND_AUTO_2, Animations.SPEAR_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SPEAR_MOUNT_ATTACK);
			cap.addStyleSpecialAttack(WieldStyle.ONE_HAND, Skills.HEARTPIERCER);
			cap.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.SLAUGHTER_STANCE);
			cap.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_HELDING_WEAPON);
			return cap;
		}),
		GREATSWORD(()->{
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.GREATSWORD, (playerdata)->WieldStyle.TWO_HAND, null, Sounds.WHOOSH_BIG, Sounds.BLADE_HIT,
					Colliders.greatSword, HandProperty.TWO_HANDED);
			cap.addStyleCombo(WieldStyle.TWO_HAND, Animations.GREATSWORD_AUTO_1, Animations.GREATSWORD_AUTO_2, Animations.GREATSWORD_DASH);
			cap.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.GIANT_WHIRLWIND);
			cap.addLivingMotionChanger(LivingMotion.IDLE, Animations.BIPED_IDLE_MASSIVE_HELD);
			cap.addLivingMotionChanger(LivingMotion.WALKING, Animations.BIPED_WALK_MASSIVE_HELD);
			cap.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_MASSIVE_HELD);
	    	cap.addLivingMotionChanger(LivingMotion.JUMPING, Animations.BIPED_JUMP_MASSIVE_HELD);
	    	cap.addLivingMotionChanger(LivingMotion.KNEELING, Animations.BIPED_KNEEL_MASSIVE_HELD);
	    	cap.addLivingMotionChanger(LivingMotion.SNEAKING, Animations.BIPED_SNEAK_MASSIVE_HELD);
			return cap;
		}),
		KATANA(()->{
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.KATANA, (playerdata)->WieldStyle.TWO_HAND, null, Sounds.WHOOSH, Sounds.BLADE_HIT,
					Colliders.katana, HandProperty.TWO_HANDED);
			cap.addStyleCombo(WieldStyle.TWO_HAND, Animations.KATANA_AUTO_1, Animations.KATANA_AUTO_2, Animations.KATANA_AUTO_3, Animations.SWORD_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			cap.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.SWEEPING_EDGE);
			cap.addLivingMotionChanger(LivingMotion.IDLE, Animations.BIPED_IDLE_UNSHEATHING);
			cap.addLivingMotionChanger(LivingMotion.WALKING, Animations.BIPED_WALK_UNSHEATHING);
			cap.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_UNSHEATHING);
			return cap;
		});
		
		Supplier<ModWeaponCapability> capabilitySupplier;
		
		private WeaponType(Supplier<ModWeaponCapability> capabilitySupplier) {
			this.capabilitySupplier = capabilitySupplier;
		}

		public ModWeaponCapability get() {
			return this.capabilitySupplier.get();
		}
		
		static Map<String, WeaponType> searchByName = Maps.<String, WeaponType>newHashMap();
		
		static {
			searchByName.put("AXE", WeaponType.AXE);
			searchByName.put("FIST", WeaponType.FIST);
			searchByName.put("HOE", WeaponType.HOE);
			searchByName.put("PICKAXE", WeaponType.PICKAXE);
			searchByName.put("SHOVEL", WeaponType.SHOVEL);
			searchByName.put("SWORD", WeaponType.SWORD);
			searchByName.put("SPEAR", WeaponType.SPEAR);
			searchByName.put("GREATSWORD", WeaponType.GREATSWORD);
			searchByName.put("KATANA", WeaponType.KATANA);
		}
		
		public static WeaponType findByName(String name) {
			return searchByName.get(name);
		}
	}
	
	public static List<WeaponConfig> getWeaponConfigs() {
		List<WeaponConfig> list = Lists.<WeaponConfig>newArrayList();
		IConfigElement root = ConfigElement.from(ConfigurationCapability.class);
		IConfigElement weapons = getElementByName(root.getChildElements(), "custom_weaponry");
		
		for (IConfigElement configElement : weapons.getChildElements()) {
			List<IConfigElement> childElements = configElement.getChildElements();
			WeaponConfig config = new WeaponConfig();
			config.registryName = (String) getElementByName(childElements, "registry_name").get();
			config.weaponType = WeaponType.findByName((String) getElementByName(childElements, "weapon_type").get());
			IConfigElement onehandConfig = getElementByName(childElements, "onehand");
			IConfigElement twohandConfig = getElementByName(childElements, "twohand");
			
			if (onehandConfig != null || twohandConfig != null) {
				if(onehandConfig != null) {
					List<IConfigElement> onehandAttributes = onehandConfig.getChildElements();
					config.onehand.armorNegation = Double.parseDouble((String)getElementByName(onehandAttributes, "armor_negation").get());
					config.onehand.maxStrikes = Integer.parseInt((String)getElementByName(onehandAttributes, "max_strikes").get());
					config.onehand.impact = Double.parseDouble((String)getElementByName(onehandAttributes, "impact").get());
				}
				if(twohandConfig != null) {
					List<IConfigElement> twohandAttributes = twohandConfig.getChildElements();
					config.twohand.armorNegation = Double.parseDouble((String)getElementByName(twohandAttributes, "armor_negation").get());
					config.twohand.maxStrikes = Integer.parseInt((String)getElementByName(twohandAttributes, "max_strikes").get());
					config.twohand.impact = Double.parseDouble((String)getElementByName(twohandAttributes, "impact").get());
				}
			} else {
				config.onehand.armorNegation = Double.parseDouble((String)getElementByName(childElements, "armor_negation").get());
				config.onehand.maxStrikes = Integer.parseInt((String)getElementByName(childElements, "max_strikes").get());
				config.onehand.impact = Double.parseDouble((String)getElementByName(childElements, "impact").get());
				config.twohand.armorNegation = config.onehand.armorNegation;
				config.twohand.maxStrikes = config.onehand.maxStrikes;
				config.twohand.impact = config.onehand.impact;
			}
			
			list.add(config);
		}
		
		return list;
	}
	
	public static List<ArmorConfig> getArmorConfigs() {
		List<ArmorConfig> list = Lists.<ArmorConfig>newArrayList();
		IConfigElement root = ConfigElement.from(ConfigurationCapability.class);
		IConfigElement armors = getElementByName(root.getChildElements(), "custom_armor");
		
		for (IConfigElement configElement : armors.getChildElements()) {
			List<IConfigElement> childElements = configElement.getChildElements();
			ArmorConfig config = new ArmorConfig();
			config.registryName = (String) getElementByName(childElements, "registry_name").get();
			config.stunArmor = Double.parseDouble((String)getElementByName(childElements, "stun_armor").get());
			config.weight = Double.parseDouble((String)getElementByName(childElements, "weight").get());
			list.add(config);
		}
		
		return list;
	}
	
	public static IConfigElement getElementByName(List<IConfigElement> configElements, String name) {
		for (IConfigElement element : configElements) {
			if (element.getName().equals(name)) {
				return element;
			}
		}
		
		return null;
	}
}