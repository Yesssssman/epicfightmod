package yesman.epicfight.api.client.model;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class ItemSkins extends SimpleJsonResourceReloadListener {
	public static final ItemSkins INSTANCE = new ItemSkins();
	private static final Map<Item, ItemSkin> ITEM_SKIN_MAP = Maps.newHashMap();
	
	public static ItemSkin getItemSkin(Item item) {
		return ITEM_SKIN_MAP.get(item);
	}
	
	public ItemSkins() {
		super((new GsonBuilder()).create(), "item_skins");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profileFiller) {
		for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
			ResourceLocation rl = entry.getKey();
			String pathString = rl.getPath();
			ResourceLocation registryName = new ResourceLocation(rl.getNamespace(), pathString);
			
			if (!ForgeRegistries.ITEMS.containsKey(registryName)) {
				EpicFightMod.LOGGER.warn("[Item Skins] Item named " + registryName + " does not exist");
				continue;
			}
			
			Item item = ForgeRegistries.ITEMS.getValue(registryName);
			ItemSkin itemSkin = ItemSkin.deserialize(entry.getValue());
			
			ITEM_SKIN_MAP.put(item, itemSkin);
		}
	}
}