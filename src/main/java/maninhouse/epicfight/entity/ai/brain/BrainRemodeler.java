package maninhouse.epicfight.entity.ai.brain;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.Task;

@SuppressWarnings("rawtypes")
public final class BrainRemodeler
{
	public static <T extends LivingEntity> void removeTask(Brain<T> targetBrain, Activity activity, int priority, Class<? extends Task> target)
	{
		Map<Integer, Map<Activity, Set<Task<? super T>>>> brainPriorityMap = targetBrain.taskPriorityMap;
		Set<Task<? super T>> set = brainPriorityMap.get(priority).get(activity);
		Set<Task<? super T>> toRemove = Sets.newHashSet();
		
		for(Task<? super T> task : set)
			if(target.isInstance(task))
				toRemove.add(task);
		
		for(Task<? super T> task : toRemove)
			set.remove(task);
	}
	
	public static <T extends LivingEntity> void replaceTask(Brain<T> targetBrain, Activity activity, int priority, Class<? extends Task> target, Task<? super T> newTask)
	{
		Map<Integer, Map<Activity, Set<Task<? super T>>>> brainPriorityMap = targetBrain.taskPriorityMap;
		Set<Task<? super T>> set = brainPriorityMap.get(priority).get(activity);
		Set<Task<? super T>> toRemove = Sets.newHashSet();
		
		for(Task<? super T> task : set) {
			if(target.isInstance(task)) {
				toRemove.add(task);
			}
		}
		
		for(Task<? super T> task : toRemove) {
			set.remove(task);
		}
		
		set.add(newTask);
	}
}