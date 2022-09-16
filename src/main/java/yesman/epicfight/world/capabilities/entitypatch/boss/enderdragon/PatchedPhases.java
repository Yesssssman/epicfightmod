package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import net.minecraft.entity.boss.dragon.phase.PhaseType;

public class PatchedPhases {
	public static final PhaseType<DragonFlyingPhase> FLYING = PhaseType.create(DragonFlyingPhase.class, "Flying");
	public static final PhaseType<DragonGroundBattlePhase> GROUND_BATTLE = PhaseType.create(DragonGroundBattlePhase.class, "Ground Battle");
	public static final PhaseType<DragonLandingPhase> LANDING = PhaseType.create(DragonLandingPhase.class, "Landing");
	public static final PhaseType<DragonAirstrikePhase> AIRSTRIKE = PhaseType.create(DragonAirstrikePhase.class, "Airstrike");
	public static final PhaseType<DragonCrystalLinkPhase> CRYSTAL_LINK = PhaseType.create(DragonCrystalLinkPhase.class, "Crystal Link");
	public static final PhaseType<DragonNeutralizedPhase> NEUTRALIZED = PhaseType.create(DragonNeutralizedPhase.class, "Neutralized");
}