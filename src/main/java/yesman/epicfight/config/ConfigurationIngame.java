package yesman.epicfight.config;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.widget.ColorSlider;
import yesman.epicfight.config.Option.DoubleOption;
import yesman.epicfight.config.Option.IntegerOption;

public class ConfigurationIngame {
	public static final float A_TICK = 0.05F;
	public static final float GENERAL_ANIMATION_CONVERT_TIME = 0.15F;
	
	public final IntegerOption longPressCount;
	public final Option<Boolean> filterAnimation;
	public final Option<ClientConfig.HealthBarShowOptions> healthBarShowOption;
	public final Option<Boolean> showTargetIndicator;
	public final DoubleOption aimHelperColor;
	public final Option<Boolean> enableAimHelperPointer;
	public final Option<Boolean> cameraAutoSwitch;
	public final Option<Boolean> autoPreparation;
	public final Option<Boolean> offBloodEffects;
	public final Option<Boolean> noMiningInCombat;
	public final List<Item> battleAutoSwitchItems;
	public final List<Item> miningAutoSwitchItems;
	public int aimHelperRealColor;
	
	public final Option<Integer> staminaBarX;
	public final Option<Integer> staminaBarY;
	public final Option<ClientConfig.HorizontalBasis> staminaBarXBase;
	public final Option<ClientConfig.VerticalBasis> staminaBarYBase;
	
	public final Option<Integer> weaponInnateX;
	public final Option<Integer> weaponInnateY;
	public final Option<ClientConfig.HorizontalBasis> weaponInnateXBase;
	public final Option<ClientConfig.VerticalBasis> weaponInnateYBase;
	
	public final Option<Integer> passivesX;
	public final Option<Integer> passivesY;
	public final Option<ClientConfig.HorizontalBasis> passivesXBase;
	public final Option<ClientConfig.VerticalBasis> passivesYBase;
	public final Option<ClientConfig.AlignDirection> passivesAlignDirection;
	
	public final Option<Integer> chargingBarX;
	public final Option<Integer> chargingBarY;
	public final Option<ClientConfig.HorizontalBasis> chargingBarXBase;
	public final Option<ClientConfig.VerticalBasis> chargingBarYBase;
	
	public ConfigurationIngame() {
		ClientConfig config = ConfigManager.INGAME_CONFIG;
		this.longPressCount = new IntegerOption(config.longPressCountConfig.get(), 1, 10);
		this.filterAnimation = new Option<Boolean>(config.filterAnimation.get());
		this.healthBarShowOption = new Option<ClientConfig.HealthBarShowOptions>(config.healthBarShowOption.get());
		this.showTargetIndicator = new Option<Boolean>(config.showTargetIndicator.get());
		this.aimHelperColor = new DoubleOption(config.aimHelperColor.get(), 0.0D, 1.0D);
		this.enableAimHelperPointer = new Option<Boolean>(config.enableAimHelper.get());
		this.aimHelperRealColor = ColorSlider.toColorInteger(config.aimHelperColor.get());
		this.cameraAutoSwitch = new Option<Boolean>(config.cameraAutoSwitch.get());
		this.autoPreparation = new Option<Boolean>(config.autoPreparation.get());
		this.offBloodEffects = new Option<Boolean>(config.offBloodEffects.get());
		this.noMiningInCombat = new Option<Boolean>(config.noMiningInCombat.get());
		this.battleAutoSwitchItems = Lists.newArrayList(config.battleAutoSwitchItems.get().stream().map((itemName) ->
			ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).iterator()
		);
		this.miningAutoSwitchItems = Lists.newArrayList(config.miningAutoSwitchItems.get().stream().map((itemName) ->
			ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).iterator()
		);
		
		this.staminaBarX = new Option<Integer>(config.staminaBarX.get());
		this.staminaBarY = new Option<Integer>(config.staminaBarY.get());
		this.staminaBarXBase = new Option<ClientConfig.HorizontalBasis>(config.staminaBarXBase.get());
		this.staminaBarYBase = new Option<ClientConfig.VerticalBasis>(config.staminaBarYBase.get());
		
		this.weaponInnateX = new Option<Integer>(config.weaponInnateX.get());
		this.weaponInnateY = new Option<Integer>(config.weaponInnateY.get());
		this.weaponInnateXBase = new Option<ClientConfig.HorizontalBasis>(config.weaponInnateXBase.get());
		this.weaponInnateYBase = new Option<ClientConfig.VerticalBasis>(config.weaponInnateYBase.get());
		
		this.passivesX = new Option<Integer>(config.passivesX.get());
		this.passivesY = new Option<Integer>(config.passivesY.get());
		this.passivesXBase = new Option<ClientConfig.HorizontalBasis>(config.passivesXBase.get());
		this.passivesYBase = new Option<ClientConfig.VerticalBasis>(config.passivesYBase.get());
		this.passivesAlignDirection = new Option<ClientConfig.AlignDirection>(config.passivesAlignDirection.get());
		
		this.chargingBarX = new Option<Integer>(config.chargingBarX.get());
		this.chargingBarY = new Option<Integer>(config.chargingBarY.get());
		this.chargingBarXBase = new Option<ClientConfig.HorizontalBasis>(config.chargingBarXBase.get());
		this.chargingBarYBase = new Option<ClientConfig.VerticalBasis>(config.chargingBarYBase.get());
	}
	
