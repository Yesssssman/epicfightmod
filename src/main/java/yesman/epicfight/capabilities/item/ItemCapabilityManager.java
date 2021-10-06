package yesman.epicfight.capabilities.item;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.capabilities.provider.ProviderItem;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.main.EpicFightMod;

public class ItemCapabilityManager extends JsonReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();
	
	public ItemCapabilityManager() {
		super(GSON, "capabilities");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		ProviderItem.clear();
		
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
				
				if (str[0].equals("armors")) {
					CapabilityItem capability = deserializeArmor(item, entry.getValue().getAsJsonObject());
					ProviderItem.addInstance(item, capability);
					
					EpicFightMod.LOGGER.info("register weapon capability for " + registryName);
				} else if (str[0].equals("weapons")) {
					CapabilityItem capability = deserializeWeapon(item, entry.getValue().getAsJsonObject(), null);
					ProviderItem.addInstance(item, capability);
					
					EpicFightMod.LOGGER.info("register weapon capability for " + registryName);
				}
			}
		}
	}
	
	private static CapabilityItem deserializeArmor(Item item, JsonObject jsonObj) {
		CapabilityItem capability = null;
		
		if (jsonObj.has("attributes")) {
			JsonObject attributes = jsonObj.get("attributes").getAsJsonObject();
			capability = new ArmorCapability(item, attributes.get("weight").getAsDouble(), attributes.get("stun_armor").getAsDouble());
		} else {
			capability = new ArmorCapability(item);
		}
		return capability;
	}
	
	private static CapabilityItem deserializeWeapon(Item item, JsonObject jsonObj, CapabilityItem defaultCapability) {
		CapabilityItem capability;
		
		if (jsonObj.has("variables")) {
			JsonArray jsonArray = jsonObj.get("variables").getAsJsonArray();
			List<Pair<Predicate<ItemStack>, CapabilityItem>> list = Lists.newArrayList();
			CapabilityItem innerDefaultCapability = jsonObj.has("type") ? DefinedWeaponTypes.get(jsonObj.get("type").getAsString()).apply(item) : CapabilityItem.EMPTY;
			
			for (JsonElement jsonElement : jsonArray) {
				JsonObject jsonObj2 = jsonElement.getAsJsonObject();
				String nbtKey = jsonObj2.get("nbt_key").getAsString();
				String nbtValue = jsonObj2.get("nbt_value").getAsString();
				Predicate<ItemStack> predicate = (itemstack) -> {
					CompoundNBT compound = itemstack.getTag();
					
					if (compound == null) {
						return false;
					}
					
					return compound.contains(nbtKey) ? compound.getString(nbtKey).equals(nbtValue) : false;
				};
				list.add(Pair.of(predicate, deserializeWeapon(item, jsonObj2, innerDefaultCapability)));
			}
			
			if (jsonObj.has("attributes")) {
				JsonObject attributes = jsonObj.get("attributes").getAsJsonObject();
				for (Map.Entry<String, JsonElement> entry : attributes.entrySet()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttributeEntry(entry.getValue().getAsJsonObject());
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						innerDefaultCapability.addStyleAttibute(CapabilityItem.Style.valueOf(entry.getKey().toUpperCase()), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}
			
			capability = new NBTPredicateCapability(list, innerDefaultCapability);
		} else {
			capability = jsonObj.has("type") ? DefinedWeaponTypes.get(jsonObj.get("type").getAsString()).apply(item) : defaultCapability;
			
			if (jsonObj.has("attributes")) {
				JsonObject attributes = jsonObj.get("attributes").getAsJsonObject();
				for (Map.Entry<String, JsonElement> entry : attributes.entrySet()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttributeEntry(entry.getValue().getAsJsonObject());
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						capability.addStyleAttibute(CapabilityItem.Style.valueOf(entry.getKey().toUpperCase()), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}
		}
		
		
		return capability;
	}
	
	private static Map<Attribute, AttributeModifier> deserializeAttributeEntry(JsonObject jsonObj) {
		Map<Attribute, AttributeModifier> modifierMap = Maps.newHashMap();
		
		if (jsonObj.has("armor_negation")) {
			modifierMap.put(EpicFightAttributes.ARMOR_NEGATION.get(), EpicFightAttributes.getArmorNegationModifier(jsonObj.get("armor_negation").getAsDouble()));
		}
		if (jsonObj.has("impact")) {
			modifierMap.put(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(jsonObj.get("impact").getAsDouble()));
		}
		if (jsonObj.has("max_strikes")) {
			modifierMap.put(EpicFightAttributes.MAX_STRIKES.get(), EpicFightAttributes.getMaxStrikesModifier(jsonObj.get("max_strikes").getAsInt()));
		}
		if (jsonObj.has("damage_bonus")) {
			modifierMap.put(Attributes.ATTACK_DAMAGE, EpicFightAttributes.getDamageBonusModifier(jsonObj.get("damage_bonus").getAsDouble()));
		}
		if (jsonObj.has("speed_bonus")) {
			modifierMap.put(Attributes.ATTACK_SPEED, EpicFightAttributes.getSpeedBonusModifier(jsonObj.get("speed_bonus").getAsDouble()));
		}
		
		return modifierMap;
	}
}