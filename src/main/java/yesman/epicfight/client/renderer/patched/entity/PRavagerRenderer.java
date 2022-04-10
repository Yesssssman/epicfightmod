package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.RavagerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.mob.RavagerPatch;

@OnlyIn(Dist.CLIENT)
public class PRavagerRenderer extends PatchedLivingEntityRenderer<Ravager, RavagerPatch, RavagerModel> {
	public static final ResourceLocation RAVABER_TEXTURE = new ResourceLocation("textures/entity/illager/ravager.png");
	
	@Override
	protected void setJointTransforms(RavagerPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(9, armature, entitypatch.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(RavagerPatch entitypatch) {
		return RAVABER_TEXTURE;
	}
}