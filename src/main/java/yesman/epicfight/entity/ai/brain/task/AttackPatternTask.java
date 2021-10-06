package yesman.epicfight.entity.ai.brain.task;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.ShootableItem;
import net.minecraft.world.server.ServerWorld;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.MobData;

public class AttackPatternTask extends Task<MobEntity> {
	private final List<AttackAnimation> patternList;
	private final MobData<?> entitydata;
	private final double minRangeSquare;
	private final double maxRangeSquare;
	private int patternCounter;

	public AttackPatternTask(MobData<?> entitydata, List<AttackAnimation> patternList, double minRange, double maxRange) {
	      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT));
	      this.patternList = patternList;
	      this.entitydata = entitydata;
	      this.minRangeSquare = minRange * minRange;
	      this.maxRangeSquare = maxRange * maxRange;
	      this.patternCounter = 0;
	}

	@Override
	protected boolean shouldExecute(ServerWorld worldIn, MobEntity owner) {
		LivingEntity target = this.getAttackTarget(owner);
		return !this.heldRangeWeapon(owner) && BrainUtil.isMobVisible(owner, target) && this.isTargetInRanged(owner, target) && !this.entitydata.getEntityState().isInaction();
	}
	
	private boolean isTargetInRanged(MobEntity owner, LivingEntity target) {
		double distance = owner.getDistanceSq(target.getPosX(), target.getPosY(), target.getPosZ());
	    return this.minRangeSquare <= distance && distance <= this.maxRangeSquare;
	}
	
	@Override
	protected void startExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
		LivingEntity livingentity = this.getAttackTarget(entityIn);
		BrainUtil.lookAt(entityIn, livingentity);
		this.entitydata.playAnimationSynchronize(this.patternList.get(patternCounter++), 0.0F);
		this.patternCounter %= this.patternList.size();
	}
	
	private boolean heldRangeWeapon(MobEntity mob) {
		return mob.func_233634_a_((item) -> {
			return item instanceof ShootableItem && mob.func_230280_a_((ShootableItem) item);
		});
	}
	
	private LivingEntity getAttackTarget(MobEntity mob) {
		return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
}