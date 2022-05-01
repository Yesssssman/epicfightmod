package yesman.epicfight.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;
import yesman.epicfight.main.EpicFightMod;

public class ConfigManager {
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final ClientConfig INGAME_CONFIG;
	
	public static final ForgeConfigSpec.BooleanValue DO_VANILLA_ATTACK;
	public static final ForgeConfigSpec.BooleanValue KEEP_SKILLS;
	public static final ForgeConfigSpec.BooleanValue HAS_FALL_ANIMATION;
	public static final ForgeConfigSpec.BooleanValue DISABLE_ENTITY_UI;
	public static final ForgeConfigSpec.IntValue WEIGHT_PENALTY;
	
	static {
		CommentedFileConfig file = CommentedFileConfig.builder(new File(FMLPaths.CONFIGDIR.get().resolve(EpicFightMod.CONFIG_FILE_PATH).toString())).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
		ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
		
		DO_VANILLA_ATTACK = server.define("default_gamerule.doVanillaAttack", true);
		KEEP_SKILLS = server.define("default_gamerule.keepSkills", false);
		HAS_FALL_ANIMATION = server.define("default_gamerule.hasFallAnimation", true);
		DISABLE_ENTITY_UI = server.define("default_gamerule.disapleEntityUI", false);
		WEIGHT_PENALTY = server.defineInRange("default_gamerule.weightPenalty", 100, 0, 100);
		
		INGAME_CONFIG = new ClientConfig(client);
		CLIENT_CONFIG = client.build();
		COMMON_CONFIG = server.build();
	}
	
	public static void loadConfig(ForgeConfigSpec config, String path) {
		CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		config.setConfig(file);
	}
}