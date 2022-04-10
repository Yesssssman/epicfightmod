package yesman.epicfight.world.entity.ai.brain.task;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class AttackPatternBehavior extends Behavior<Mob> {
	private final List<AttackAnimation> patternList;
	private final MobPatch<?> mobpatch;
	private final double minRangeSquare;
	private final double maxRangeSquare;
	private int patternCounter;

	public AttackPatternBehavior(MobPatch<?> mobpatch, List<AttackAnimation> patternList, double minRange, double maxRange) {
	      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
	      this.patternList = patternList;
	      this.mobpatch = mobpatch;
	      this.minRangeSquare = minRange * minRange;
	      this.maxRangeSquare = maxRange * maxRange;
	      this.patternCounter = 0;
	}
	
	@Override
	protected boolean checkExtraStartConditions(ServerLevel worldIn, Mob owner) {
		LivingEntity target = this.getAttackTarget(owner);
		return !this.heldRangeWeapon(owner) && BehaviorUtils.canSee(owner, target) && this.isTargetInRanged(owner, target) && !this.mobpatch.getEntityState().inaction();
	}
	
	private boolean isTargetInRanged(Mob owner, LivingEntity target) {
		double distance = owner.distanceToSqr(target.getX(), target.getY(), target.getZ());
	    return this.minRangeSquare <= distance && distance <= this.maxRangeSquare;
	}
	
	@Override
	protected void start(ServerLevel worldIn, Mob entityIn, long gameTimeIn) {
		LivingEntity livingentity = this.getAttackTarget(entityIn);
		BehaviorUtils.lookAtEntity(entityIn, livingentity);
		this.mobpatch.playAnimationSynchronized(this.patternList.get(patternCounter++), 0.0F);
		this.patternCounter %= this.patternList.size();
	}
	
	private boolean heldRangeWeapon(Mob mob) {
		return mob.isHolding((stack) -> {
			Item item = stack.getItem();
			return item instanceof ProjectileWeaponItem && mob.canFireProjectileWeapon((ProjectileWeaponItem) item);
		});
	}
	
	private LivingEntity getAttackTarget(Mob mob) {
		return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
}