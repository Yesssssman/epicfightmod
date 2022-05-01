package yesman.epicfight.world.gamerule;

import net.minecraft.world.level.GameRules;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeGamerule;
import yesman.epicfight.network.server.SPChangeGamerule.SynchronizedGameRules;

public class EpicFightGamerules {
	public static GameRules.Key<GameRules.BooleanValue> DO_VANILLA_ATTACK;
	public static GameRules.Key<GameRules.BooleanValue> HAS_FALL_ANIMATION;
	public static GameRules.Key<GameRules.IntegerValue> WEIGHT_PENALTY;
	public static GameRules.Key<GameRules.BooleanValue> KEEP_SKILLS;
	public static GameRules.Key<GameRules.BooleanValue> DISABLE_ENTITY_UI;
	
	public static void registerRules() {
		DO_VANILLA_ATTACK = GameRules.register("doVanillaAttack", GameRules.Category.PLAYER, GameRules.BooleanValue.create(ConfigManager.DO_VANILLA_ATTACK.get()));
		HAS_FALL_ANIMATION = GameRules.register("hasFallAnimation", GameRules.Category.PLAYER, GameRules.BooleanValue.create(ConfigManager.HAS_FALL_ANIMATION.get(), (server, value) -> {
			EpicFightNetworkManager.sendToAll(new SPChangeGamerule(SynchronizedGameRules.HAS_FALL_ANIMATION, value.get()));
		}));
		WEIGHT_PENALTY = GameRules.register("weightPenalty", GameRules.Category.PLAYER, GameRules.IntegerValue.create(ConfigManager.WEIGHT_PENALTY.get(), (server, value) -> {
			EpicFightNetworkManager.sendToAll(new SPChangeGamerule(SynchronizedGameRules.WEIGHT_PENALTY, value.get()));
		}));
		KEEP_SKILLS = GameRules.register("keepSkills", GameRules.Category.PLAYER, GameRules.BooleanValue.create(ConfigManager.KEEP_SKILLS.get()));
		DISABLE_ENTITY_UI = GameRules.register("disableEntityUI", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (server, value) -> {
			EpicFightNetworkManager.sendToAll(new SPChangeGamerule(SynchronizedGameRules.DIABLE_ENTITY_UI, value.get()));
		}));
	}
}