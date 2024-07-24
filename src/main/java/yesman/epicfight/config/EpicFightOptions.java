package yesman.epicfight.config;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.widgets.ColorWidget;
import yesman.epicfight.config.OptionHandler.BooleanOptionHandler;
import yesman.epicfight.config.OptionHandler.DoubleOptionHandler;
import yesman.epicfight.config.OptionHandler.IntegerOptionHandler;

public class EpicFightOptions {
	public static final float A_TICK = 0.05F;
	public static final float GENERAL_ANIMATION_CONVERT_TIME = 0.15F;
	
	public final IntegerOptionHandler longPressCount;
	public final BooleanOptionHandler filterAnimation;
	public final OptionHandler<ClientConfig.HealthBarShowOptions> healthBarShowOption;
	public final BooleanOptionHandler showTargetIndicator;
	public final DoubleOptionHandler aimHelperColor;
	public final BooleanOptionHandler enableAimHelperPointer;
	public final BooleanOptionHandler cameraAutoSwitch;
	public final BooleanOptionHandler autoPreparation;
	public final BooleanOptionHandler bloodEffects;
	public final BooleanOptionHandler noMiningInCombat;
	public final BooleanOptionHandler aimingCorrection;
	public final BooleanOptionHandler showEpicFightAttributes;
	public final IntegerOptionHandler maxStuckProjectiles;
	public final BooleanOptionHandler useAnimationShader;
	public final Set<Item> battleAutoSwitchItems;
	public final Set<Item> miningAutoSwitchItems;
	public int aimHelperRealColor;
	
	public final OptionHandler<Integer> staminaBarX;
	public final OptionHandler<Integer> staminaBarY;
	public final OptionHandler<ClientConfig.HorizontalBasis> staminaBarXBase;
	public final OptionHandler<ClientConfig.VerticalBasis> staminaBarYBase;
	
	public final OptionHandler<Integer> weaponInnateX;
	public final OptionHandler<Integer> weaponInnateY;
	public final OptionHandler<ClientConfig.HorizontalBasis> weaponInnateXBase;
	public final OptionHandler<ClientConfig.VerticalBasis> weaponInnateYBase;
	
	public final OptionHandler<Integer> passivesX;
	public final OptionHandler<Integer> passivesY;
	public final OptionHandler<ClientConfig.HorizontalBasis> passivesXBase;
	public final OptionHandler<ClientConfig.VerticalBasis> passivesYBase;
	public final OptionHandler<ClientConfig.AlignDirection> passivesAlignDirection;
	
	public final OptionHandler<Integer> chargingBarX;
	public final OptionHandler<Integer> chargingBarY;
	public final OptionHandler<ClientConfig.HorizontalBasis> chargingBarXBase;
	public final OptionHandler<ClientConfig.VerticalBasis> chargingBarYBase;
	
