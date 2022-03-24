package yesman.epicfight.world;

import net.minecraft.world.level.GameRules;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeGamerule;
import yesman.epicfight.network.server.SPChangeGamerule.Gamerules;

public class EpicFightGamerules {
	public static GameRules.Key<GameRules.BooleanValue> DO_VANILLA_ATTACK;
	public static GameRules.Key<GameRules.BooleanValue> HAS_FALL_ANIMATION;
	public static GameRules.Key<GameRules.IntegerValue> WEIGHT_PENALTY;
	public static GameRules.Key<GameRules.BooleanValue> KEEP_SKILLS;
	
	public static void registerRules() {
		DO_VANILLA_ATTACK = GameRules.register("doVanillaAttack", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
		HAS_FALL_ANIMATION = GameRules.register("hasFallAnimation", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true, (server, value) -> {
			EpicFightNetworkManager.sendToAll(new SPChangeGamerule(Gamerules.HAS_FALL_ANIMATION, value.get()));
		}));
		WEIGHT_PENALTY = GameRules.register("weightPenalty", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100, (server, value) -> {
			EpicFightNetworkManager.sendToAll(new SPChangeGamerule(Gamerules.SPEED_PENALTY_PERCENT, value.get()));
		}));
		KEEP_SKILLS = GameRules.register("keepSkills", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
	}
}