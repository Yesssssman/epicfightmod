package yesman.epicfight.config;

import java.util.List;
import java.util.function.BiFunction;

import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import yesman.epicfight.api.utils.math.Vec2i;

public class ClientConfig {
	public final IntValue longPressCountConfig;
	public final BooleanValue filterAnimation;
	public final DoubleValue aimHelperColor;
	public final BooleanValue enableAimHelper;
	public final BooleanValue cameraAutoSwitch;
	public final BooleanValue autoPreparation;
	public final BooleanValue bloodEffects;
	public final BooleanValue noMiningInCombat;
	
	public final ConfigValue<List<? extends String>> battleAutoSwitchItems;
	public final ConfigValue<List<? extends String>> miningAutoSwitchItems;
	
	public final BooleanValue showTargetIndicator;
	public final EnumValue<HealthBarShowOptions> healthBarShowOption;
	
	public final ConfigValue<Integer> staminaBarX;
	public final ConfigValue<Integer> staminaBarY;
	public final EnumValue<HorizontalBasis> staminaBarXBase;
	public final EnumValue<VerticalBasis> staminaBarYBase;
	
	public final ConfigValue<Integer> weaponInnateX;
	public final ConfigValue<Integer> weaponInnateY;
	public final EnumValue<HorizontalBasis> weaponInnateXBase;
	public final EnumValue<VerticalBasis> weaponInnateYBase;
	
	public final ConfigValue<Integer> passivesX;
	public final ConfigValue<Integer> passivesY;
	public final EnumValue<HorizontalBasis> passivesXBase;
	public final EnumValue<VerticalBasis> passivesYBase;
	public final EnumValue<AlignDirection> passivesAlignDirection;
	
	public final ConfigValue<Integer> chargingBarX;
	public final ConfigValue<Integer> chargingBarY;
	public final EnumValue<HorizontalBasis> chargingBarXBase;
	public final EnumValue<VerticalBasis> chargingBarYBase;
	
	public ClientConfig(ForgeConfigSpec.Builder config) {
		this.longPressCountConfig = config.defineInRange("ingame.long_press_count", 2, 1, 10);
		this.healthBarShowOption = config.defineEnum("ingame.health_bar_show_option", HealthBarShowOptions.HURT);
		this.showTargetIndicator = config.define("ingame.show_target_indicator", () -> true);
		this.filterAnimation = config.define("ingame.filter_animation", () -> false);
		this.aimHelperColor = config.defineInRange("ingame.laser_pointer_color", 0.328125D, 0.0D, 1.0D);
		this.enableAimHelper = config.define("ingame.enable_laser_pointer", () -> true);
		this.cameraAutoSwitch = config.define("ingame.camera_auto_switch", () -> false);
		this.autoPreparation = config.define("ingame.auto_preparation", () -> false);
		this.bloodEffects = config.define("ingame.blood_effects", () -> true);
		this.noMiningInCombat = config.define("ingame.no_mining_in_combat", () -> true);
		
		this.battleAutoSwitchItems = config.defineList("ingame.battle_autoswitch_items", Lists.newArrayList(), (element) -> {
			if (element instanceof String str) {
				return str.contains(":");
			}
			
			return false;
		});
		this.miningAutoSwitchItems = config.defineList("ingame.mining_autoswitch_items", Lists.newArrayList(), (element) -> {
			if (element instanceof String str) {
				return str.contains(":");
			}
			
			return false;
		});
		
		this.staminaBarX = config.define("ingame.ui.stamina_bar_x", 120);
		this.staminaBarY = config.define("ingame.ui.stamina_bar_y", 10);
		this.staminaBarXBase = config.defineEnum("ingame.ui.stamina_bar_x_base", HorizontalBasis.RIGHT);
		this.staminaBarYBase = config.defineEnum("ingame.ui.stamina_bar_y_base", VerticalBasis.BOTTOM);
		
		this.weaponInnateX = config.define("ingame.ui.weapon_innate_x", 42);
		this.weaponInnateY = config.define("ingame.ui.weapon_innate_y", 48);
		this.weaponInnateXBase = config.defineEnum("ingame.ui.weapon_innate_x_base", HorizontalBasis.RIGHT);
		this.weaponInnateYBase = config.defineEnum("ingame.ui.weapon_innate_y_base", VerticalBasis.BOTTOM);
		
		this.passivesX = config.define("ingame.ui.passives_x", 70);
		this.passivesY = config.define("ingame.ui.passives_y", 36);
		this.passivesXBase = config.defineEnum("ingame.ui.passives_x_base", HorizontalBasis.RIGHT);
		this.passivesYBase = config.defineEnum("ingame.ui.passives_y_base", VerticalBasis.BOTTOM);
		this.passivesAlignDirection = config.defineEnum("ingame.ui.passives_align_direction", AlignDirection.HORIZONTAL);
		
		this.chargingBarX = config.define("ingame.ui.charging_bar_x", -119);
		this.chargingBarY = config.define("ingame.ui.charging_bar_y", 60);
		this.chargingBarXBase = config.defineEnum("ingame.ui.charging_bar_x_base", HorizontalBasis.CENTER);
		this.chargingBarYBase = config.defineEnum("ingame.ui.charging_bar_y_base", VerticalBasis.CENTER);
	}
	