	public EpicFightOptions() {
		ClientConfig config = ConfigManager.INGAME_CONFIG;
		this.longPressCount = new IntegerOptionHandler(config.longPressCountConfig.get(), 1, 10);
		this.filterAnimation = new BooleanOptionHandler(config.filterAnimation.get());
		this.healthBarShowOption = new OptionHandler<ClientConfig.HealthBarShowOptions>(config.healthBarShowOption.get());
		this.showTargetIndicator = new BooleanOptionHandler(config.showTargetIndicator.get());
		this.aimHelperColor = new DoubleOptionHandler(config.aimHelperColor.get(), 0.0D, 1.0D);
		this.enableAimHelperPointer = new BooleanOptionHandler(config.enableAimHelper.get());
		this.aimHelperRealColor = ColorWidget.toColorInteger(config.aimHelperColor.get());
		this.cameraAutoSwitch = new BooleanOptionHandler(config.cameraAutoSwitch.get());
		this.autoPreparation = new BooleanOptionHandler(config.autoPreparation.get());
		this.bloodEffects = new BooleanOptionHandler(config.bloodEffects.get());
		this.noMiningInCombat = new BooleanOptionHandler(config.noMiningInCombat.get());
		this.aimingCorrection = new BooleanOptionHandler(config.aimingCorrection.get());
		this.showEpicFightAttributes = new BooleanOptionHandler(config.showEpicFightAttributes.get());
		this.maxStuckProjectiles = new IntegerOptionHandler(config.maxStuckProjectiles.get(), 1, 30);
		this.useAnimationShader = new BooleanOptionHandler(config.useAnimationShader.get());
		
		this.battleAutoSwitchItems = config.battleAutoSwitchItems.get().stream()
				.map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		this.miningAutoSwitchItems = config.miningAutoSwitchItems.get().stream()
				.map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		this.staminaBarX = new OptionHandler<Integer>(config.staminaBarX.get());
		this.staminaBarY = new OptionHandler<Integer>(config.staminaBarY.get());
		this.staminaBarXBase = new OptionHandler<ClientConfig.HorizontalBasis>(config.staminaBarXBase.get());
		this.staminaBarYBase = new OptionHandler<ClientConfig.VerticalBasis>(config.staminaBarYBase.get());

		this.weaponInnateX = new OptionHandler<Integer>(config.weaponInnateX.get());
		this.weaponInnateY = new OptionHandler<Integer>(config.weaponInnateY.get());
		this.weaponInnateXBase = new OptionHandler<ClientConfig.HorizontalBasis>(config.weaponInnateXBase.get());
		this.weaponInnateYBase = new OptionHandler<ClientConfig.VerticalBasis>(config.weaponInnateYBase.get());

		this.passivesX = new OptionHandler<Integer>(config.passivesX.get());
		this.passivesY = new OptionHandler<Integer>(config.passivesY.get());
		this.passivesXBase = new OptionHandler<ClientConfig.HorizontalBasis>(config.passivesXBase.get());
		this.passivesYBase = new OptionHandler<ClientConfig.VerticalBasis>(config.passivesYBase.get());
		this.passivesAlignDirection = new OptionHandler<ClientConfig.AlignDirection>(config.passivesAlignDirection.get());

		this.chargingBarX = new OptionHandler<Integer>(config.chargingBarX.get());
		this.chargingBarY = new OptionHandler<Integer>(config.chargingBarY.get());
		this.chargingBarXBase = new OptionHandler<ClientConfig.HorizontalBasis>(config.chargingBarXBase.get());
		this.chargingBarYBase = new OptionHandler<ClientConfig.VerticalBasis>(config.chargingBarYBase.get());
	}
	
	public void resetSettings() {
		this.longPressCount.setDefaultValue();
		this.maxStuckProjectiles.setDefaultValue();
		this.filterAnimation.setDefaultValue();
		this.healthBarShowOption.setDefaultValue();
		this.showTargetIndicator.setDefaultValue();
		this.aimHelperColor.setDefaultValue();
		this.enableAimHelperPointer.setDefaultValue();
		this.cameraAutoSwitch.setDefaultValue();
		this.autoPreparation.setDefaultValue();
		this.bloodEffects.setDefaultValue();
		this.noMiningInCombat.setDefaultValue();
		this.aimingCorrection.setDefaultValue();
		this.showEpicFightAttributes.setDefaultValue();
		this.aimHelperRealColor = ColorWidget.toColorInteger(this.aimHelperColor.getValue());
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
		config.maxStuckProjectiles.set(this.maxStuckProjectiles.getValue());
		config.filterAnimation.set(this.filterAnimation.getValue());
		config.healthBarShowOption.set(this.healthBarShowOption.getValue());
		config.showTargetIndicator.set(this.showTargetIndicator.getValue());
		config.aimHelperColor.set(this.aimHelperColor.getValue());
		config.enableAimHelper.set(this.enableAimHelperPointer.getValue());
		config.cameraAutoSwitch.set(this.cameraAutoSwitch.getValue());
		config.autoPreparation.set(this.autoPreparation.getValue());
		config.bloodEffects.set(this.bloodEffects.getValue());
		config.noMiningInCombat.set(this.noMiningInCombat.getValue());
		config.aimingCorrection.set(this.aimingCorrection.getValue());
		config.showEpicFightAttributes.set(this.showEpicFightAttributes.getValue());
		this.aimHelperRealColor = ColorWidget.toColorInteger(this.aimHelperColor.getValue());
		config.battleAutoSwitchItems.set(Lists.newArrayList(this.battleAutoSwitchItems.stream().map((item) -> ForgeRegistries.ITEMS.getKey(item).toString()).iterator()));
		config.miningAutoSwitchItems.set(Lists.newArrayList(this.miningAutoSwitchItems.stream().map((item) -> ForgeRegistries.ITEMS.getKey(item).toString()).iterator()));
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