	public void resetSettings() {
		this.longPressCount.setDefaultValue();
		this.filterAnimation.setDefaultValue();
		this.healthBarShowOption.setDefaultValue();
		this.showTargetIndicator.setDefaultValue();
		this.aimHelperColor.setDefaultValue();
		this.enableAimHelperPointer.setDefaultValue();
		this.cameraAutoSwitch.setDefaultValue();
		this.autoPreparation.setDefaultValue();
		this.offBloodEffects.setDefaultValue();
		this.noMiningInCombat.setDefaultValue();
		this.aimHelperRealColor = ColorSlider.toColorInteger(this.aimHelperColor.getValue());
		this.staminaBarX.setDefaultValue();
		this.staminaBarY.setDefaultValue();
		this.staminaBarXBase.setDefaultValue();
		this.staminaBarYBase.setDefaultValue();
		this.weaponInnateX.setDefaultValue();
		this.weaponInnateY.setDefaultValue();
		this.weaponInnateXBase.setDefaultValue();
		this.weaponInnateYBase.setDefaultValue();
		this.passivesX.setDefaultValue();
		this.passivesY.setDefaultValue();
		this.passivesXBase.setDefaultValue();
		this.passivesYBase.setDefaultValue();
		this.passivesAlignDirection.setDefaultValue();
		this.chargingBarX.setDefaultValue();
		this.chargingBarY.setDefaultValue();
		this.chargingBarXBase.setDefaultValue();
		this.chargingBarYBase.setDefaultValue();
	}
	
	public void save() {
		ClientConfig config = ConfigManager.INGAME_CONFIG;
		config.longPressCountConfig.set(this.longPressCount.getValue());
		config.filterAnimation.set(this.filterAnimation.getValue());
		config.healthBarShowOption.set(this.healthBarShowOption.getValue());
		config.showTargetIndicator.set(this.showTargetIndicator.getValue());
		config.aimHelperColor.set(this.aimHelperColor.getValue());
		config.enableAimHelper.set(this.enableAimHelperPointer.getValue());
		config.cameraAutoSwitch.set(this.cameraAutoSwitch.getValue());
		config.autoPreparation.set(this.autoPreparation.getValue());
		config.offBloodEffects.set(this.offBloodEffects.getValue());
		config.noMiningInCombat.set(this.noMiningInCombat.getValue());
		this.aimHelperRealColor = ColorSlider.toColorInteger(this.aimHelperColor.getValue());
		config.battleAutoSwitchItems.set(Lists.newArrayList(this.battleAutoSwitchItems.stream().map((item) -> item.getRegistryName().toString()).iterator()));
		config.miningAutoSwitchItems.set(Lists.newArrayList(this.miningAutoSwitchItems.stream().map((item) -> item.getRegistryName().toString()).iterator()));
		config.staminaBarX.set(this.staminaBarX.getValue());
		config.staminaBarY.set(this.staminaBarY.getValue());
		config.staminaBarXBase.set(this.staminaBarXBase.getValue());
		config.staminaBarYBase.set(this.staminaBarYBase.getValue());
		config.weaponInnateX.set(this.weaponInnateX.getValue());
		config.weaponInnateY.set(this.weaponInnateY.getValue());
		config.weaponInnateXBase.set(this.weaponInnateXBase.getValue());
		config.weaponInnateYBase.set(this.weaponInnateYBase.getValue());
		config.passivesX.set(this.passivesX.getValue());
		config.passivesY.set(this.passivesY.getValue());
		config.passivesXBase.set(this.passivesXBase.getValue());
		config.passivesYBase.set(this.passivesYBase.getValue());
		config.passivesAlignDirection.set(this.passivesAlignDirection.getValue());
	}
	
	public Vec2i getStaminaPosition(int width, int height) {
		int posX = this.staminaBarXBase.getValue().positionGetter.apply(width, this.staminaBarX.getValue());
		int posY = this.staminaBarYBase.getValue().positionGetter.apply(height, this.staminaBarY.getValue());
		
		return new Vec2i(posX, posY);
	}
	
	public Vec2i getWeaponInnatePosition(int width, int height) {
		int posX = this.weaponInnateXBase.getValue().positionGetter.apply(width, this.weaponInnateX.getValue());
		int posY = this.weaponInnateYBase.getValue().positionGetter.apply(height, this.weaponInnateY.getValue());
		
		return new Vec2i(posX, posY);
	}
	
	public Vec2i getChargingBarPosition(int width, int height) {
		int posX = this.chargingBarXBase.getValue().positionGetter.apply(width, this.chargingBarX.getValue());
		int posY = this.chargingBarYBase.getValue().positionGetter.apply(height, this.chargingBarY.getValue());
		
		return new Vec2i(posX, posY);
	}
}