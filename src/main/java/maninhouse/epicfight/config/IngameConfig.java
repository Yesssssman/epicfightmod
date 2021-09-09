package maninhouse.epicfight.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class IngameConfig {
	public final IntValue longPressCountConfig;
	public final BooleanValue showHealthIndicator;
	public final BooleanValue showTargetIndicator;
	public final BooleanValue filterAnimation;
	public final DoubleValue aimHelperColor;
	public final BooleanValue enableAimHelper;
	public final BooleanValue cameraAutoSwitch;
	public final BooleanValue autoPreparation;
	
	public IngameConfig(ForgeConfigSpec.Builder config) {
		this.longPressCountConfig = config.defineInRange("ingame.long_press_count", 2, 1, 10);
		this.showHealthIndicator = config.define("ingame.show_health_indicator", () -> true);
		this.showTargetIndicator = config.define("ingame.show_target_indicator", () -> true);
		this.filterAnimation = config.define("ingame.filter_animation", () -> false);
		this.aimHelperColor = config.defineInRange("ingame.laser_pointer_color", 0.328125D, 0.0D, 1.0D);
		this.enableAimHelper = config.define("ingame.enable_laser_pointer", () -> true);
		this.cameraAutoSwitch = config.define("ingame.camera_auto_switch", () -> false);
		this.autoPreparation = config.define("ingame.auto_preparation", () -> false);
	}
}