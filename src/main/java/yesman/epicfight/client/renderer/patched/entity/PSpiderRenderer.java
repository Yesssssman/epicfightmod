package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.SpiderMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedEyeLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.SpiderPatch;

@OnlyIn(Dist.CLIENT)
public class PSpiderRenderer extends PatchedLivingEntityRenderer<Spider, SpiderPatch<Spider>, SpiderModel<Spider>, SpiderMesh> {
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	public PSpiderRenderer() {
		this.addPatchedLayer(SpiderEyesLayer.class, new PatchedEyeLayer<>(SPIDER_EYE_TEXTURE, Meshes.SPIDER));
	}
	
	@Override
	public SpiderMesh getMesh(SpiderPatch<Spider> entitypatch) {
		return Meshes.SPIDER;
	}
}