package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.ZombieVillagerModel;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.renderer.patched.layer.PatchedVillagerProfessionLayer;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@OnlyIn(Dist.CLIENT)
public class PZombieVillagerRenderer extends PHumanoidRenderer<ZombieVillagerEntity, MobPatch<ZombieVillagerEntity>, ZombieVillagerModel<ZombieVillagerEntity>> {
	public PZombieVillagerRenderer() {
		this.addPatchedLayer(VillagerLevelPendantLayer.class, new PatchedVillagerProfessionLayer());
	}
}