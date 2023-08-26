package yesman.epicfight.world.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EpicFightMod.MODID);
	
	public static final RegistryObject<Item> UCHIGATANA = ITEMS.register("uchigatana", () -> new UchigatanaItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> UCHIGATANA_SHEATH = ITEMS.register("uchigatana_sheath", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> STONE_GREATSWORD = ITEMS.register("stone_greatsword", () -> new GreatswordItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.STONE));
	public static final RegistryObject<Item> IRON_GREATSWORD = ITEMS.register("iron_greatsword", () -> new GreatswordItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.IRON));
	public static final RegistryObject<Item> GOLDEN_GREATSWORD = ITEMS.register("golden_greatsword", () -> new GreatswordItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.GOLD));
	public static final RegistryObject<Item> DIAMOND_GREATSWORD = ITEMS.register("diamond_greatsword", () -> new GreatswordItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.DIAMOND));
	public static final RegistryObject<Item> NETHERITE_GREATSWORD = ITEMS.register("netherite_greatsword", () -> new GreatswordItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS).fireResistant(), Tiers.NETHERITE));
	
	public static final RegistryObject<Item> STONE_SPEAR = ITEMS.register("stone_spear", () -> new SpearItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.STONE));
	public static final RegistryObject<Item> IRON_SPEAR = ITEMS.register("iron_spear", () -> new SpearItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.IRON));
	public static final RegistryObject<Item> GOLDEN_SPEAR = ITEMS.register("golden_spear", () -> new SpearItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.GOLD));
	public static final RegistryObject<Item> DIAMOND_SPEAR = ITEMS.register("diamond_spear", () -> new SpearItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.DIAMOND));
	public static final RegistryObject<Item> NETHERITE_SPEAR = ITEMS.register("netherite_spear", () -> new SpearItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS).fireResistant(), Tiers.NETHERITE));
	
	public static final RegistryObject<Item> IRON_TACHI = ITEMS.register("iron_tachi", () -> new TachiItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.IRON));
	public static final RegistryObject<Item> GOLDEN_TACHI = ITEMS.register("golden_tachi", () -> new TachiItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.GOLD));
	public static final RegistryObject<Item> DIAMOND_TACHI = ITEMS.register("diamond_tachi", () -> new TachiItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.DIAMOND));
	public static final RegistryObject<Item> NETHERITE_TACHI = ITEMS.register("netherite_tachi", () -> new TachiItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS).fireResistant(), Tiers.NETHERITE));
	
	public static final RegistryObject<Item> IRON_LONGSWORD = ITEMS.register("iron_longsword", () -> new LongswordItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.IRON));
	public static final RegistryObject<Item> GOLDEN_LONGSWORD = ITEMS.register("golden_longsword", () -> new LongswordItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.GOLD));
	public static final RegistryObject<Item> DIAMOND_LONGSWORD = ITEMS.register("diamond_longsword", () -> new LongswordItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.DIAMOND));
	public static final RegistryObject<Item> NETHERITE_LONGSWORD = ITEMS.register("netherite_longsword", () -> new LongswordItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS).fireResistant(), Tiers.NETHERITE));
	
	public static final RegistryObject<Item> IRON_DAGGER = ITEMS.register("iron_dagger", () -> new DaggerItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.IRON));
	public static final RegistryObject<Item> GOLDEN_DAGGER = ITEMS.register("golden_dagger", () -> new DaggerItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.GOLD));
	public static final RegistryObject<Item> DIAMOND_DAGGER = ITEMS.register("diamond_dagger", () -> new DaggerItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), Tiers.DIAMOND));
	public static final RegistryObject<Item> NETHERITE_DAGGER = ITEMS.register("netherite_dagger", () -> new DaggerItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS).fireResistant(), Tiers.NETHERITE));
	
	public static final RegistryObject<Item> KNUCKLE = ITEMS.register("knuckle", () -> new KnuckleItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS), EpicFightItemTier.KNUCKLE));
	
	public static final RegistryObject<Item> STRAY_HAT = ITEMS.register("stray_hat", () -> new ArmorItem(EpicFightArmorMaterials.STRAY_CLOTH, EquipmentSlot.HEAD, new Item.Properties().tab(EpicFightCreativeTabs.ITEMS)));
	public static final RegistryObject<Item> STRAY_ROBE = ITEMS.register("stray_robe", () -> new ArmorItem(EpicFightArmorMaterials.STRAY_CLOTH, EquipmentSlot.CHEST, new Item.Properties().tab(EpicFightCreativeTabs.ITEMS)));
	public static final RegistryObject<Item> STRAY_PANTS = ITEMS.register("stray_pants", () -> new ArmorItem(EpicFightArmorMaterials.STRAY_CLOTH, EquipmentSlot.LEGS, new Item.Properties().tab(EpicFightCreativeTabs.ITEMS)));
	
	public static final RegistryObject<Item> SKILLBOOK = ITEMS.register("skillbook", () -> new SkillBookItem(new Item.Properties().tab(EpicFightCreativeTabs.ITEMS).rarity(Rarity.RARE).stacksTo(1)));
}