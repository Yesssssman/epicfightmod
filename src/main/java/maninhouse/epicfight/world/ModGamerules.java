package maninhouse.epicfight.world;

import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCGameruleChange;
import maninhouse.epicfight.network.server.STCGameruleChange.Gamerules;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.RuleKey;

public class ModGamerules {
	public static RuleKey<GameRules.BooleanValue> DO_VANILLA_ATTACK;
	public static RuleKey<GameRules.BooleanValue> HAS_FALL_ANIMATION;
	public static RuleKey<GameRules.IntegerValue> ATTACK_SPEED_PENALTY;
	public static RuleKey<GameRules.BooleanValue> KEEP_SKILLS;
	
	public static void registerRules() {
		DO_VANILLA_ATTACK = GameRules.register("doVanillaAttack", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
		HAS_FALL_ANIMATION = GameRules.register("hasFallAnimation", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true, (server, value) -> {
			ModNetworkManager.sendToAll(new STCGameruleChange(Gamerules.HAS_FALL_ANIMATION, value.get()));
		}));
		ATTACK_SPEED_PENALTY = GameRules.register("attackSpeedPenalty", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100, (server, value) -> {
			ModNetworkManager.sendToAll(new STCGameruleChange(Gamerules.SPEED_PENALTY_PERCENT, value.get()));
		}));
		
		KEEP_SKILLS = GameRules.register("keepSkills", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
	}
}