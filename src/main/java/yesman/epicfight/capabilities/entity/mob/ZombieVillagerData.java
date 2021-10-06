package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.monster.ZombieVillagerEntity;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Model;

public class ZombieVillagerData extends ZombieData<ZombieVillagerEntity>
{
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.villagerZombie;
	}
}