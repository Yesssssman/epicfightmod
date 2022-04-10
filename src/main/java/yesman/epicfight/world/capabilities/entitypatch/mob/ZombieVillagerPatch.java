package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.monster.ZombieVillager;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Models;

public class ZombieVillagerPatch extends ZombiePatch<ZombieVillager> {
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.villagerZombie;
	}
}