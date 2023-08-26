package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedVillagerProfessionLayer;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@OnlyIn(Dist.CLIENT)
public class PZombieVillagerRenderer extends PHumanoidRenderer<ZombieVillager, MobPatch<ZombieVillager>, ZombieVillagerModel<ZombieVillager>, HumanoidMesh> {
	public PZombieVillagerRenderer() {
		super(Meshes.VILLAGER_ZOMBIE);
		this.addPatchedLayer(VillagerProfessionLayer.class, new PatchedVillagerProfessionLayer());
	}
}