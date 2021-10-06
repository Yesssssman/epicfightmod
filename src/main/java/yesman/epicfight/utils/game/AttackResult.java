package yesman.epicfight.utils.game;

import java.util.List;
import java.util.function.BiFunction;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import yesman.epicfight.capabilities.entity.LivingData;

public class AttackResult {
	private List<Entity> hitEntites;
	private int index;
	
	public AttackResult(LivingData<?> attacker, List<Entity> entities, Priority priority) {
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
				double distance = attacker.getOriginalEntity().getDistanceSq(entity);
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
				double distance = attacker.getOriginalEntity().getDistanceSq(entity);
				int index = 0;
				if (entity.equals(attacker.getAttackTarget())) {
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
		
		BiFunction<LivingData<?>, List<Entity>, List<Entity>> sortingFunction;
		
		Priority(BiFunction<LivingData<?>, List<Entity>, List<Entity>> sortingFunction) {
			this.sortingFunction = sortingFunction;
		}
	}
}