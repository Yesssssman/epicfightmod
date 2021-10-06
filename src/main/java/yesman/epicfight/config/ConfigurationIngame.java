package yesman.epicfight.config;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.client.gui.widget.ColorSlider;
import yesman.epicfight.config.Option.DoubleOption;
import yesman.epicfight.config.Option.IntegerOption;

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
	public final Option<Boolean> autoPreparation;
	public final Option<Boolean> offGoreEffect;
	public final List<Item> battleAutoSwitchItems;
	public final List<Item> miningAutoSwitchItems;
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
		this.autoPreparation = new Option<Boolean>(config.autoPreparation.get());
		this.offGoreEffect = new Option<Boolean>(config.offGoreEffect.get());
		this.battleAutoSwitchItems = Lists.newArrayList(config.battleAutoSwitchItems.get().stream().map((itemName) ->
			ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).iterator()
		);
		this.miningAutoSwitchItems = Lists.newArrayList(config.miningAutoSwitchItems.get().stream().map((itemName) ->
			ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).iterator()
		);
	}
	
	public void resetSettings() {
		this.longPressCount.setDefaultValue();
		this.filterAnimation.setDefaultValue();
		this.showHealthIndicator.setDefaultValue();
		this.showTargetIndicator.setDefaultValue();
		this.aimHelperColor.setDefaultValue();
		this.enableAimHelperPointer.setDefaultValue();
		this.cameraAutoSwitch.setDefaultValue();
		this.autoPreparation.setDefaultValue();
		this.offGoreEffect.setDefaultValue();
		this.aimHelperRealColor = ColorSlider.toColorInteger(this.aimHelperColor.getValue());
	}
	
	public void save() {
		IngameConfig config = ConfigManager.INGAME_CONFIG;
		config.longPressCountConfig.set(this.longPressCount.getValue());
		config.filterAnimation.set(this.filterAnimation.getValue());
		config.showHealthIndicator.set(this.showHealthIndicator.getValue());
		config.showTargetIndicator.set(this.showTargetIndicator.getValue());
		config.aimHelperColor.set(this.aimHelperColor.getValue());
		config.enableAimHelper.set(this.enableAimHelperPointer.getValue());
		config.cameraAutoSwitch.set(this.cameraAutoSwitch.getValue());
		config.autoPreparation.set(this.autoPreparation.getValue());
		config.offGoreEffect.set(this.offGoreEffect.getValue());
		this.aimHelperRealColor = ColorSlider.toColorInteger(this.aimHelperColor.getValue());
		config.battleAutoSwitchItems.set(Lists.newArrayList(this.battleAutoSwitchItems.stream().map((item) -> item.getRegistryName().toString()).iterator()));
		config.miningAutoSwitchItems.set(Lists.newArrayList(this.miningAutoSwitchItems.stream().map((item) -> item.getRegistryName().toString()).iterator()));
	}
}