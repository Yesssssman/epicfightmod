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
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.world.capabilities.item.ArmorCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.TagBasedSeparativeCapability;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;
import yesman.epicfight.world.capabilities.provider.ItemCapabilityProvider;
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
				String[] str = path.split("/", 2);
				ResourceLocation registryName = new ResourceLocation(rl.getNamespace(), str[1]);
				Item item = ForgeRegistries.ITEMS.getValue(registryName);
				
				if (item == null) {
					EpicFightMod.LOGGER.warn("Tried to add a capability for item " + registryName + ", but it doesn't exist!");
					return;
				}
				
				CompoundTag nbt = null;
				
				try {
					nbt = TagParser.parseTag(entry.getValue().toString());
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
				}
				
				try {
					if (str[0].equals("armors")) {
						CapabilityItem capability = deserializeArmor(item, nbt);
						ItemCapabilityProvider.put(item, capability);
						CAPABILITY_ARMOR_DATA_MAP.put(item, nbt);
					} else if (str[0].equals("weapons")) {
						CapabilityItem capability = deserializeWeapon(item, nbt, null);
						ItemCapabilityProvider.put(item, capability);
						CAPABILITY_WEAPON_DATA_MAP.put(item, nbt);
					}
				} catch (Exception e) {
					EpicFightMod.LOGGER.warn("Error while deserializing datapack for " + registryName);
					e.printStackTrace();
				}
			}
		}
		
		ItemCapabilityProvider.addDefaultItems();
	}
	
	public static CapabilityItem deserializeArmor(Item item, CompoundTag tag) {
		ArmorCapability.Builder builder = ArmorCapability.builder();
		
		if (tag.contains("attributes")) {
			CompoundTag attributes = tag.getCompound("attributes");
			builder.weight(attributes.getDouble("weight")).stunArmor(attributes.getDouble("stun_armor"));
		}
		
		builder.item(item);
		
		return builder.build();
	}
	
	public static CapabilityItem deserializeWeapon(Item item, CompoundTag tag, CapabilityItem.Builder defaultCapability) {
		CapabilityItem capability;
		
		if (tag.contains("variations")) {
			ListTag jsonArray = tag.getList("variations", 10);
			List<Pair<Predicate<ItemStack>, CapabilityItem>> list = Lists.newArrayList();
			CapabilityItem.Builder innerDefaultCapabilityBuilder = tag.contains("type") ? WeaponTypeReloadListener.get(tag.getString("type")).apply(item) : CapabilityItem.builder();
			
			for (Tag jsonElement : jsonArray) {
				CompoundTag innerTag = ((CompoundTag)jsonElement);
				String nbtKey = innerTag.getString("nbt_key");
				String nbtValue = innerTag.getString("nbt_value");
				Predicate<ItemStack> predicate = (itemstack) -> {
					CompoundTag compound = itemstack.getTag();
					
					if (compound == null) {
						return false;
					}
					
					return compound.contains(nbtKey) && compound.getString(nbtKey).equals(nbtValue);
				};
				
				list.add(Pair.of(predicate, deserializeWeapon(item, innerTag, innerDefaultCapabilityBuilder)));
			}
			
			if (tag.contains("attributes")) {
				CompoundTag attributes = tag.getCompound("attributes");
				
				for (String key : attributes.getAllKeys()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttributes(attributes.getCompound(key));
					
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						innerDefaultCapabilityBuilder.addStyleAttibutes(Style.ENUM_MANAGER.get(key), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}
			
			capability = new TagBasedSeparativeCapability(list, innerDefaultCapabilityBuilder.build());
		} else {
			CapabilityItem.Builder builder = tag.contains("type") ? WeaponTypeReloadListener.get(tag.getString("type")).apply(item) : CapabilityItem.builder();
			
			if (tag.contains("attributes")) {
				CompoundTag attributes = tag.getCompound("attributes");
				
				for (String key : attributes.getAllKeys()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttributes(attributes.getCompound(key));
					
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						builder.addStyleAttibutes(Style.ENUM_MANAGER.get(key), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}
			
			if (tag.contains("collider") && builder instanceof WeaponCapability.Builder weaponCapBuilder) {
				CompoundTag colliderTag = tag.getCompound("collider");
				Collider collider = deserializeCollider(colliderTag);
				weaponCapBuilder.collider(collider);
			}
			
			capability = builder.build();
		}
		
		return capability;
	}
	
	private static Map<Attribute, AttributeModifier> deserializeAttributes(CompoundTag tag) {
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
	
	public static Collider deserializeCollider(CompoundTag tag) {
		int number = tag.getInt("number");
		
		if (number < 1) {
			throw new IllegalArgumentException("Datapack deserialization error: the number of colliders must bigger than 0!");
		}
		
		ListTag sizeVector = tag.getList("size", 6);
		ListTag centerVector = tag.getList("center", 6);
		
		double sizeX = sizeVector.getDouble(0);
		double sizeY = sizeVector.getDouble(1);
		double sizeZ = sizeVector.getDouble(2);
		
		double centerX = centerVector.getDouble(0);
		double centerY = centerVector.getDouble(1);
		double centerZ = centerVector.getDouble(2);
		
		if (sizeX < 0 || sizeY < 0 || sizeZ < 0) {
			throw new IllegalArgumentException("Datapack deserialization error: the size of the collider must be non-negative value!");
		}
		
		if (number == 1) {
			return new OBBCollider(sizeX, sizeY, sizeZ, centerX, centerY, centerZ);
		} else {
			return new MultiOBBCollider(number, sizeX, sizeY, sizeZ, centerX, centerY, centerZ);
		}
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
		default:
			break;
		}
		
		if (armorReceived && weaponReceived) {
			CAPABILITY_ARMOR_DATA_MAP.forEach((item, tag) -> {
				ItemCapabilityProvider.put(item, deserializeArmor(item, tag));
			});
			
			CAPABILITY_WEAPON_DATA_MAP.forEach((item, tag) -> {
				ItemCapabilityProvider.put(item, deserializeWeapon(item, tag, null));
			});
			
			ItemCapabilityProvider.addDefaultItems();
		}
	}
}