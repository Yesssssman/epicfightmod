package yesman.epicfight.world.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ShootableItem;
import net.minecraft.world.server.ServerWorld;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class AnimatedCombatBehavior<T extends MobPatch<?>> extends Task<MobEntity> {
	protected final T mobpatch;
	protected final CombatBehaviors<T> combatBehaviors;
	
	public AnimatedCombatBehavior(T mobpatch, CombatBehaviors<T> combatBehaviors) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT));
	    this.mobpatch = mobpatch;
	    this.combatBehaviors = combatBehaviors;
	}
	
	@Override
	protected boolean checkExtraStartConditions(ServerWorld levelIn, MobEntity entityIn) {
		return !this.isHoldingRangeWeapon(entityIn) && this.isValidTarget(this.mobpatch.getTarget());
	}
	
	@Override
	protected boolean canStillUse(ServerWorld levelIn, MobEntity entityIn, long gameTimeIn) {
		return this.checkExtraStartConditions(levelIn, entityIn) && BrainUtil.canSee(entityIn, this.mobpatch.getTarget()) && !this.mobpatch.getEntityState().hurt();
	}
	
	@Override
	protected void tick(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
		EntityState state = this.mobpatch.getEntityState();
		this.combatBehaviors.tick();
		
		if (this.combatBehaviors.hasActivatedMove()) {
			if (state.canBasicAttack()) {
				CombatBehaviors.Behavior<T> result = this.combatBehaviors.tryProceed();
				
				if (result != null) {
					result.execute(this.mobpatch);
				}
			}
		} else {
			if (!state.inaction()) {
				CombatBehaviors.Behavior<T> result = this.combatBehaviors.selectRandomBehaviorSeries();
				
				if (result != null) {
					result.execute(this.mobpatch);
				}
			}
		}
	}
	
	private boolean isHoldingRangeWeapon(MobEntity mob) {
		return mob.isHolding((stack) -> {
			Item item = stack.getItem();
			return item instanceof ShootableItem && mob.canFireProjectileWeapon((ShootableItem) item);
		});
	}
	
	protected boolean isValidTarget(LivingEntity attackTarget) {
    	return attackTarget != null && attackTarget.isAlive() && !((attackTarget instanceof PlayerEntity) && (((PlayerEntity)attackTarget).isSpectator() || ((PlayerEntity)attackTarget).isCreative()));
    }
}