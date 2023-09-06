package yesman.epicfight.world.capabilities.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.server.SPPlayAnimationInstant;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.mob.WitherSkeletonPatch;
import yesman.epicfight.world.entity.EpicFightEntities;
import yesman.epicfight.world.entity.WitherSkeletonMinion;

public class WitherSkullPatch extends ProjectilePatch<WitherSkull> {
	@Override
	public void onJoinWorld(WitherSkull projectileEntity, EntityJoinWorldEvent event) {
		super.onJoinWorld(projectileEntity, event);
		this.impact = 1.0F;
	}
	
	@Override
	protected void setMaxStrikes(WitherSkull projectileEntity, int maxStrikes) {
		
	}
	
	@Override
	public boolean onProjectileImpact(ProjectileImpactEvent event) {
		if (!(event.getRayTraceResult() instanceof EntityHitResult)) {
			if (Math.random() < 0.2D) {
				Vec3 location = event.getRayTraceResult().getLocation();
				BlockPos blockpos = new BlockPos(location);
				Projectile projectile = event.getProjectile();
				ServerLevel level = (ServerLevel)projectile.level;
				EntityType<?> entityType = EpicFightEntities.WITHER_SKELETON_MINION.get();
				
				if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(entityType), level, blockpos, entityType) && SpawnPlacements.checkSpawnRules(entityType, level, MobSpawnType.REINFORCEMENT, blockpos, level.random)) {
					WitherBoss summoner = (projectile.getOwner() instanceof WitherBoss) ? ((WitherBoss)projectile.getOwner()) : null;
					WitherSkeletonMinion witherskeletonminion = new WitherSkeletonMinion(level, summoner, projectile.getX(), projectile.getY() + 0.1D, projectile.getZ());
					witherskeletonminion.finalizeSpawn(level, level.getCurrentDifficultyAt(blockpos), MobSpawnType.REINFORCEMENT, (SpawnGroupData)null, (CompoundTag)null);
					witherskeletonminion.setYRot(projectile.getYRot() - 180.0F);
					level.addFreshEntity(witherskeletonminion);
					
					WitherSkeletonPatch<?> witherskeletonpatch = EpicFightCapabilities.getEntityPatch(witherskeletonminion, WitherSkeletonPatch.class);
					witherskeletonpatch.playAnimationSynchronized(Animations.WITHER_SKELETON_SPECIAL_SPAWN, 0, SPPlayAnimationInstant::new);
				}
			}
		} else {
			if (((EntityHitResult)event.getRayTraceResult()).getEntity() instanceof WitherSkeletonMinion) {
				return true;
			}
		}
		
		return false;
	}
}