package maninthehouse.epicfight.client.renderer.entity;

import java.util.List;

import com.google.common.collect.Lists;

import maninthehouse.epicfight.animation.AnimationPlayer;
import maninthehouse.epicfight.animation.Joint;
import maninthehouse.epicfight.animation.types.attack.AttackAnimation;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.client.model.ClientModel;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.client.renderer.layer.Layer;
import maninthehouse.epicfight.model.Armature;
import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ArmatureRenderer<E extends EntityLivingBase, T extends LivingData<E>> {
	protected List<Layer<E, T>> layers;
	
	public ArmatureRenderer() {
		this.layers = Lists.newArrayList();
	}
	
	public void render(E entityIn, T entitydata, RenderLivingBase<E> renderer, double x, double y, double z, float partialTicks) {
		renderer.renderName(entityIn, x, y, z);
		ClientModel model = entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		GlStateManager.pushMatrix();
		this.applyRotations(armature, entityIn, entitydata, x, y, z, partialTicks);
		entitydata.getClientAnimator().setPoseToModel(partialTicks);
		VisibleMatrix4f[] poses = armature.getJointTransforms();
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getEntityTexture(entityIn));
		GlStateManager.disableCull();
		model.draw(poses);
		renderLayer(entitydata, entityIn, poses, partialTicks);
		if(Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox()) {
			AnimatorClient animator = entitydata.getClientAnimator();
			AnimationPlayer player = animator.getPlayer();
			if(player.getPlay() instanceof AttackAnimation) {
				AttackAnimation attackAnimation = (AttackAnimation) player.getPlay();
				boolean flag3 = entitydata.getEntityState().shouldDetectCollision();
				float elapsedTime = player.getElapsedTime();
				int index = attackAnimation.getIndexer(elapsedTime);
				Collider collider = attackAnimation.getCollider((LivingData<?>) entitydata, elapsedTime);
				VisibleMatrix4f mat = null;
				
				if (index > 0) {
					Joint joint = armature.getJointHierarcy();
					while(index >> 5 != 0) {
						index = index >> 5;
						joint = joint.getSubJoints().get((index & 31) - 1);
					}
					mat = joint.getAnimatedTransform();
				}
				
				if (mat == null) {
					mat = new VisibleMatrix4f();
				}
				collider.draw(mat, partialTicks, flag3);
			}
		}
		
		GlStateManager.popMatrix();
	}
	
	protected abstract ResourceLocation getEntityTexture(E entityIn);
	
	protected void renderLayer(T entitydata, E entityIn, VisibleMatrix4f[] poses, float partialTicks) {
		for(Layer<E, T> layer : this.layers) {
			layer.renderLayer(entitydata, entityIn, poses, partialTicks);
		}
	}
	
	protected boolean isVisible(E entityIn) {
		return !entityIn.isInvisible();
	}

	protected void transformJoint(int jointId, Armature modelArmature, VisibleMatrix4f mat) {
		Joint joint = modelArmature.findJointById(jointId);
        VisibleMatrix4f.mul(joint.getAnimatedTransform(), mat, joint.getAnimatedTransform());
	}
	
	protected void applyRotations(Armature armature, E entityIn, T entitydata, double x, double y, double z, float partialTicks) {
		VisibleMatrix4f mat4f = entitydata.getModelMatrix(partialTicks);
		mat4f.m30 = 0.0F;
		mat4f.m31 = 0.0F;
		mat4f.m32 = 0.0F;
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F, 0, 1, 0);
        GlStateManager.multMatrix(mat4f.toFloatBuffer());
	}
}