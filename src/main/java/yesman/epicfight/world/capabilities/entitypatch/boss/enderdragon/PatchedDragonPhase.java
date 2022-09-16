package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public abstract class PatchedDragonPhase extends Phase {
	protected final EnderDragonPatch dragonpatch;
	
	public PatchedDragonPhase(EnderDragonEntity dragon) {
		super(dragon);
		this.dragonpatch = (EnderDragonPatch)dragon.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
	}
	
	@Override
	public void doClientTick() {
		this.dragon.oFlapTime = 0.5F;
		this.dragon.flapTime = 0.5F;
	}
	
	protected boolean isValidTarget(LivingEntity entity) {
		return this.dragon.canAttack(entity);
	}
	
	protected static boolean isInEndSpikes(LivingEntity entity) {
		BlockPos blockpos = entity.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
		return blockpos.distSqr(new Vector3i(entity.getX(), blockpos.getY(), entity.getZ())) < 2000.0D;
	}
	
	protected List<PlayerEntity> getPlayersNearbyWithin(double within) {
		return this.dragon.level.getNearbyPlayers(EnderDragonPatch.DRAGON_TARGETING, this.dragon, this.dragon.getBoundingBox().inflate(within, within, within));
	}
}