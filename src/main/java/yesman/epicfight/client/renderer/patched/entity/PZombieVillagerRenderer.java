package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.renderer.patched.layer.VillagerAnimatedProfessionLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.ZombiePatch;

@OnlyIn(Dist.CLIENT)
public class PZombieVillagerRenderer extends PHumanoidRenderer<ZombieVillager, ZombiePatch<ZombieVillager>, ZombieVillagerModel<ZombieVillager>> {
	private static final ResourceLocation ZOMBIE_VILLAGER_TEXTURE = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");
	
	public PZombieVillagerRenderer() {
		this.layerRendererReplace.put(VillagerProfessionLayer.class, new VillagerAnimatedProfessionLayer());
	}
	
	@Override
	protected ResourceLocation getEntityTexture(ZombiePatch<ZombieVillager> entitypatch) {
		return ZOMBIE_VILLAGER_TEXTURE;
	}
}