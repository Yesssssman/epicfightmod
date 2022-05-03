package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.PathfinderMob;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Models;

public class ZombieVillagerPatch<T extends PathfinderMob> extends ZombiePatch<T> {
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.villagerZombie;
	}
}