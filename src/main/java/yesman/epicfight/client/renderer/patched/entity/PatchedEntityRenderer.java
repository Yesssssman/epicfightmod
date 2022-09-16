package yesman.epicfight.client.renderer.patched.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public abstract class PatchedEntityRenderer<E extends LivingEntity, T extends LivingEntityPatch<E>, R extends EntityRenderer<E>> {
	protected static Method shouldShowName;
	protected static Method renderNameTag;
	private ResourceLocation overridingTexture;
	
	static {
		shouldShowName = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "func_177070_b", Entity.class);
		renderNameTag = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "func_225629_a_", Entity.class, ITextComponent.class, MatrixStack.class, IRenderTypeBuffer.class, int.class);
	}
	
	public PatchedEntityRenderer<E, T, R> setOverridingTexture(String texture) {
		this.overridingTexture = new ResourceLocation(texture);
		return this;
	}
	
	public void render(E entityIn, T entitypatch, R renderer, IRenderTypeBuffer buffer, MatrixStack poseStack, int packedLight, float partialTicks) {
		try {
			RenderNameplateEvent renderNameplateEvent = new RenderNameplateEvent(entityIn, entityIn.getDisplayName(), renderer, poseStack, buffer, packedLight, partialTicks);
			MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
			
			if (((boolean)shouldShowName.invoke(renderer, entityIn) || renderNameplateEvent.getResult() == Result.ALLOW) && renderNameplateEvent.getResult() != Result.DENY) {
				renderNameTag.invoke(renderer, entityIn, renderNameplateEvent.getContent(), poseStack, buffer, packedLight);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public OpenMatrix4f[] getPoseMatrices(T entitypatch, Armature armature, float partialTicks) {
		armature.initializeTransform();
        this.setJointTransforms(entitypatch, armature, partialTicks);
		entitypatch.getClientAnimator().setPoseToModel(partialTicks);
		OpenMatrix4f[] poseMatrices = armature.getJointTransforms();
		
		return poseMatrices;
	}
	
	protected void setJointTransform(int jointId, Armature modelArmature, OpenMatrix4f mat) {
		modelArmature.searchJointById(jointId).getAnimatedTransform().mulFront(mat);
	}
	
	public void mulPoseStack(MatrixStack poseStack, Armature armature, E entityIn, T entitypatch, float partialTicks) {
		OpenMatrix4f modelMatrix = entitypatch.getModelMatrix(partialTicks);
        OpenMatrix4f transpose = modelMatrix.transpose(null);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        MathUtils.translateStack(poseStack, modelMatrix);
        MathUtils.rotateStack(poseStack, transpose);
        MathUtils.scaleStack(poseStack, transpose);
	}
	
	protected void setJointTransforms(T entitypatch, Armature armature, float partialTicks) {}
	
	protected ResourceLocation getEntityTexture(T entitypatch, R renderer) {
		if (this.overridingTexture != null) {
			return this.overridingTexture;
		}
		
		return renderer.getTextureLocation(entitypatch.getOriginal());
	}
}