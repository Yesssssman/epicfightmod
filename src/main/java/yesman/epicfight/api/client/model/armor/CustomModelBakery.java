package yesman.epicfight.api.client.model.armor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.SharedConstants;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class CustomModelBakery {
	static final Map<ResourceLocation, AnimatedMesh> BAKED_MODELS = Maps.newHashMap();
	static final List<ArmorModelTransformer> MODEL_TRANSFORMERS = Lists.newArrayList();
	static final ArmorModelTransformer VANILLA_TRANSFORMER = new VanillaArmor();
	static final Set<ArmorItem> EXCEPTIONAL_MODELS = Sets.newHashSet();
	
	public static void registerNewTransformer(ArmorModelTransformer transformer) {
		MODEL_TRANSFORMERS.add(transformer);
	}
	
	public static void exportModels(File resourcePackDirectory) throws IOException {
		File zipFile = new File(resourcePackDirectory, "epicfight_custom_armors.zip");
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		
		for (Map.Entry<ResourceLocation, AnimatedMesh> entry : BAKED_MODELS.entrySet()) {
			ZipEntry zipEntry = new ZipEntry(String.format("assets/%s/animmodels/armor/%s.json", entry.getKey().getNamespace(), entry.getKey().getPath()));
			Gson gson = new GsonBuilder().create();
			out.putNextEntry(zipEntry);
			out.write(gson.toJson(entry.getValue().toJsonObject()).getBytes());
			out.closeEntry();
			EpicFightMod.LOGGER.info("Exported custom armor model : " + entry.getKey());
		}
		
		ZipEntry zipEntry = new ZipEntry("pack.mcmeta");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonObject root = new JsonObject();
		JsonObject pack = new JsonObject();
		pack.addProperty("description", "epicfight_custom_armor_models");
		pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES));
		root.add("pack", pack);
		out.putNextEntry(zipEntry);
		out.write(gson.toJson(root).getBytes());
		out.closeEntry();
		out.close();
	}
	
	public static AnimatedMesh bake(HumanoidModel<?> armorModel, ArmorItem armorItem, EquipmentSlot slot, boolean debuggingMode) {
		AnimatedMesh animatedArmorModel = null;
		
		if (!EXCEPTIONAL_MODELS.contains(armorItem)) {
			for (ArmorModelTransformer modelTransformer : MODEL_TRANSFORMERS) {
				try {
					animatedArmorModel = modelTransformer.transformModel(armorModel, armorItem, slot, debuggingMode);
				} catch (Exception e) {
					EpicFightMod.LOGGER.warn("Can't transform the model of " + ForgeRegistries.ITEMS.getKey(armorItem) + " because of :");
					e.printStackTrace();
					EXCEPTIONAL_MODELS.add(armorItem);
				}
				
				if (animatedArmorModel != null) {
					break;
				}
			}
			
			if (animatedArmorModel == null) {
				animatedArmorModel = VANILLA_TRANSFORMER.transformModel(armorModel, armorItem, slot, debuggingMode);
			}
		}
		
		BAKED_MODELS.put(ForgeRegistries.ITEMS.getKey(armorItem), animatedArmorModel);
		
		return animatedArmorModel;
	}
}