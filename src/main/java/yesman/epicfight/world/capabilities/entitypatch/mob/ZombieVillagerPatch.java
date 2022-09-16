package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.entity.CreatureEntity;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Models;

public class ZombieVillagerPatch<T extends CreatureEntity> extends ZombiePatch<T> {
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.villagerZombie;
	}
}