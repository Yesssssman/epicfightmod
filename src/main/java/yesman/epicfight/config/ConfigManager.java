package yesman.epicfight.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;
import yesman.epicfight.config.CapabilityConfig.EntityAI;
import yesman.epicfight.config.CapabilityConfig.WeaponType;
import yesman.epicfight.main.EpicFightMod;

public class ConfigManager {
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final IngameConfig INGAME_CONFIG;
	
	static {
		CommentedFileConfig file = CommentedFileConfig.builder(new File(FMLPaths.CONFIGDIR.get().resolve(EpicFightMod.CONFIG_FILE_PATH).toString())).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
		ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
		
		INGAME_CONFIG = new IngameConfig(client);
		
		String weaponKey = "custom_weaponry";
		if(file.valueMap().get(weaponKey) == null) {
			server.define(String.format("%s.%s.registry_name", weaponKey, "sample"), "modid:registryname");
			server.defineEnum(String.format("%s.%s.weapon_type", weaponKey, "sample"), WeaponType.SWORD);
			server.define(String.format("%s.%s.impact", weaponKey, "sample"), 0.5D);
			server.define(String.format("%s.%s.armor_ignorance", weaponKey, "sample"), 0.0D);
			server.define(String.format("%s.%s.hit_at_once", weaponKey, "sample"), 1);
		}
		
		String armorKey = "custom_armor";
		if(file.valueMap().get(armorKey) == null) {
			server.define(String.format("%s.%s.registry_name", armorKey, "sample"), "modid:registryname");
			server.define(String.format("%s.%s.stunArmor", armorKey, "sample"), 0.0D);
			server.define(String.format("%s.%s.weight", armorKey, "sample"), 0.0D);
		}
		
		String entityKey = "custom_entity";
		if(file.valueMap().get(entityKey) == null) {
			server.define(String.format("%s.%s.type_name", entityKey, "sample"), "modid:type_name");
			server.define(String.format("%s.%s.texture_location", entityKey, "sample"), "modid:texture_location");
			server.define(String.format("%s.%s.ai_type", entityKey, "sample"), EntityAI.ZOMBIE);
			server.define(String.format("%s.%s.impact", entityKey, "sample"), 0.5D);
			server.define(String.format("%s.%s.armor_negation", entityKey, "sample"), 0.0D);
			server.define(String.format("%s.%s.max_strikes", entityKey, "sample"), 1);
		}
		
		CapabilityConfig.init(server, file.valueMap());
		CLIENT_CONFIG = client.build();
		COMMON_CONFIG = server.build();
	}
	
	public static void loadConfig(ForgeConfigSpec config, String path) {
		CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		config.setConfig(file);
		EpicFightMod.LOGGER.info("Load Configuration File");
	}
}