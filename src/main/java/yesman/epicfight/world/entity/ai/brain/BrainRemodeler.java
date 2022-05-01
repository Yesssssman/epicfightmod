package yesman.epicfight.world.entity.ai.brain;

import java.util.Map;
import java.util.Set;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.schedule.Activity;

@SuppressWarnings("rawtypes")
public final class BrainRemodeler {
	public static <E extends LivingEntity> void removeBehavior(Brain<E> targetBrain, Activity activity, int priority, Class target) {
		Map<Integer, Map<Activity, Set<Behavior<? super E>>>> brainPriorityMap = targetBrain.availableBehaviorsByPriority;
		Set<Behavior<? super E>> set = brainPriorityMap.get(priority).get(activity);
		set.removeIf((behavior) -> target.isInstance(behavior));
	}
	
	public static <E extends LivingEntity> void replaceBehavior(Brain<E> targetBrain, Activity activity, int priority, Class target, Behavior<? super E> newBehavior) {
		Map<Integer, Map<Activity, Set<Behavior<? super E>>>> brainPriorityMap = targetBrain.availableBehaviorsByPriority;
		Set<Behavior<? super E>> set = brainPriorityMap.get(priority).get(activity);
		set.removeIf((behavior) -> target.isInstance(behavior));
		set.add(newBehavior);
	}
}