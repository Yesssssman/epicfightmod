package yesman.epicfight.world.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class TargetChasingGoal extends MeleeAttackGoal {
	protected final MobPatch<? extends CreatureEntity> mobpatch;
	protected final double attackRadiusSqr;
	
	public TargetChasingGoal(MobPatch<? extends CreatureEntity> mobpatch, CreatureEntity pathfinderMob, double speedModifier, boolean longMemory) {
		this(mobpatch, pathfinderMob, speedModifier, longMemory, 0.0D);
	}
	
	public TargetChasingGoal(MobPatch<? extends CreatureEntity> mobpatch, CreatureEntity pathfinderMob, double speedModifier, boolean longMemory, double attackRadius) {
		super(pathfinderMob, speedModifier, longMemory);
		this.mobpatch = mobpatch;
		this.attackRadiusSqr = attackRadius * attackRadius;
	}
	
	@Override
	public void tick() {
		LivingEntity livingentity = this.mob.getTarget();
		
		if (livingentity != null) {
			double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
			
			if (!(d0 > (double) this.attackRadiusSqr)) {
				this.mob.getNavigation().stop();
				this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
			} else {
				super.tick();
			}
		}
	}
	
	@Override
	protected void checkAndPerformAttack(LivingEntity target, double p_25558_) {
		
	}
}