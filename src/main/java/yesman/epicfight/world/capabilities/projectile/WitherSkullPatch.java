package yesman.epicfight.world.capabilities.projectile;

import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.server.SPPlayAnimationInstant;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.mob.WitherSkeletonPatch;
import yesman.epicfight.world.entity.EpicFightEntities;
import yesman.epicfight.world.entity.WitherSkeletonMinion;

public class WitherSkullPatch extends ProjectilePatch<WitherSkullEntity> {
	@Override
	public void onJoinWorld(WitherSkullEntity projectileEntity, EntityJoinWorldEvent event) {
		super.onJoinWorld(projectileEntity, event);
		this.impact = 1.0F;
	}
	
	@Override
	protected void setMaxStrikes(WitherSkullEntity projectileEntity, int maxStrikes) {
		
	}
	
	@Override
	public boolean onProjectileImpact(ProjectileImpactEvent event) {
		if (!(event.getRayTraceResult() instanceof EntityRayTraceResult)) {
			if (Math.random() < 0.5D) {
				Vector3d location = event.getRayTraceResult().getLocation();
				BlockPos blockpos = new BlockPos(location);
				ProjectileEntity projectile = (ProjectileEntity) event.getEntity();
				ServerWorld level = (ServerWorld)projectile.level;
				EntityType<?> entityType = EpicFightEntities.WITHER_SKELETON_MINION.get();
				
				if (WorldEntitySpawner.isSpawnPositionOk(EntitySpawnPlacementRegistry.getPlacementType(entityType), level, blockpos, entityType) &&
						EntitySpawnPlacementRegistry.checkSpawnRules(entityType, level, SpawnReason.REINFORCEMENT, blockpos, level.random)) {
					WitherEntity summoner = (projectile.getOwner() instanceof WitherEntity) ? ((WitherEntity)projectile.getOwner()) : null;
					WitherSkeletonMinion witherskeletonminion = new WitherSkeletonMinion(level, summoner, projectile.getX(), projectile.getY() + 0.1D, projectile.getZ());
					witherskeletonminion.finalizeSpawn(level, level.getCurrentDifficultyAt(blockpos), SpawnReason.REINFORCEMENT, null, null);
					witherskeletonminion.yRot = projectile.yRot - 180.0F;
					level.addFreshEntity(witherskeletonminion);
					
					WitherSkeletonPatch<?> witherskeletonpatch = (WitherSkeletonPatch<?>)witherskeletonminion.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
					witherskeletonpatch.playAnimationSynchronized(Animations.WITHER_SKELETON_SPECIAL_SPAWN, 0, SPPlayAnimationInstant::new);
				}
			}
		} else {
			if (((EntityRayTraceResult)event.getRayTraceResult()).getEntity() instanceof WitherSkeletonMinion) {
				return true;
			}
		}
		
		return false;
	}
}