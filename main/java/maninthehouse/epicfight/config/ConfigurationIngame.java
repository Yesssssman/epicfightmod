package maninthehouse.epicfight.config;

import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;

@Config(modid = EpicFightMod.MODID, type = Type.INSTANCE, name = EpicFightMod.MODID + "_client")
public class ConfigurationIngame {
	@Name("Long Press Count")
	public static int longPressCount = 2;
	
	@Name("Filter Animation")
	public static boolean filterAnimation = false;
	
	@Name("show Health Indicator")
	public static boolean showHealthIndicator = true;
	
	public static void resetSettings() {
		longPressCount = 2;
		filterAnimation = false;
		showHealthIndicator = true;
	}
}