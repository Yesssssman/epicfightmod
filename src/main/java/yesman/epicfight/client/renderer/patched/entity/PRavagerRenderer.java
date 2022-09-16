package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.model.RavagerModel;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.mob.RavagerPatch;

@OnlyIn(Dist.CLIENT)
public class PRavagerRenderer extends PatchedLivingEntityRenderer<RavagerEntity, RavagerPatch, RavagerModel> {
	@Override
	protected void setJointTransforms(RavagerPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(9, armature, entitypatch.getHeadMatrix(partialTicks));
	}
}