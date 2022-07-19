package yesman.epicfight.api.data.reloader;

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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.world.capabilities.item.ArmorCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.TagBasedSeparativeCapability;
import yesman.epicfight.world.capabilities.item.WeaponCapabilityPresets;
import yesman.epicfight.world.capabilities.provider.ProviderItem;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ItemCapabilityReloadListener extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();
	private static final Map<Item, CompoundTag> CAPABILITY_ARMOR_DATA_MAP = Maps.newHashMap();
	private static final Map<Item, CompoundTag> CAPABILITY_WEAPON_DATA_MAP = Maps.newHashMap();
	
	public ItemCapabilityReloadListener() {
		super(GSON, "capabilities");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
		for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
			ResourceLocation rl = entry.getKey();
			String path = rl.getPath();
			
			if (path.contains("/")) {
				String[] str = path.split("/");
				ResourceLocation registryName = new ResourceLocation(rl.getNamespace(), str[1]);
				Item item = ForgeRegistries.ITEMS.getValue(registryName);
				
				if (item == null) {
					EpicFightMod.LOGGER.warn("Tried to add a capabiltiy for item " + registryName + ", but it's not exist!");
					return;
				}
				
				CompoundTag nbt = null;
				
				try {
					nbt = TagParser.parseTag(entry.getValue().toString());
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
				}
				
				if (str[0].equals("armors")) {
					CapabilityItem capability = deserializeArmor(item, nbt);
					ProviderItem.put(item, capability);
					CAPABILITY_ARMOR_DATA_MAP.put(item, nbt);
				} else if (str[0].equals("weapons")) {
					CapabilityItem capability = deserializeWeapon(item, nbt, null);
					ProviderItem.put(item, capability);
					CAPABILITY_WEAPON_DATA_MAP.put(item, nbt);
				}
			}
		}
		
		ProviderItem.addDefaultItems();
	}
	
	public static CapabilityItem deserializeArmor(Item item, CompoundTag tag) {
		CapabilityItem capability = null;
		
		if (tag.contains("attributes")) {
			CompoundTag attributes = tag.getCompound("attributes");
			capability = new ArmorCapability(item, attributes.getDouble("weight"), attributes.getDouble("stun_armor"));
		} else {
			capability = new ArmorCapability(item);
		}
		
		return capability;
	}
	
	public static CapabilityItem deserializeWeapon(Item item, CompoundTag tag, CapabilityItem defaultCapability) {
		CapabilityItem capability;
		
		if (tag.contains("variations")) {
			ListTag jsonArray = tag.getList("variations", 10);
			List<Pair<Predicate<ItemStack>, CapabilityItem>> list = Lists.newArrayList();
			CapabilityItem innerDefaultCapability = tag.contains("type") ? WeaponCapabilityPresets.get(tag.getString("type")).apply(item) : CapabilityItem.EMPTY;
			
			for (Tag jsonElement : jsonArray) {
				CompoundTag innerTag = ((CompoundTag)jsonElement);
				String nbtKey = innerTag.getString("nbt_key");
				String nbtValue = innerTag.getString("nbt_value");
				Predicate<ItemStack> predicate = (itemstack) -> {
					CompoundTag compound = itemstack.getTag();
					
					if (compound == null) {
						return false;
					}
					
					return compound.contains(nbtKey) ? compound.getString(nbtKey).equals(nbtValue) : false;
				};
				list.add(Pair.of(predicate, deserializeWeapon(item, innerTag, innerDefaultCapability)));
			}
			
			if (tag.contains("attributes")) {
				CompoundTag attributes = tag.getCompound("attributes");
				
				for (String key : attributes.getAllKeys()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttribute(attributes.getCompound(key));
					
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						innerDefaultCapability.addStyleAttibute(Style.ENUM_MANAGER.get(key), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}
			
			capability = new TagBasedSeparativeCapability(list, innerDefaultCapability);
		} else {
			capability = tag.contains("type") ? WeaponCapabilityPresets.get(tag.getString("type")).apply(item) : defaultCapability;
			
			if (tag.contains("attributes")) {
				CompoundTag attributes = tag.getCompound("attributes");
				
				for (String key : attributes.getAllKeys()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttribute(attributes.getCompound(key));
					
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						capability.addStyleAttibute(Style.ENUM_MANAGER.get(key), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}
		}
		
		return capability;
	}
	
	private static Map<Attribute, AttributeModifier> deserializeAttribute(CompoundTag tag) {
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
	
	public static Stream<CompoundTag> getArmorDataStream() {
		Stream<CompoundTag> tagStream = CAPABILITY_ARMOR_DATA_MAP.entrySet().stream().map((entry) -> {
			entry.getValue().putInt("id", Item.getId(entry.getKey()));
			return entry.getValue();
		});
		return tagStream;
	}
	
	public static Stream<CompoundTag> getWeaponDataStream() {
		Stream<CompoundTag> tagStream = CAPABILITY_WEAPON_DATA_MAP.entrySet().stream().map((entry) -> {
			entry.getValue().putInt("id", Item.getId(entry.getKey()));
			return entry.getValue();
		});
		return tagStream;
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
	public static void processServerPacket(SPDatapackSync packet) {
		switch (packet.getType()) {
		case ARMOR:
			for (CompoundTag tag : packet.getTags()) {
				Item item = Item.byId(tag.getInt("id"));
				CAPABILITY_ARMOR_DATA_MAP.put(item, tag);
			}
			armorReceived = true;
			break;
		case WEAPON:
			for (CompoundTag tag : packet.getTags()) {
				Item item = Item.byId(tag.getInt("id"));
				CAPABILITY_WEAPON_DATA_MAP.put(item, tag);
			}
			weaponReceived = true;
			break;
		case MOB:
			break;
		}
		
		if (armorReceived && weaponReceived) {
			CAPABILITY_ARMOR_DATA_MAP.forEach((item, tag) -> {
				ProviderItem.put(item, deserializeArmor(item, tag));
			});
			
			CAPABILITY_WEAPON_DATA_MAP.forEach((item, tag) -> {
				ProviderItem.put(item, deserializeWeapon(item, tag, null));
			});
			
			ProviderItem.addDefaultItems();
		}
	}
}