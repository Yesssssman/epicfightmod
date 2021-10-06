package yesman.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IFlinging;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.Joint;
import yesman.epicfight.capabilities.entity.MobData;
import yesman.epicfight.model.Armature;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class HoglinRenderer<E extends MobEntity & IFlinging, T extends MobData<E>> extends ArmatureRenderer<E, T, BoarModel<E>> {
	private static final OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate((float)Math.toRadians(-30.0), new Vec3f(1, 0, 0));
	private static final OpenMatrix4f REVERSE = new OpenMatrix4f().rotate((float)Math.toRadians(30.0), new Vec3f(1, 0, 0));
	private final ResourceLocation textureLocation;
	
	public HoglinRenderer(String texturePath) {
		this.textureLocation = new ResourceLocation(texturePath);
	}
	
	@Override
	protected void transformJoint(int jointId, Armature modelArmature, OpenMatrix4f mat) {
		Joint joint = modelArmature.findJointById(jointId);
		OpenMatrix4f.mul(joint.getAnimatedTransform(), CORRECTION, joint.getAnimatedTransform());
        OpenMatrix4f.mul(joint.getAnimatedTransform(), mat, joint.getAnimatedTransform());
        OpenMatrix4f.mul(joint.getAnimatedTransform(), REVERSE, joint.getAnimatedTransform());
	}
	
	@Override
	protected void applyTransforms(MatrixStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks) {
        super.applyTransforms(matStack, armature, entityIn, entitydata, partialTicks);
        if (entityIn.isChild()) {
			this.transformJoint(1, armature, new OpenMatrix4f().scale(new Vec3f(1.25F, 1.25F, 1.25F)));
		}
        this.transformJoint(1, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(E entityIn) {
		return textureLocation;
	}
}