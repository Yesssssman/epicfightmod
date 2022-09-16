package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.PatchedEyeLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.EndermanPatch;

@OnlyIn(Dist.CLIENT)
public class PEndermanRenderer extends PatchedLivingEntityRenderer<EndermanEntity, EndermanPatch, EndermanModel<EndermanEntity>> {
	private static final ResourceLocation ENDERMAN_EYE_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	
	public PEndermanRenderer() {
		this.addPatchedLayer(AbstractEyesLayer.class, new PatchedEyeLayer<>(ENDERMAN_EYE_TEXTURE, ClientModels.LOGICAL_CLIENT.endermanEye));
	}
	
	@Override
	protected void setJointTransforms(EndermanPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(15, armature, entitypatch.getHeadMatrix(partialTicks));
		
		if (entitypatch.isRaging()) {
			OpenMatrix4f head = new OpenMatrix4f().translate(0.0F, 0.25F, 0.0F);
			this.setJointTransform(16, armature, head);
		}
	}
}