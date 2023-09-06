package yesman.epicfight.client.renderer.patched.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

@OnlyIn(Dist.CLIENT)
public abstract class PatchedEntityRenderer<E extends Entity, T extends EntityPatch<E>, R extends EntityRenderer<E>, AM extends AnimatedMesh> {
	protected static Method shouldShowName;
	protected static Method renderNameTag;
	
	static {
		shouldShowName = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "m_6512_", Entity.class);
		renderNameTag = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "m_7649_", Entity.class, Component.class, PoseStack.class, MultiBufferSource.class, int.class);
	}
	
	public void render(E entityIn, T entitypatch, R renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
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
	
	protected void setJointTransform(String jointName, Armature modelArmature, OpenMatrix4f mat) {
		Joint joint = modelArmature.searchJointByName(jointName);
		
		if (joint != null) {
			joint.getPoseTransform().mulFront(mat);
		}
	}
	
	public void mulPoseStack(PoseStack poseStack, Armature armature, E entityIn, T entitypatch, float partialTicks) {
		OpenMatrix4f modelMatrix = entitypatch.getModelMatrix(partialTicks);
        OpenMatrix4f transpose = modelMatrix.transpose(null);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        MathUtils.translateStack(poseStack, modelMatrix);
        MathUtils.rotateStack(poseStack, transpose);
        MathUtils.scaleStack(poseStack, transpose);
        
        if (entitypatch.getOriginal() instanceof LivingEntity livingEntity && LivingEntityRenderer.isEntityUpsideDown(livingEntity)) {
        	poseStack.translate(0.0D, (double)(livingEntity.getBbHeight() + 0.1F), 0.0D);
        	poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		}
	}
	
	public OpenMatrix4f[] getPoseMatrices(T entitypatch, Armature armature, float partialTicks) {
		armature.initializeTransform();
        this.setJointTransforms(entitypatch, armature, partialTicks);
		OpenMatrix4f[] poseMatrices = armature.getAllPoseTransform(partialTicks);
		
		return poseMatrices;
	}
	
	public abstract AM getMesh(T entitypatch);
	
	protected void setJointTransforms(T entitypatch, Armature armature, float partialTicks) {}
}