package yesman.epicfight.api.utils;

import java.util.List;
import java.util.function.BiFunction;

import com.google.common.collect.Lists;

import net.minecraft.world.entity.Entity;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class HitEntityList {
	private List<Entity> hitEntites;
	private int index;
	
	public HitEntityList(LivingEntityPatch<?> attacker, List<Entity> entities, Priority priority) {
		this.index = -1;
		this.hitEntites = priority.sortingFunction.apply(attacker, entities);
	}
	
	public Entity getEntity() {
		return this.hitEntites.get(this.index);
	}
	
	public boolean next() {
		this.index++;
		return this.hitEntites.size() > this.index ? true : false;
	}
	
	public static enum Priority {
		DISTANCE((attacker, list) -> {
			List<Double> distanceToAttacker = Lists.<Double>newArrayList();
			List<Entity> hitEntites = Lists.<Entity>newArrayList();
			
			Outer:
			for (Entity entity : list) {
				double distance = attacker.getOriginal().distanceToSqr(entity);
				int index = 0;
				for (; index < hitEntites.size(); index++) {
					if (distance < distanceToAttacker.get(index)) {
						hitEntites.add(index, entity);
						distanceToAttacker.add(index, distance);
						continue Outer;
					}
				}
				
				hitEntites.add(index, entity);
				distanceToAttacker.add(index, distance);
			}
			return hitEntites;
		}),
		
		TARGET((attacker, list) -> {
			List<Double> distanceToAttacker = Lists.<Double>newArrayList();
			List<Entity> hitEntites = Lists.<Entity>newArrayList();
			
			Outer:
			for (Entity entity : list) {
				double distance = attacker.getOriginal().distanceToSqr(entity);
				int index = 0;
				if (entity.equals(attacker.getTarget())) {
					hitEntites.add(0, entity);
					distanceToAttacker.add(0, 0.0D);
					continue Outer;
				}
				
				for (; index < hitEntites.size(); index++) {
					if (distance < distanceToAttacker.get(index)) {
						hitEntites.add(index, entity);
						distanceToAttacker.add(index, distance);
						continue Outer;
					}
				}
				
				hitEntites.add(index, entity);
				distanceToAttacker.add(index, distance);
			}
			return hitEntites;
		});
		
		BiFunction<LivingEntityPatch<?>, List<Entity>, List<Entity>> sortingFunction;
		
		Priority(BiFunction<LivingEntityPatch<?>, List<Entity>, List<Entity>> sortingFunction) {
			this.sortingFunction = sortingFunction;
		}
	}
}