	private static final BiFunction<Integer, Integer, Integer> ORIGIN = ((screenLength, value) -> value);
	private static final BiFunction<Integer, Integer, Integer> SCREEN_EDGE = ((screenLength, value) -> screenLength - value);
	private static final BiFunction<Integer, Integer, Integer> CENTER = ((screenLength, value) -> screenLength / 2 + value);
	private static final BiFunction<Integer, Integer, Integer> CENTER_SAVE = ((screenLength, value) -> value - screenLength / 2);
	
	public enum HorizontalBasis {
		LEFT(ClientConfig.ORIGIN, ClientConfig.ORIGIN), RIGHT(ClientConfig.SCREEN_EDGE, ClientConfig.SCREEN_EDGE), CENTER(ClientConfig.CENTER, ClientConfig.CENTER_SAVE);
		
		public BiFunction<Integer, Integer, Integer> positionGetter;
		public BiFunction<Integer, Integer, Integer> saveCoordGetter;
		
		HorizontalBasis(BiFunction<Integer, Integer, Integer> positionGetter, BiFunction<Integer, Integer, Integer> saveCoordGetter) {
			this.positionGetter = positionGetter;
			this.saveCoordGetter = saveCoordGetter;
		}
	}
	
	public enum VerticalBasis {
		TOP(ClientConfig.ORIGIN, ClientConfig.ORIGIN), BOTTOM(ClientConfig.SCREEN_EDGE, ClientConfig.SCREEN_EDGE), CENTER(ClientConfig.CENTER, ClientConfig.CENTER_SAVE);
		
		public BiFunction<Integer, Integer, Integer> positionGetter;
		public BiFunction<Integer, Integer, Integer> saveCoordGetter;
		
		VerticalBasis(BiFunction<Integer, Integer, Integer> positionGetter, BiFunction<Integer, Integer, Integer> saveCoordGetter) {
			this.positionGetter = positionGetter;
			this.saveCoordGetter = saveCoordGetter;
		}
	}
	
	@FunctionalInterface
	public interface StartCoordGetter {
		Vec2i get(int x, int y, int width, int height, int icons, HorizontalBasis horBasis, VerticalBasis verBasis);
	}
	
	private static final StartCoordGetter START_HORIZONTAL = (x, y, width, height, icons, horBasis, verBasis) -> {
		if (horBasis == HorizontalBasis.CENTER) {
			return new Vec2i(x - width * (icons - 1) / 2, y);
		} else {
			return new Vec2i(x, y);
		}
	};
	
	private static final StartCoordGetter START_VERTICAL = (x, y, width, height, icons, horBasis, verBasis) -> {
		if (verBasis == VerticalBasis.CENTER) {
			return new Vec2i(x, y - height * (icons - 1) / 2);
		} else {
			return new Vec2i(x, y);
		}
	};
	
	@FunctionalInterface
	public interface NextCoordGetter {
		Vec2i getNext(HorizontalBasis horBasis, VerticalBasis verBasis, Vec2i prevCoord, int width, int height);
	}
	
	private static final NextCoordGetter NEXT_HORIZONTAL = (horBasis, verBasis, oldPos, width, height) -> {
		if (horBasis == HorizontalBasis.LEFT || horBasis == HorizontalBasis.CENTER) {
			return new Vec2i(oldPos.x + width, oldPos.y);
		} else {
			return new Vec2i(oldPos.x - width, oldPos.y);
		}
	};
	
	private static final NextCoordGetter NEXT_VERTICAL = (horBasis, verBasis, oldPos, width, height) -> {
		if (verBasis == VerticalBasis.TOP || verBasis == VerticalBasis.CENTER) {
			return new Vec2i(oldPos.x, oldPos.y + height);
		} else {
			return new Vec2i(oldPos.x, oldPos.y - height);
		}
	};
	
	public enum AlignDirection {
		HORIZONTAL(START_HORIZONTAL, NEXT_HORIZONTAL), VERTICAL(START_VERTICAL, NEXT_VERTICAL);
		
		public StartCoordGetter startCoordGetter;
		public NextCoordGetter nextPositionGetter;
		
		AlignDirection(StartCoordGetter startCoordGetter, NextCoordGetter nextPositionGetter) {
			this.startCoordGetter = startCoordGetter;
			this.nextPositionGetter = nextPositionGetter;
		}
	}
	
	public enum HealthBarShowOptions {
		NONE, HURT, TARGET;
		
		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
		
		public HealthBarShowOptions nextOption() {
			return HealthBarShowOptions.values()[(this.ordinal() + 1) % 3];
		}
	}
}