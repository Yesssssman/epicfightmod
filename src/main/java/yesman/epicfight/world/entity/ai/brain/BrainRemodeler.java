package yesman.epicfight.world.entity.ai.brain;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.schedule.Activity;

@SuppressWarnings("rawtypes")
public final class BrainRemodeler {
	public static <T extends LivingEntity> void removeBehavior(Brain<T> targetBrain, Activity activity, int priority, Class<? extends Behavior> target) {
		Map<Integer, Map<Activity, Set<Behavior<? super T>>>> brainPriorityMap = targetBrain.availableBehaviorsByPriority;
		Set<Behavior<? super T>> set = brainPriorityMap.get(priority).get(activity);
		Set<Behavior<? super T>> toRemove = Sets.newHashSet();
		
		for(Behavior<? super T> task : set)
			if(target.isInstance(task))
				toRemove.add(task);
		
		for(Behavior<? super T> task : toRemove)
			set.remove(task);
	}
	
	public static <T extends LivingEntity> void replaceBehavior(Brain<T> targetBrain, Activity activity, int priority, Class<? extends Behavior> target, Behavior<? super T> newBehavior) {
		Map<Integer, Map<Activity, Set<Behavior<? super T>>>> brainPriorityMap = targetBrain.availableBehaviorsByPriority;
		Set<Behavior<? super T>> set = brainPriorityMap.get(priority).get(activity);
		Set<Behavior<? super T>> toRemove = Sets.newHashSet();
		
		for(Behavior<? super T> task : set) {
			if(target.isInstance(task)) {
				toRemove.add(task);
			}
		}
		
		for(Behavior<? super T> task : toRemove) {
			set.remove(task);
		}
		
		set.add(newBehavior);
	}
}