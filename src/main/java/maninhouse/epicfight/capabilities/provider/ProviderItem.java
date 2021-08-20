package maninhouse.epicfight.capabilities.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.item.*;
import maninhouse.epicfight.config.CapabilityConfig;
import maninhouse.epicfight.config.CapabilityConfig.CustomArmorConfig;
import maninhouse.epicfight.config.CapabilityConfig.CustomWeaponConfig;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.ForgeRegistries;

public class ProviderItem implements ICapabilityProvider, NonNullSupplier<CapabilityItem> {
	private static final Map<Item, CapabilityItem> CAPABILITY_BY_INSTANCE = new HashMap<Item, CapabilityItem> ();
	private static final Map<Class<? extends Item>, Function<Item, CapabilityItem>> CAPABILITY_BY_CLASS = new HashMap<Class<? extends Item>, Function<Item, CapabilityItem>> ();
	
	public static void makeMap() {
		addConfigItems();
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.WOODEN_AXE, AxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.STONE_AXE, AxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.IRON_AXE, AxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.GOLDEN_AXE, AxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.DIAMOND_AXE, AxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.NETHERITE_AXE, AxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.WOODEN_PICKAXE, PickaxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.STONE_PICKAXE, PickaxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.IRON_PICKAXE, PickaxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.GOLDEN_PICKAXE, PickaxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.DIAMOND_PICKAXE, PickaxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.NETHERITE_PICKAXE, PickaxeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.WOODEN_HOE, HoeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.STONE_HOE, HoeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.IRON_HOE, HoeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.GOLDEN_HOE, HoeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.DIAMOND_HOE, HoeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.NETHERITE_HOE, HoeCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.WOODEN_SHOVEL, ShovelCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.STONE_SHOVEL, ShovelCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.IRON_SHOVEL, ShovelCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.GOLDEN_SHOVEL, ShovelCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.DIAMOND_SHOVEL, ShovelCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.NETHERITE_SHOVEL, ShovelCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.WOODEN_SWORD, SwordCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.STONE_SWORD, SwordCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.IRON_SWORD, SwordCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.GOLDEN_SWORD, SwordCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.DIAMOND_SWORD, SwordCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.NETHERITE_SWORD, SwordCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.LEATHER_BOOTS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.LEATHER_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.LEATHER_HELMET, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.LEATHER_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.GOLDEN_BOOTS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.GOLDEN_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.GOLDEN_HELMET, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.GOLDEN_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.CHAINMAIL_BOOTS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.CHAINMAIL_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.CHAINMAIL_HELMET, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.CHAINMAIL_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.IRON_BOOTS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.IRON_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.IRON_HELMET, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.IRON_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.DIAMOND_BOOTS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.DIAMOND_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.DIAMOND_HELMET, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.DIAMOND_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.NETHERITE_BOOTS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.NETHERITE_CHESTPLATE, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.NETHERITE_HELMET, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.NETHERITE_LEGGINGS, VanillaArmorCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.BOW, BowCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.CROSSBOW, CrossbowCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.TRIDENT, TridentCapability::new);
		CAPABILITY_BY_INSTANCE.computeIfAbsent(Items.SHIELD, ShieldCapability ::new);
		
		CAPABILITY_BY_CLASS.put(ArmorItem.class, ArmorCapability::new);
		CAPABILITY_BY_CLASS.put(SwordItem.class, SwordCapability::new);
		CAPABILITY_BY_CLASS.put(PickaxeItem.class, PickaxeCapability::new);
		CAPABILITY_BY_CLASS.put(AxeItem.class, AxeCapability::new);
		CAPABILITY_BY_CLASS.put(ShovelItem.class, ShovelCapability::new);
		CAPABILITY_BY_CLASS.put(HoeItem.class, HoeCapability::new);
		CAPABILITY_BY_CLASS.put(BowItem.class, BowCapability::new);
		CAPABILITY_BY_CLASS.put(CrossbowItem.class, CrossbowCapability::new);
	}
	
	public static void addInstance(Item item, CapabilityItem cap) {
		CAPABILITY_BY_INSTANCE.put(item, cap);
	}
	
	public static void addConfigItems() {
		for (CustomWeaponConfig config : CapabilityConfig.CUSTOM_WEAPON_LIST) {
			ResourceLocation key = new ResourceLocation(config.getRegistryName());
			if (ForgeRegistries.ITEMS.containsKey(key)) {
				Item item = ForgeRegistries.ITEMS.getValue(key);
				EpicFightMod.LOGGER.info("Register Custom Capaiblity for " + config.getRegistryName());
				CapabilityItem cap = config.getWeaponType().get(item);
				cap.setCustomWeapon(config.getArmorIgnoranceOnehand(), config.getImpactOnehand(), config.getMaxStrikesOnehand(),
						config.getArmorIgnoranceTwohand(), config.getImpactTwohand(), config.getMaxStrikesTwohand());
				CAPABILITY_BY_INSTANCE.put(item, cap);
			} else {
				EpicFightMod.LOGGER.warn("Failed to load custom item " + config.getRegistryName() + ". Item not exist!");
			}
		}
		
		for (CustomArmorConfig config : CapabilityConfig.CUSTOM_ARMOR_LIST) {
			ResourceLocation key = new ResourceLocation(config.getRegistryName());
			if (ForgeRegistries.ITEMS.containsKey(key)) {
				Item item = ForgeRegistries.ITEMS.getValue(key);
				if (item instanceof ArmorItem) {
					ArmorCapability cap = new ArmorCapability(item, config.getWeight(), config.getStunArmor());
					CAPABILITY_BY_INSTANCE.put(item, cap);
					EpicFightMod.LOGGER.info("Register Custom Capaiblity for " + config.getRegistryName());
				} else {
					if (item == null) {
						EpicFightMod.LOGGER.warn("Failed to load custom item " + config.getRegistryName() + ". Item not exist!");
					} else if (!(item instanceof ArmorItem)) {
						EpicFightMod.LOGGER.warn("Failed to load custom item " + config.getRegistryName() + ". Item is not armor!");
					}
				}
			}
		}
	}
	
	private CapabilityItem capability;
	private LazyOptional<CapabilityItem> optional = LazyOptional.of(this);
	
	public ProviderItem(Item item, boolean autogenerate) {
		this.capability = CAPABILITY_BY_INSTANCE.get(item);
		if (this.capability == null && autogenerate) {
			this.capability = this.makeCustomCapability(item);
			if(this.capability != null) {
				CAPABILITY_BY_INSTANCE.put(item, this.capability);
			}
		}
	}
	
	public boolean hasCapability() {
		return this.capability != null;
	}
	
	private CapabilityItem makeCustomCapability(Item item) {
		Class<?> clazz = item.getClass();
		CapabilityItem cap = null;
		for (; clazz != null && cap == null; clazz = clazz.getSuperclass()) {
			cap = CAPABILITY_BY_CLASS.getOrDefault(clazz, (argIn) -> null).apply(item);
		}
		return cap;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == ModCapabilities.CAPABILITY_ITEM ? optional.cast() : LazyOptional.empty();
	}

	@Override
	public CapabilityItem get() {
		return capability;
	}
}