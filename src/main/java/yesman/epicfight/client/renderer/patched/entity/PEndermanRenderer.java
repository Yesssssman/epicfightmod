package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.EndermanMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedEyeLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.EndermanPatch;

@OnlyIn(Dist.CLIENT)
public class PEndermanRenderer extends PatchedLivingEntityRenderer<EnderMan, EndermanPatch, EndermanModel<EnderMan>, EndermanRenderer, EndermanMesh> {
	private static final ResourceLocation ENDERMAN_EYE_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	
	public PEndermanRenderer(EntityRendererProvider.Context context) {
		super(context);
		
		this.addPatchedLayer(EnderEyesLayer.class, new PatchedEyeLayer<>(ENDERMAN_EYE_TEXTURE, Meshes.ENDERMAN));
	}
	
	@Override
	public EndermanMesh getMesh(EndermanPatch entitypatch) {
		return Meshes.ENDERMAN;
	}
}