package maninhouse.epicfight.config;

import maninhouse.epicfight.client.gui.widget.ColorSlider;
import maninhouse.epicfight.config.Option.DoubleOption;
import maninhouse.epicfight.config.Option.IntegerOption;

public class ConfigurationIngame {
	public static final float A_TICK = 0.05F;
	public static final float GENERAL_ANIMATION_CONVERT_TIME = 0.15F;
	
	public final IntegerOption longPressCount;
	public final Option<Boolean> filterAnimation;
	public final Option<Boolean> showHealthIndicator;
	public final Option<Boolean> showTargetIndicator;
	public final DoubleOption aimHelperColor;
	public final Option<Boolean> enableAimHelperPointer;
	public final Option<Boolean> cameraAutoSwitch;
	public int aimHelperRealColor;
	
	public ConfigurationIngame() {
		IngameConfig config = ConfigManager.INGAME_CONFIG;
		this.longPressCount = new IntegerOption(config.longPressCountConfig.get(), 1, 10);
		this.filterAnimation = new Option<Boolean>(config.filterAnimation.get());
		this.showHealthIndicator = new Option<Boolean>(config.showHealthIndicator.get());
		this.showTargetIndicator = new Option<Boolean>(config.showTargetIndicator.get());
		this.aimHelperColor = new DoubleOption(config.aimHelperColor.get(), 0.0D, 1.0D);
		this.enableAimHelperPointer = new Option<Boolean>(config.enableAimHelper.get());
		this.aimHelperRealColor = ColorSlider.toColorInteger(config.aimHelperColor.get());
		this.cameraAutoSwitch = new Option<Boolean>(config.cameraAutoSwitch.get());
	}
	
	public void resetSettings() {
		this.longPressCount.setDefaultValue();
		this.filterAnimation.setDefaultValue();
		this.showHealthIndicator.setDefaultValue();
		this.showTargetIndicator.setDefaultValue();
		this.aimHelperColor.setDefaultValue();
		this.enableAimHelperPointer.setDefaultValue();
		this.cameraAutoSwitch.setDefaultValue();
		this.aimHelperRealColor = ColorSlider.toColorInteger(this.aimHelperColor.getValue());
	}
	
	public void save() {
		ConfigManager.INGAME_CONFIG.longPressCountConfig.set(this.longPressCount.getValue());
		ConfigManager.INGAME_CONFIG.filterAnimation.set(this.filterAnimation.getValue());
		ConfigManager.INGAME_CONFIG.showHealthIndicator.set(this.showHealthIndicator.getValue());
		ConfigManager.INGAME_CONFIG.showTargetIndicator.set(this.showTargetIndicator.getValue());
		ConfigManager.INGAME_CONFIG.aimHelperColor.set(this.aimHelperColor.getValue());
		ConfigManager.INGAME_CONFIG.enableAimHelper.set(this.enableAimHelperPointer.getValue());
		ConfigManager.INGAME_CONFIG.cameraAutoSwitch.set(this.cameraAutoSwitch.getValue());
		this.aimHelperRealColor = ColorSlider.toColorInteger(this.aimHelperColor.getValue());
	}
}