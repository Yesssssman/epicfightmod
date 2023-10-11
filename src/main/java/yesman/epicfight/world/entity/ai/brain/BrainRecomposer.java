package yesman.epicfight.world.entity.ai.brain;

import java.util.Map;
import java.util.Set;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.schedule.Activity;

@SuppressWarnings("rawtypes")
public final class BrainRecomposer {
	public static <E extends LivingEntity> void removeBehavior(Brain<E> brain, Activity activity, int priority, Class targetBehaviorClass) {
		Set<BehaviorControl<? super E>> set = brain.availableBehaviorsByPriority.get(priority).get(activity);
		set.removeIf((behavior) -> targetBehaviorClass.isInstance(behavior));
	}
	
	public static <E extends LivingEntity> void replaceBehavior(Brain<E> brain, Activity activity, int priority, Class targetBehaviorClass, BehaviorControl<? super E> newBehavior) {
		Set<BehaviorControl<? super E>> set = brain.availableBehaviorsByPriority.get(priority).get(activity);
		
		set.removeIf((behavior) -> targetBehaviorClass.isInstance(behavior));
		set.add(newBehavior);
	}
	
	public static <E extends LivingEntity> void removeBehaviors(Brain<E> brain, Activity activity, Class target) {
		for (Map<Activity, Set<BehaviorControl<? super E>>> map : brain.availableBehaviorsByPriority.values()) {
			Set<BehaviorControl<? super E>> set = map.get(activity);
			
			if (set != null) {
				set.removeIf((behavior) -> target.isInstance(behavior));
			}
		}
	}
	
	public static <E extends LivingEntity> void replaceBehaviors(Brain<E> brain, Activity activity, Class target, BehaviorControl<? super E> newBehavior) {
		for (Map<Activity, Set<BehaviorControl<? super E>>> map : brain.availableBehaviorsByPriority.values()) {
			Set<BehaviorControl<? super E>> set = map.get(activity);
			
			if (set != null) {
				set.removeIf((behavior) -> target.isInstance(behavior));
				set.add(newBehavior);
			}
		}
	}
}