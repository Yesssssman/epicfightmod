package maninhouse.epicfight.client.renderer.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.animation.Joint;
import maninhouse.epicfight.animation.types.AttackAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.client.animation.Layer.Priority;
import maninhouse.epicfight.client.model.ClientModel;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.client.renderer.ModRenderTypes;
import maninhouse.epicfight.client.renderer.layer.Layer;
import maninhouse.epicfight.model.Armature;
import maninhouse.epicfight.utils.math.MathUtils;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
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

@OnlyIn(Dist.CLIENT)
public abstract class ArmatureRenderer<E extends LivingEntity, T extends LivingData<E>> {
	protected List<Layer<E, T>> layers;
	protected static Method canRenderName;
	protected static Method renderName;
	
	static {
		canRenderName = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "func_177070_b", Entity.class);
		renderName = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "func_225629_a_", Entity.class,
					ITextComponent.class, MatrixStack.class, IRenderTypeBuffer.class, int.class);
	}
	
	public ArmatureRenderer() {
		this.layers = Lists.newArrayList();
	}
	
	public void render(E entityIn, T entitydata, EntityRenderer<E> renderer, IRenderTypeBuffer buffer, MatrixStack matStack, int packedLightIn, float partialTicks) {
		try {
			RenderNameplateEvent renderNameplateEvent = new RenderNameplateEvent(entityIn, entityIn.getDisplayName(), renderer, matStack, buffer, packedLightIn, partialTicks);
			MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
			
			if (((boolean)canRenderName.invoke(renderer, entityIn) || renderNameplateEvent.getResult() == Result.ALLOW)
					&& renderNameplateEvent.getResult() != Result.DENY) {
				renderName.invoke(renderer, entityIn, renderNameplateEvent.getContent(), matStack, buffer, packedLightIn);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		Minecraft mc = Minecraft.getInstance();
		boolean flag = this.isVisible(entityIn);
		boolean flag1 = !flag && !entityIn.isInvisibleToPlayer(mc.player);
		boolean flag2 = mc.isEntityGlowing(entityIn);
		RenderType renderType = this.getRenderType(entityIn, entitydata, flag, flag1, flag2);
		
		ClientModel model = entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		matStack.push();
		this.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		entitydata.getClientAnimator().setPoseToModel(partialTicks);
		OpenMatrix4f[] poses = armature.getJointTransforms();
		
		if (renderType != null) {
			IVertexBuilder builder = buffer.getBuffer(renderType);
			model.draw(matStack, builder, packedLightIn, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F, poses);
		}
		
		if (!entityIn.isSpectator()) {
			this.renderLayer(entitydata, entityIn, poses, buffer, matStack, packedLightIn, partialTicks);
		}
		
		if (renderType != null) {
			if (Minecraft.getInstance().getRenderManager().isDebugBoundingBox()) {
				AnimatorClient animator = entitydata.getClientAnimator();
				AnimationPlayer player = animator.getLayer(Priority.HIGHEST).animationPlayer;
				if(player.getPlay() instanceof AttackAnimation) {
					AttackAnimation attackAnimation = (AttackAnimation)player.getPlay();
					float prevElapsedTime = player.getPrevElapsedTime();
					float elapsedTime = player.getElapsedTime();
					attackAnimation.getCollider(entitydata, elapsedTime).draw(matStack, buffer, entitydata, attackAnimation, prevElapsedTime, elapsedTime, partialTicks, player.getPlay().getPlaySpeed(entitydata));
				}
			}
		}
		
		matStack.pop();
	}
	
	public RenderType getRenderType(E entityIn, T entitydata, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
		ResourceLocation resourcelocation = this.getEntityTexture(entityIn);
		if (isVisibleToPlayer) {
			return ModRenderTypes.getItemEntityTranslucentCull(resourcelocation);
		} else if (isVisible) {
			return this.getCommonRenderType(resourcelocation);
		} else {
			return isGlowing ? RenderType.getOutline(resourcelocation) : null;
		}
	}
	
	protected abstract ResourceLocation getEntityTexture(E entityIn);
	
	protected void renderLayer(T entitydata, E entityIn, OpenMatrix4f[] poses, IRenderTypeBuffer buffer, MatrixStack matrixStackIn, int packedLightIn, float partialTicks) {
		for (Layer<E, T> layer : this.layers) {
			layer.renderLayer(entitydata, entityIn, matrixStackIn, buffer, packedLightIn, poses, partialTicks);
		}
	}
	
	protected boolean isVisible(E entityIn) {
		return !entityIn.isInvisible();
	}

	protected RenderType getCommonRenderType(ResourceLocation resourcelocation) {
		return ModRenderTypes.getAnimatedModel(resourcelocation);
	}
	
	protected void transformJoint(int jointId, Armature modelArmature, OpenMatrix4f mat) {
		Joint joint = modelArmature.findJointById(jointId);
        OpenMatrix4f.mul(joint.getAnimatedTransform(), mat, joint.getAnimatedTransform());
	}
	
	protected void applyRotations(MatrixStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks) {
		OpenMatrix4f origin = entitydata.getModelMatrix(partialTicks);
        OpenMatrix4f transpose = new OpenMatrix4f(origin).transpose();
        matStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
        MathUtils.translateStack(matStack, origin);
        MathUtils.rotateStack(matStack, transpose);
        MathUtils.scaleStack(matStack, transpose);
	}
}