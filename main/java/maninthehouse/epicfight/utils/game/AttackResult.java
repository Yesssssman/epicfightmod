package maninthehouse.epicfight.utils.game;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;

public class AttackResult
{
	private Entity attacker;
	private List<Double> distanceToAttacker;
	private List<Entity> hitEntites;
	private int index;
	
	public AttackResult(Entity attacker)
	{
		this.attacker = attacker;
		this.distanceToAttacker = Lists.<Double>newArrayList();
		this.hitEntites = Lists.<Entity>newArrayList();
		this.index = 0;
	}
	
	public AttackResult(Entity attacker, List<Entity> entities)
	{
		this(attacker);
		this.addEntities(entities);
	}
	
	private void addEntities(List<Entity> entities)
	{
		for(Entity entity : entities)
		{
			addNewEntity(entity);
		}
	}
	
	private void addNewEntity(Entity newHitEntity)
	{
		double distance = attacker.getDistanceSq(newHitEntity);
		int index = 0;
		
		for(; index < hitEntites.size(); index++)
		{
			if(distance < distanceToAttacker.get(index))
			{
				hitEntites.add(index, newHitEntity);
				distanceToAttacker.add(index, distance);
				return;
			}
		}
		
		hitEntites.add(index, newHitEntity);
		distanceToAttacker.add(index, distance);
	}
	
	public Entity getEntity()
	{
		return hitEntites.get(index);
	}
	
	public boolean next()
	{
		index++;
		return hitEntites.size() > index ? true : false;
	}
}
