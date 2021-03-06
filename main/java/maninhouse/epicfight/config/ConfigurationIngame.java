package maninhouse.epicfight.config;

import maninhouse.epicfight.config.Option.IntegerOption;

public class ConfigurationIngame {
	public static final float A_TICK = 0.05F;
	public static final float GENERAL_ANIMATION_CONVERT_TIME = 0.16F;
	
	public final Option<Integer> longPressCount;
	public final Option<Boolean> filterAnimation;
	public final Option<Boolean> showHealthIndicator;
	
	public ConfigurationIngame() {
		IngameConfig config = ConfigManager.INGAME_CONFIG;
		
		this.longPressCount = new IntegerOption(config.longPressCountConfig.get(), 1, 10);
		this.filterAnimation = new Option<Boolean>(config.filterAnimation.get());
		this.showHealthIndicator = new Option<Boolean>(config.showHealthIndicator.get());
	}
	
	public void resetSettings() {
		this.longPressCount.setDefaultValue();
		this.filterAnimation.setDefaultValue();
		this.showHealthIndicator.setDefaultValue();
	}
	
	public void save() {
		ConfigManager.INGAME_CONFIG.longPressCountConfig.set(this.longPressCount.getValue());
		ConfigManager.INGAME_CONFIG.filterAnimation.set(this.filterAnimation.getValue());
		ConfigManager.INGAME_CONFIG.showHealthIndicator.set(this.showHealthIndicator.getValue());
	}
}