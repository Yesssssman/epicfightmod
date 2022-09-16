package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;

public class DragonCrystalLinkPhase extends PatchedDragonPhase {
	public static final float STUN_SHIELD_AMOUNT = 20.0F;
	public static final int CHARGING_TICK = 158;
	private int chargingCount;
	private EnderCrystalEntity linkingCrystal;
	
	public DragonCrystalLinkPhase(EnderDragonEntity dragon) {
		super(dragon);
	}
	
	@Override
	public void begin() {
		this.dragonpatch.getAnimator().playAnimation(Animations.DRAGON_CRYSTAL_LINK, 0.0F);
		this.dragon.level.playLocalSound(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), EpicFightSounds.ENDER_DRAGON_CRYSTAL_LINK, this.dragon.getSoundSource(), 10.0F, 1.0F, false);
		BlockPos blockpos = this.dragon.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
		List<EnderCrystalEntity> list = this.dragon.level.getEntitiesOfClass(EnderCrystalEntity.class, new AxisAlignedBB(blockpos).inflate(200.0D));
		EnderCrystalEntity nearestCrystal = null;
		double d0 = Double.MAX_VALUE;
		
		for (EnderCrystalEntity endcrystal : list) {
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
			Vector3d correction = this.dragon.getLookAngle().multiply(2.0D, 0.0D, 2.0D).subtract(0.0D, 2.0D, 0.0D);
			Vector3d spawnPosition = this.dragon.position().subtract(correction);
			
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
			this.dragon.level.explode((Entity)null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 6.0F, Explosion.Mode.DESTROY);
		}
	}
	
	@Override
	public float onHurt(DamageSource damagesource, float amount) {
		if (damagesource instanceof ExtendedDamageSource) {
			float impact = ((ExtendedDamageSource)damagesource).getImpact();
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
	
	public EnderCrystalEntity getLinkingCrystal() {
		return this.linkingCrystal;
	}
	
	@Override
	public boolean isSitting() {
		return true;
	}
	
	@Override
	public PhaseType<DragonCrystalLinkPhase> getPhase() {
		return PatchedPhases.CRYSTAL_LINK;
	}
}