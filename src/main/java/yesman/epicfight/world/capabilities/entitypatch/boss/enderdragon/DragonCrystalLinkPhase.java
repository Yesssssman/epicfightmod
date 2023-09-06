package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DragonCrystalLinkPhase extends PatchedDragonPhase {
	public static final float STUN_SHIELD_AMOUNT = 20.0F;
	public static final int CHARGING_TICK = 158;
	private int chargingCount;
	private EndCrystal linkingCrystal;
	
	public DragonCrystalLinkPhase(EnderDragon dragon) {
		super(dragon);
	}
	
	@Override
	public void begin() {
		this.dragonpatch.getAnimator().playAnimation(Animations.DRAGON_CRYSTAL_LINK, 0.0F);
		this.dragon.level.playLocalSound(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), EpicFightSounds.ENDER_DRAGON_CRYSTAL_LINK, this.dragon.getSoundSource(), 10.0F, 1.0F, false);
		BlockPos blockpos = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
		List<EndCrystal> list = this.dragon.level.getEntitiesOfClass(EndCrystal.class, new AABB(blockpos).inflate(200.0D));
		EndCrystal nearestCrystal = null;
		double d0 = Double.MAX_VALUE;
		
		for (EndCrystal endcrystal : list) {
			double d1 = endcrystal.distanceToSqr(this.dragon);
			
			if (d1 < d0) {
				d0 = d1;
				nearestCrystal = endcrystal;
			}
		}
		
		this.linkingCrystal = nearestCrystal;
		this.chargingCount = CHARGING_TICK;
		
		if (this.dragonpatch.isLogicalClient()) {
			double x = -45.0D;
			double z = 0.0D;
			Vec3 correction = this.dragon.getLookAngle().multiply(2.0D, 0.0D, 2.0D).subtract(0.0D, 2.0D, 0.0D);
			Vec3 spawnPosition = this.dragon.position().subtract(correction);
			
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					this.dragon.level.addAlwaysVisibleParticle(EpicFightParticles.FORCE_FIELD.get(), spawnPosition.x, spawnPosition.y, spawnPosition.z, x + 90.0D * i, Double.longBitsToDouble(this.dragon.getId()), z + 90.0D * j);
				}
			}
		} else {
			if (!this.dragonpatch.isLogicalClient()) {
				int shieldCorrection = this.getPlayersNearbyWithin(100.0D).size() - 1;
				float stunShield = STUN_SHIELD_AMOUNT + 15.0F * shieldCorrection;
				
				this.dragonpatch.setMaxStunShield(stunShield);
				this.dragonpatch.setStunShield(stunShield);
			}
		}
	}
	
	@Override
	public void end() {
		BlockPos blockpos = this.linkingCrystal.blockPosition();
		this.dragon.nearestCrystal = null;
		this.linkingCrystal = null;
		
		if (!this.dragonpatch.isLogicalClient()) {
			this.dragon.level.explode((Entity)null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 6.0F, Explosion.BlockInteraction.DESTROY);
		}
	}
	
	@Override
	public float onHurt(DamageSource damagesource, float amount) {
		LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(damagesource.getEntity(), LivingEntityPatch.class);
		
		if (entitypatch != null && entitypatch.getEpicFightDamageSource() != null) {
			float impact = entitypatch.getEpicFightDamageSource().getImpact();
			this.dragonpatch.setStunShield(this.dragonpatch.getStunShield() - impact);
		}
		
		return amount;
	}
	
	@Override
	public void doClientTick() {
		super.doClientTick();
		this.dragon.growlTime = 200;
		this.chargingCount--;
		this.dragon.nearestCrystal = this.linkingCrystal;
	}
	
	@Override
	public void doServerTick() {
		this.chargingCount--;
		this.dragon.ambientSoundTime = 0;
		
		if (this.chargingCount > 0) {
			this.dragon.setHealth(this.dragon.getHealth() + 0.5F);
		}
	}
	
	public int getChargingCount() {
		return this.chargingCount;
	}
	
	public EndCrystal getLinkingCrystal() {
		return this.linkingCrystal;
	}
	
	@Override
	public boolean isSitting() {
		return true;
	}
	
	@Override
	public EnderDragonPhase<? extends DragonPhaseInstance> getPhase() {
		return PatchedPhases.CRYSTAL_LINK;
	}
}