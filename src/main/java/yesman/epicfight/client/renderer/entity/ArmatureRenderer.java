package yesman.epicfight.client.renderer.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
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
import yesman.epicfight.animation.AnimationPlayer;
import yesman.epicfight.animation.Joint;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.client.animation.Layer.Priority;
import yesman.epicfight.client.model.ClientModel;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.client.renderer.ModRenderTypes;
import yesman.epicfight.client.renderer.layer.AnimatedLayer;
import yesman.epicfight.model.Armature;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public abstract class ArmatureRenderer<E extends LivingEntity, T extends LivingData<E>, M extends EntityModel<E>> {
	protected Map<Class<?>, AnimatedLayer<E, T, M, ? extends LayerRenderer<E, M>>> layerRendererReplace;
	protected static Method canRenderName;
	protected static Method renderName;
	
	static {
		canRenderName = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "func_177070_b", Entity.class);
		renderName = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "func_225629_a_", Entity.class,
					ITextComponent.class, MatrixStack.class, IRenderTypeBuffer.class, int.class);
	}
	
	public ArmatureRenderer() {
		this.layerRendererReplace = Maps.newHashMap();
	}
	
	public void render(E entityIn, T entitydata, LivingRenderer<E, M> renderer, IRenderTypeBuffer buffer, MatrixStack matStack, int packedLightIn, float partialTicks) {
		try {
			RenderNameplateEvent renderNameplateEvent = new RenderNameplateEvent(entityIn, entityIn.getDisplayName(), renderer, matStack, buffer, packedLightIn, partialTicks);
			MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
			if (((boolean)canRenderName.invoke(renderer, entityIn) || renderNameplateEvent.getResult() == Result.ALLOW) && renderNameplateEvent.getResult() != Result.DENY) {
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
        this.applyTransforms(matStack, armature, entityIn, entitydata, partialTicks);
		entitydata.getClientAnimator().setPoseToModel(partialTicks);
		OpenMatrix4f[] poses = armature.getJointTransforms();
		
		if (renderType != null) {
			IVertexBuilder builder = buffer.getBuffer(renderType);
			model.draw(matStack, builder, packedLightIn, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F, poses);
		}
		
		if (!entityIn.isSpectator()) {
			this.renderLayer(renderer, entitydata, entityIn, poses, buffer, matStack, packedLightIn, partialTicks);
		}
		
		if (renderType != null) {
			if (Minecraft.getInstance().getRenderManager().isDebugBoundingBox()) {
				AnimatorClient animator = entitydata.getClientAnimator();
				AnimationPlayer player = animator.getLayer(Priority.HIGHEST).animationPlayer;
				if (player.getPlay() instanceof AttackAnimation) {
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
	
	protected void renderLayer(LivingRenderer<E, M> renderer, T entitydata, E entityIn, OpenMatrix4f[] poses, IRenderTypeBuffer buffer, MatrixStack matrixStackIn, int packedLightIn, float partialTicks) {
		List<LayerRenderer<E, M>> layers = Lists.newArrayList();
		renderer.layerRenderers.forEach(layers::add);
		Iterator<LayerRenderer<E, M>> iter = layers.iterator();
		float f = MathUtils.interpolateRotation(entityIn.prevRenderYawOffset, entityIn.renderYawOffset, partialTicks);
        float f1 = MathUtils.interpolateRotation(entityIn.prevRotationYawHead, entityIn.rotationYawHead, partialTicks);
        float f2 = f1 - f;
		float f7 = entityIn.getPitch(partialTicks);
		
		while (iter.hasNext()) {
			LayerRenderer<E, M> layer = iter.next();
			this.layerRendererReplace.computeIfPresent(layer.getClass(), (key, val) -> {
				val.renderLayer(0, entitydata, entityIn, layer, matrixStackIn, buffer, packedLightIn, poses, f2, f7, partialTicks);
				iter.remove();
				return val;
			});
		}
		
		OpenMatrix4f modelMatrix = new OpenMatrix4f();
		OpenMatrix4f.mul(entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(this.getRootJointIndex()).getAnimatedTransform(), modelMatrix, modelMatrix);
		OpenMatrix4f transpose = OpenMatrix4f.transpose(modelMatrix, null);
		
		matrixStackIn.push();
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		MathUtils.rotateStack(matrixStackIn, transpose);
		matrixStackIn.translate(0.0D, this.getLayerCorrection(), 0.0D);
		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		layers.forEach((layer) -> {
			layer.render(matrixStackIn, buffer, packedLightIn, entityIn, entityIn.limbSwing, entityIn.limbSwingAmount, partialTicks, entityIn.ticksExisted, f2, f7);
		});
		matrixStackIn.pop();
	}
	
	protected boolean isVisible(E entityIn) {
		return !entityIn.isInvisible();
	}

	protected RenderType getCommonRenderType(ResourceLocation resourcelocation) {
		return ModRenderTypes.getAnimatedModel(resourcelocation);
	}
	
	protected void transformJoint(int jointId, Armature modelArmature, OpenMatrix4f mat) {
		Joint joint = modelArmature.findJointById(jointId);
        OpenMatrix4f.mul(mat, joint.getAnimatedTransform(), joint.getAnimatedTransform());
	}
	
	protected void applyTransforms(MatrixStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks) {
		OpenMatrix4f modelMatrix = entitydata.getModelMatrix(partialTicks);
        OpenMatrix4f transpose = new OpenMatrix4f(modelMatrix).transpose();
        matStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
        MathUtils.translateStack(matStack, modelMatrix);
        MathUtils.rotateStack(matStack, transpose);
        MathUtils.scaleStack(matStack, transpose);
	}
	
	protected int getRootJointIndex() {
		return 0;
	}
	
	protected double getLayerCorrection() {
		return 1.15D;
	}
}