package maninthehouse.epicfight.capabilities.entity.mob;

import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import net.minecraft.entity.monster.EntityZombieVillager;

public class ZombieVillagerData extends ZombieData<EntityZombieVillager> {
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_VILLAGER_ZOMBIE;
	}
}