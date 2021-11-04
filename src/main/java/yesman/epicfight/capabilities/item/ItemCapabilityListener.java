package yesman.epicfight.capabilities.item;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.capabilities.provider.ProviderItem;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.server.STCDatapackSync;

public class ItemCapabilityListener extends JsonReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();
	private static final Map<Item, CompoundNBT> CAPABILITY_ARMOR_DATA_MAP = Maps.newHashMap();
	private static final Map<Item, CompoundNBT> CAPABILITY_WEAPON_DATA_MAP = Maps.newHashMap();
	
	public ItemCapabilityListener() {
		super(GSON, "capabilities");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		ProviderItem.addConfigItems();
		
		for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
			ResourceLocation rl = entry.getKey();
			String pathString = rl.getPath();
			
			if (pathString.contains("/")) {
				String[] str = pathString.split("/");
				ResourceLocation registryName = new ResourceLocation(rl.getNamespace(), str[1]);
				Item item = ForgeRegistries.ITEMS.getValue(registryName);
				
				if (item == null) {
					EpicFightMod.LOGGER.warn("Tried to add a capabiltiy for item " + registryName + ", but it's not found!");
					return;
				}
				
				CompoundNBT nbt = null;
				try {
					nbt = JsonToNBT.getTagFromJson(entry.getValue().toString());
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
				}
				
				if (str[0].equals("armors")) {
					CapabilityItem capability = deserializeArmor(item, nbt);
					ProviderItem.put(item, capability);
					EpicFightMod.LOGGER.info("register armor capability for " + registryName);
					CAPABILITY_ARMOR_DATA_MAP.put(item, nbt);
				} else if (str[0].equals("weapons")) {
					CapabilityItem capability = deserializeWeapon(item, nbt, null);
					ProviderItem.put(item, capability);
					EpicFightMod.LOGGER.info("register weapon capability for " + registryName);
					CAPABILITY_WEAPON_DATA_MAP.put(item, nbt);
				}
			}
		}
		ProviderItem.addDefaultItems();
		ProviderItem.processDeferredProviders();
	}
	
	public static CapabilityItem deserializeArmor(Item item, CompoundNBT tag) {
		CapabilityItem capability = null;
		if (tag.contains("attributes")) {
			CompoundNBT attributes = tag.getCompound("attributes");
			
			capability = new ArmorCapability(item, attributes.getDouble("weight"), attributes.getDouble("stun_armor"));
		} else {
			capability = new ArmorCapability(item);
		}
		
		return capability;
	}
	
	public static CapabilityItem deserializeWeapon(Item item, CompoundNBT tag, CapabilityItem defaultCapability) {
		CapabilityItem capability;
		if (tag.contains("variables")) {
			ListNBT jsonArray = tag.getList("variables", 10);
			List<Pair<Predicate<ItemStack>, CapabilityItem>> list = Lists.newArrayList();
			CapabilityItem innerDefaultCapability = tag.contains("type") ? DefinedWeaponTypes.get(tag.getString("type")).apply(item) : CapabilityItem.EMPTY;
			
			for (INBT jsonElement : jsonArray) {
				CompoundNBT innerTag = ((CompoundNBT)jsonElement);
				String nbtKey = innerTag.getString("nbt_key");
				String nbtValue = innerTag.getString("nbt_value");
				Predicate<ItemStack> predicate = (itemstack) -> {
					CompoundNBT compound = itemstack.getTag();
					if (compound == null) {
						return false;
					}
					return compound.contains(nbtKey) ? compound.getString(nbtKey).equals(nbtValue) : false;
				};
				list.add(Pair.of(predicate, deserializeWeapon(item, innerTag, innerDefaultCapability)));
			}
			
			if (tag.contains("attributes")) {
				CompoundNBT attributes = tag.getCompound("attributes");
				for (String key : attributes.keySet()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttribute(attributes.getCompound(key));
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						innerDefaultCapability.addStyleAttibute(CapabilityItem.Style.valueOf(key.toUpperCase()), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}
			
			capability = new NBTSeparativeCapability(list, innerDefaultCapability);
		} else {
			capability = tag.contains("type") ? DefinedWeaponTypes.get(tag.getString("type")).apply(item) : defaultCapability;
			
			if (tag.contains("attributes")) {
				CompoundNBT attributes = tag.getCompound("attributes");
				for (String key : attributes.keySet()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttribute(attributes.getCompound(key));
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						capability.addStyleAttibute(CapabilityItem.Style.valueOf(key.toUpperCase()), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}
		}
		
		return capability;
	}
	
	private static Map<Attribute, AttributeModifier> deserializeAttribute(CompoundNBT tag) {
		Map<Attribute, AttributeModifier> modifierMap = Maps.newHashMap();
		
		if (tag.contains("armor_negation")) {
			modifierMap.put(EpicFightAttributes.ARMOR_NEGATION.get(), EpicFightAttributes.getArmorNegationModifier(tag.getDouble("armor_negation")));
		}
		if (tag.contains("impact")) {
			modifierMap.put(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(tag.getDouble("impact")));
		}
		if (tag.contains("max_strikes")) {
			modifierMap.put(EpicFightAttributes.MAX_STRIKES.get(), EpicFightAttributes.getMaxStrikesModifier(tag.getInt("max_strikes")));
		}
		if (tag.contains("damage_bonus")) {
			modifierMap.put(Attributes.ATTACK_DAMAGE, EpicFightAttributes.getDamageBonusModifier(tag.getDouble("damage_bonus")));
		}
		if (tag.contains("speed_bonus")) {
			modifierMap.put(Attributes.ATTACK_SPEED, EpicFightAttributes.getSpeedBonusModifier(tag.getDouble("speed_bonus")));
		}
		
		return modifierMap;
	}
	
	public static Stream<CompoundNBT> getArmorDataStream() {
		Stream<CompoundNBT> nbtStream = CAPABILITY_ARMOR_DATA_MAP.entrySet().stream().map((entry) -> {
			entry.getValue().putInt("id", Item.getIdFromItem(entry.getKey()));
			return entry.getValue();
		});
		return nbtStream;
	}
	
	public static Stream<CompoundNBT> getWeaponDataStream() {
		Stream<CompoundNBT> nbtStream = CAPABILITY_WEAPON_DATA_MAP.entrySet().stream().map((entry) -> {
			entry.getValue().putInt("id", Item.getIdFromItem(entry.getKey()));
			return entry.getValue();
		});
		return nbtStream;
	}
	
	public static int armorCount() {
		return CAPABILITY_ARMOR_DATA_MAP.size();
	}
	
	public static int weaponCount() {
		return CAPABILITY_WEAPON_DATA_MAP.size();
	}
	
	private static boolean armorReceived = false;
	private static boolean weaponReceived = false;
	
	@OnlyIn(Dist.CLIENT)
	public static void reset() {
		armorReceived = false;
		weaponReceived = false;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void processServerData(STCDatapackSync packet) {
		switch (packet.getType()) {
		case ARMOR:
			for (CompoundNBT tag : packet.getTags()) {
				Item item = Item.getItemById(tag.getInt("id"));
				CAPABILITY_ARMOR_DATA_MAP.put(item, tag);
				armorReceived = true;
			}
			break;
		case WEAPON:
			for (CompoundNBT tag : packet.getTags()) {
				Item item = Item.getItemById(tag.getInt("id"));
				CAPABILITY_WEAPON_DATA_MAP.put(item, tag);
				weaponReceived = true;
			}
			break;
		}
		
		if (armorReceived && weaponReceived) {
			ProviderItem.addConfigItems();
			
			CAPABILITY_ARMOR_DATA_MAP.forEach((item, tag) -> {
				ProviderItem.put(item, deserializeArmor(item, tag));
				EpicFightMod.LOGGER.info("register armor capability for " + item + " from server");
			});
			
			CAPABILITY_WEAPON_DATA_MAP.forEach((item, tag) -> {
				ProviderItem.put(item, deserializeWeapon(item, tag, null));
				EpicFightMod.LOGGER.info("register weapon capability for " + item + " from server");
			});
			
			ProviderItem.addDefaultItems();
			ProviderItem.processDeferredProviders();
		}
	}
}