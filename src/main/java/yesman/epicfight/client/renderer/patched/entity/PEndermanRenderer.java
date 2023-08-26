package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.EndermanMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedEyeLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.EndermanPatch;

@OnlyIn(Dist.CLIENT)
public class PEndermanRenderer extends PatchedLivingEntityRenderer<EnderMan, EndermanPatch, EndermanModel<EnderMan>, EndermanMesh> {
	private static final ResourceLocation ENDERMAN_EYE_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	
	public PEndermanRenderer() {
		this.addPatchedLayer(EnderEyesLayer.class, new PatchedEyeLayer<>(ENDERMAN_EYE_TEXTURE, Meshes.ENDERMAN));
	}
	
	@Override
	protected void setJointTransforms(EndermanPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform("Head", armature, entitypatch.getHeadMatrix(partialTicks));
		
		if (entitypatch.isRaging()) {
			OpenMatrix4f head = new OpenMatrix4f().translate(0.0F, 0.25F, 0.0F);
			this.setJointTransform("Head_Top", armature, head);
		}
	}

	@Override
	public EndermanMesh getMesh(EndermanPatch entitypatch) {
		return Meshes.ENDERMAN;
	}
}