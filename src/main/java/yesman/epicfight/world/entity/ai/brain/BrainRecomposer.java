package yesman.epicfight.world.entity.ai.brain;

import java.util.Map;
import java.util.Set;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.Task;

@SuppressWarnings("rawtypes")
public final class BrainRecomposer {
	public static <E extends LivingEntity> void removeBehavior(Brain<E> brain, Activity activity, int priority, Class targetBehaviorClass) {
		Set<Task<? super E>> set = brain.availableBehaviorsByPriority.get(priority).get(activity);
		set.removeIf((behavior) -> targetBehaviorClass.isInstance(behavior));
	}
	
	public static <E extends LivingEntity> void replaceBehavior(Brain<E> brain, Activity activity, int priority, Class targetBehaviorClass, Task<? super E> newBehavior) {
		Set<Task<? super E>> set = brain.availableBehaviorsByPriority.get(priority).get(activity);
		
		set.removeIf((behavior) -> targetBehaviorClass.isInstance(behavior));
		set.add(newBehavior);
	}
	
	public static <E extends LivingEntity> void removeBehaviors(Brain<E> brain, Activity activity, Class target) {
		for (Map<Activity, Set<Task<? super E>>> map : brain.availableBehaviorsByPriority.values()) {
			Set<Task<? super E>> set = map.get(activity);
			
			if (set != null) {
				set.removeIf((behavior) -> target.isInstance(behavior));
			}
		}
	}
	
	public static <E extends LivingEntity> void replaceBehaviors(Brain<E> brain, Activity activity, Class target, Task<? super E> newBehavior) {
		for (Map<Activity, Set<Task<? super E>>> map : brain.availableBehaviorsByPriority.values()) {
			Set<Task<? super E>> set = map.get(activity);
			
			if (set != null) {
				set.removeIf((behavior) -> target.isInstance(behavior));
				set.add(newBehavior);
			}
		}
	}
}