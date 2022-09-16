package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IFlinging;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@OnlyIn(Dist.CLIENT)
public class PHoglinRenderer<E extends MobEntity & IFlinging, T extends MobPatch<E>> extends PatchedLivingEntityRenderer<E, T, BoarModel<E>> {
	private static final OpenMatrix4f CORRECTION = OpenMatrix4f.createRotatorDeg(-30.0F, Vec3f.X_AXIS);
	private static final OpenMatrix4f REVERSE = OpenMatrix4f.createRotatorDeg(30.0F, Vec3f.X_AXIS);
	
	@Override
	protected void setJointTransform(int jointId, Armature modelArmature, OpenMatrix4f mat) {
		modelArmature.searchJointById(jointId).getAnimatedTransform().mulBack(CORRECTION).mulBack(mat).mulBack(REVERSE);
	}
	
	@Override
	protected void setJointTransforms(T entitypatch, Armature armature, float partialTicks) {
		if (entitypatch.getOriginal().isBaby()) {
			this.setJointTransform(1, armature, new OpenMatrix4f().scale(new Vec3f(1.25F, 1.25F, 1.25F)));
		}
		
        this.setJointTransform(1, armature, entitypatch.getHeadMatrix(partialTicks));
	}
}