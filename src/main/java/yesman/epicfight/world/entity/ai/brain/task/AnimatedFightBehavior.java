package yesman.epicfight.world.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class AnimatedFightBehavior extends Behavior<Mob> {
	protected final MobPatch<?> mobpatch;
	protected final CombatBehaviors combatBehaviors;
	protected boolean shouldStop;
	
	public AnimatedFightBehavior(MobPatch<?> mobpatch, CombatBehaviors combatBehaviors) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
	    this.mobpatch = mobpatch;
	    this.combatBehaviors = combatBehaviors;
	}
	
	@Override
	protected boolean checkExtraStartConditions(ServerLevel levelIn, Mob entityIn) {
		return !this.heldRangeWeapon(entityIn) && this.isValidTarget(this.mobpatch.getAttackTarget());
	}
	
	@Override
	protected boolean canStillUse(ServerLevel levelIn, Mob entityIn, long gameTimeIn) {
		return this.checkExtraStartConditions(levelIn, entityIn) && BehaviorUtils.canSee(entityIn, this.getAttackTarget(entityIn)) && !this.mobpatch.getEntityState().hurt() && !this.shouldStop;
	}
	
	@Override
	protected void start(ServerLevel worldIn, Mob entityIn, long gameTimeIn) {
		this.shouldStop = false;
	}
	
	@Override
	protected void stop(ServerLevel worldIn, Mob entityIn, long gameTimeIn) {
		this.combatBehaviors.cancel();
	}
	
	@Override
	protected void tick(ServerLevel worldIn, Mob entityIn, long gameTimeIn) {
		EntityState state = this.mobpatch.getEntityState();
		this.combatBehaviors.tick();
		
		if (this.combatBehaviors.hasActivatedMove()) {
			if (state.basicAttackPossible()) {
				CombatBehaviors.Behavior result = this.combatBehaviors.tryProceed();
				
				if (result != null) {
					result.execute(this.mobpatch, this.combatBehaviors);
				} else {
					this.shouldStop = true;
				}
			}
		} else {
			if (!state.inaction()) {
				CombatBehaviors.Behavior result = this.combatBehaviors.selectRandomBehaviorSeries();
				
				if (result != null) {
					result.execute(this.mobpatch, this.combatBehaviors);
				}
			}
		}
	}
	
	private LivingEntity getAttackTarget(Mob mob) {
		return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
	
	private boolean heldRangeWeapon(Mob mob) {
		return mob.isHolding((stack) -> {
			Item item = stack.getItem();
			return item instanceof ProjectileWeaponItem && mob.canFireProjectileWeapon((ProjectileWeaponItem) item);
		});
	}
	
	protected boolean isValidTarget(LivingEntity attackTarget) {
    	return attackTarget != null && attackTarget.isAlive() && !((attackTarget instanceof Player) && (((Player)attackTarget).isSpectator() || ((Player)attackTarget).isCreative()));
    }
}