package yesman.epicfight.world;

import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.RuleKey;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCGameruleChange;
import yesman.epicfight.network.server.STCGameruleChange.Gamerules;

public class ModGamerules {
	public static RuleKey<GameRules.BooleanValue> DO_VANILLA_ATTACK;
	public static RuleKey<GameRules.BooleanValue> HAS_FALL_ANIMATION;
	public static RuleKey<GameRules.IntegerValue> WEIGHT_PENALTY;
	public static RuleKey<GameRules.BooleanValue> KEEP_SKILLS;
	
	public static void registerRules() {
		DO_VANILLA_ATTACK = GameRules.register("doVanillaAttack", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
		HAS_FALL_ANIMATION = GameRules.register("hasFallAnimation", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true, (server, value) -> {
			ModNetworkManager.sendToAll(new STCGameruleChange(Gamerules.HAS_FALL_ANIMATION, value.get()));
		}));
		WEIGHT_PENALTY = GameRules.register("weightPenalty", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100, (server, value) -> {
			ModNetworkManager.sendToAll(new STCGameruleChange(Gamerules.SPEED_PENALTY_PERCENT, value.get()));
		}));
		KEEP_SKILLS = GameRules.register("keepSkills", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
	}
}