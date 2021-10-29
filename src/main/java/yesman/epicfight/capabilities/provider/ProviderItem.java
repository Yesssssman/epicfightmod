package yesman.epicfight.capabilities.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.item.ArmorCapability;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.item.DefinedWeaponTypes;
import yesman.epicfight.capabilities.item.NBTSeparativeCapability;
import yesman.epicfight.config.CapabilityConfig;
import yesman.epicfight.config.CapabilityConfig.CustomArmorConfig;
import yesman.epicfight.config.CapabilityConfig.CustomWeaponConfig;
import yesman.epicfight.main.EpicFightMod;

public class ProviderItem implements ICapabilityProvider, NonNullSupplier<CapabilityItem> {
	private static final Map<Class<? extends Item>, Function<Item, CapabilityItem>> CAPABILITY_BY_CLASS = new HashMap<Class<? extends Item>, Function<Item, CapabilityItem>> ();
	private static final Map<Item, CapabilityItem> CAPABILITIES = new HashMap<Item, CapabilityItem> ();
	
	public static void registerWeaponClass() {
		CAPABILITY_BY_CLASS.put(ArmorItem.class, ArmorCapability::new);
		CAPABILITY_BY_CLASS.put(ShieldItem.class, DefinedWeaponTypes.SHIELD);
		CAPABILITY_BY_CLASS.put(SwordItem.class, DefinedWeaponTypes.SWORD);
		CAPABILITY_BY_CLASS.put(PickaxeItem.class, DefinedWeaponTypes.PICKAXE);
		CAPABILITY_BY_CLASS.put(AxeItem.class, DefinedWeaponTypes.AXE);
		CAPABILITY_BY_CLASS.put(ShovelItem.class, DefinedWeaponTypes.SHOVEL);
		CAPABILITY_BY_CLASS.put(HoeItem.class, DefinedWeaponTypes.HOE);
		CAPABILITY_BY_CLASS.put(BowItem.class, DefinedWeaponTypes.BOW);
		CAPABILITY_BY_CLASS.put(CrossbowItem.class, DefinedWeaponTypes.CROSSBOW);
	}
	
	public static void put(Item item, CapabilityItem cap) {
		CAPABILITIES.put(item, cap);
	}
	
	public static boolean has(Item item) {
		return CAPABILITIES.containsKey(item);
	}
	
	public static void addDefaultItems() {
		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			if (!CAPABILITIES.containsKey(item)) {
				Class<?> clazz = item.getClass();
				CapabilityItem capability = null;
				
				for (; clazz != null && capability == null; clazz = clazz.getSuperclass()) {
					capability = CAPABILITY_BY_CLASS.getOrDefault(clazz, (argIn) -> null).apply(item);
				}
				
				if (capability != null) {
					EpicFightMod.LOGGER.info("register weapon capability for " + item);
					CAPABILITIES.put(item, capability);
				}
			}
		}
	}
	
	public static void addConfigItems() {
		for (CustomWeaponConfig config : CapabilityConfig.CUSTOM_WEAPON_LIST) {
			ResourceLocation key = new ResourceLocation(config.getRegistryName());
			if (ForgeRegistries.ITEMS.containsKey(key)) {
				Item item = ForgeRegistries.ITEMS.getValue(key);
				EpicFightMod.LOGGER.info("Register Custom Capaiblity for " + config.getRegistryName());
				CapabilityItem cap = config.getWeaponType().get(item);
				cap.setConfigFileAttribute(config.getArmorIgnoranceOnehand(), config.getImpactOnehand(), config.getMaxStrikesOnehand(),
						config.getArmorIgnoranceTwohand(), config.getImpactTwohand(), config.getMaxStrikesTwohand());
				CAPABILITIES.put(item, cap);
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
					CAPABILITIES.put(item, cap);
					EpicFightMod.LOGGER.info("Register Custom Capability for " + config.getRegistryName());
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
	
	public static void clear() {
		CAPABILITIES.clear();
	}
	
	private CapabilityItem capability;
	private LazyOptional<CapabilityItem> optional = LazyOptional.of(this);
	
	public ProviderItem(ItemStack itemstack) {
		this.capability = CAPABILITIES.get(itemstack.getItem());
		
		if (this.capability instanceof NBTSeparativeCapability) {
			this.capability = this.capability.getFinal(itemstack);
		}
	}
	
	public boolean hasCapability() {
		return this.capability != null;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == ModCapabilities.CAPABILITY_ITEM ? this.optional.cast() : LazyOptional.empty();
	}

	@Override
	public CapabilityItem get() {
		return this.capability;
	}
}