package yesman.epicfight.client.gui.datapack.widgets;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightShaders;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class AnimatedModelPlayer extends AbstractWidget implements ResizableComponent {
	private final AnimationPlayer animationPlayer = new AnimationPlayer() {
		@Override
		public void tick(LivingEntityPatch<?> entitypatch) {
			this.prevElapsedTime = this.elapsedTime;
			this.elapsedTime += EpicFightOptions.A_TICK * 0.75F * (this.isReversed() && this.getAnimation().canBePlayedReverse() ? -1.0F : 1.0F);
			
			if (this.elapsedTime >= this.play.getTotalTime()) {
				if (this.play.isRepeat()) {
					this.prevElapsedTime = 0;
					this.elapsedTime %= this.play.getTotalTime();
				} else {
					this.elapsedTime = this.play.getTotalTime();
					this.isEnd = true;
				}
			} else if (this.elapsedTime < 0) {
				if (this.play.isRepeat()) {
					this.prevElapsedTime = this.play.getTotalTime();
					this.elapsedTime = this.play.getTotalTime() + this.elapsedTime;
				} else {
					this.elapsedTime = 0.0F;
					this.isEnd = true;
				}
			}
		}
	};
	private final LinkAnimation linkAnimation = new LinkAnimation();
	private final ModelRenderTarget modelRenderTarget = new ModelRenderTarget();
	private final List<StaticAnimation> animationsToPlay = Lists.newLinkedList();
	
	private double zoom = -3.0D;
	private float xRot = 0.0F;
	private float yRot = 180.0F;
	private int index;
	private Armature armature;
	private AnimatedMesh mesh;
	
	public AnimatedModelPlayer(int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical) {
		super(x1, y1, x2, y2, Component.literal("datapack_edit.weapon_type.combo.animation_player"));
		
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.horizontalSizingOption = horizontal;
		this.verticalSizingOption = vertical;
		
		this.resize(Minecraft.getInstance().screen.getRectangle());
		
		this.modelRenderTarget.setClearColor(0.1552F, 0.1552F, 0.1552F, 1.0F);
		this.modelRenderTarget.clear(Minecraft.ON_OSX);
	}
	
	public void setArmature(Armature armature) {
		this.armature = armature.deepCopy();
	}
	
	public void setMesh(AnimatedMesh mesh) {
		this.mesh = mesh;
	}
	
	public void addAnimationToPlay(StaticAnimation animation) {
		if (this.index == -1) {
			this.index = 0;
		}
		
		this.animationsToPlay.add(animation);
	}
	
	public void removeAnimationPlayingAnimation(StaticAnimation animation) {
		this.animationsToPlay.remove(animation);
	}
	
	public void clearAnimations() {
		this.index = -1;
		this.animationsToPlay.clear();
		this.animationPlayer.setPlayAnimation(Animations.DUMMY_ANIMATION);
	}
	
	public void restartAnimations() {
		this.index = 0;
		
		if (this.animationsToPlay.size() == 0) {
			this.index = -1;
			return;
		}
		
		this.animationPlayer.setPlayAnimation(this.animationsToPlay.get(0));
	}
	
	public void tick() {
		if (this.animationsToPlay.size() == 0) {
			return;
		}
		
		this.animationPlayer.tick(null);
		
		if (this.animationPlayer.isEnd()) {
			if (this.animationPlayer.getAnimation() == this.linkAnimation) {
				this.animationPlayer.setPlayAnimation(this.animationsToPlay.get(this.index));
				this.index = (this.index + 1) % this.animationsToPlay.size();
			} else {
				Pose currentPose = this.animationPlayer.getAnimation().getRawPose(this.animationPlayer.getElapsedTime());
				StaticAnimation toPlay = this.animationsToPlay.get(this.index);
				Pose nextAnimationPose = toPlay.getRawPose(0.0F);
				float totalTime = toPlay.getConvertTime();
				
				this.linkAnimation.getTransfroms().clear();
				this.linkAnimation.setTotalTime(totalTime);
				this.linkAnimation.setNextAnimation(toPlay);
				
				Map<String, JointTransform> data1 = currentPose.getJointTransformData();
				Map<String, JointTransform> data2 = nextAnimationPose.getJointTransformData();
				
				for (String jointName : data1.keySet()) {
					if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
						Keyframe[] keyframes = new Keyframe[2];
						keyframes[0] = new Keyframe(0.0F, data1.get(jointName));
						keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
						TransformSheet sheet = new TransformSheet(keyframes);
						this.linkAnimation.getAnimationClip().addJointTransform(jointName, sheet);
					}
				}
				
				this.animationPlayer.setPlayAnimation(this.linkAnimation);
			}
		}
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
		this.xRot = (float)Mth.clamp(this.xRot + dy * 2.5D, -180.0D, 180.0D);
		this.yRot += dx * 2.5D;
		
		return true;
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double amount) {
		this.zoom = Mth.clamp(this.zoom + amount * 0.5D, -10.0D, -0.5D);
		return true;
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getMainRenderTarget().unbindWrite();
		
		this.modelRenderTarget.clear(true);
		this.modelRenderTarget.bindWrite(true);
		
		this.armature.initializeTransform();
		this.armature.setPose(this.animationPlayer.getAnimation().getRawPose(this.animationPlayer.getPrevElapsedTime() + (this.animationPlayer.getElapsedTime() - this.animationPlayer.getPrevElapsedTime()) * partialTicks));
		
		OpenMatrix4f[] poseMatrices = this.armature.getAllPoseTransform(partialTicks);
		
		Tesselator tesselator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		guiGraphics.pose().pushPose();
		
		ShaderInstance prevShader = RenderSystem.getShader();
		Matrix4f oldProjection = RenderSystem.getProjectionMatrix();
		RenderSystem.setShader(EpicFightShaders::getPositionColorNormalShader);
		
		Matrix4f perspective = (new Matrix4f()).setPerspective(70.0F, (float)this.width / (float)this.height, 0.05F, 100.0F);
		
		RenderSystem.setProjectionMatrix(perspective, VertexSorting.DISTANCE_TO_ORIGIN);
		RenderSystem.getModelViewStack().pushPose();
		RenderSystem.getModelViewStack().setIdentity();
		RenderSystem.applyModelViewMatrix();
		
		guiGraphics.pose().translate(0.0D, 0.0D, this.zoom);
		guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(this.xRot));
		guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.yRot));
		guiGraphics.pose().translate(0.0D, -1.0D, 0.0D);
		
		RenderSystem.enableDepthTest();
		
		this.mesh.initialize();
		bufferbuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
		this.mesh.draw(guiGraphics.pose(), bufferbuilder, AnimatedMesh.DrawingFunction.ENTITY_SOLID, -1, 0.9411F, 0.9411F, 0.9411F, 1.0F, -1, this.armature, poseMatrices);
		BufferUploader.drawWithShader(bufferbuilder.end());
		
		guiGraphics.pose().popPose();
		
		RenderSystem.setProjectionMatrix(oldProjection, VertexSorting.ORTHOGRAPHIC_Z);
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();
		
		RenderSystem.setShader(() -> prevShader);
		this.modelRenderTarget.unbindWrite();
		minecraft.getMainRenderTarget().bindWrite(true);
		
		RenderSystem.disableDepthTest();
		
		this.modelRenderTarget.blitToScreen();
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementInput) {
		narrationElementInput.add(NarratedElementType.TITLE, this.createNarrationMessage());
	}
	
	@Override
	public void resize(ScreenRectangle screenRectangle) {
		if (this.getHorizontalSizingOption() != null) {
			this.getHorizontalSizingOption().resizeFunction.resize(this, screenRectangle, this.getX1(), this.getX2());
		}
		
		if (this.getVerticalSizingOption() != null) {
			this.getVerticalSizingOption().resizeFunction.resize(this, screenRectangle, this.getY1(), this.getY2());
		}
		
		double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
		
		this.modelRenderTarget.resize(this.getWidth() * (int)guiScale, this.getHeight() * (int)guiScale, false);
	}
	
	public void onDestroy() {
		this.modelRenderTarget.destroyBuffers();
	}
	
	@OnlyIn(Dist.CLIENT)
	private class ModelRenderTarget extends RenderTarget {
		public ModelRenderTarget() {
			super(true);
			
			RenderSystem.assertOnRenderThreadOrInit();
			Window window = Minecraft.getInstance().getWindow();
			
			this.resize(window.getWidth(), window.getHeight(), false);
		}
		
		private void blitToScreen() {
			Minecraft minecraft = Minecraft.getInstance();
			ShaderInstance shaderinstance = minecraft.gameRenderer.blitShader;
			shaderinstance.setSampler("DiffuseSampler", this.colorTextureId);
			shaderinstance.apply();
			
			double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
			double left = AnimatedModelPlayer.this.getX() * guiScale;
			double top = AnimatedModelPlayer.this.getY() * guiScale;
			double right = left + AnimatedModelPlayer.this.getWidth() * guiScale;
			double bottom = top + AnimatedModelPlayer.this.getHeight() * guiScale;
			
			float u = (float) this.viewWidth / (float) this.width;
			float v = (float) this.viewHeight / (float) this.height;
			
			Tesselator tesselator = RenderSystem.renderThreadTesselator();
			BufferBuilder bufferbuilder = tesselator.getBuilder();
			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			bufferbuilder.vertex(left, bottom, 0.0D).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
			bufferbuilder.vertex(right, bottom, 0.0D).uv(u, 0.0F).color(255, 255, 255, 255).endVertex();
			bufferbuilder.vertex(right, top, 0.0D).uv(u, v).color(255, 255, 255, 255).endVertex();
			bufferbuilder.vertex(left, top, 0.0D).uv(0.0F, v).color(255, 255, 255, 255).endVertex();
			BufferUploader.draw(bufferbuilder.end());
			shaderinstance.clear();
		}
	}
	
	/*******************************************************************
	 * @ResizableComponent variables                                   *
	 *******************************************************************/
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private final HorizontalSizing horizontalSizingOption;
	private final VerticalSizing verticalSizingOption;
	
	@Override
	public void setX1(int x1) {
		this.x1 = x1;
	}

	@Override
	public void setX2(int x2) {
		this.x2 = x2;
	}

	@Override
	public void setY1(int y1) {
		this.y1 = y1;
	}

	@Override
	public void setY2(int y2) {
		this.y2 = y2;
	}
	
	@Override
	public int getX1() {
		return this.x1;
	}

	@Override
	public int getX2() {
		return this.x2;
	}

	@Override
	public int getY1() {
		return this.y1;
	}

	@Override
	public int getY2() {
		return this.y2;
	}

	@Override
	public HorizontalSizing getHorizontalSizingOption() {
		return this.horizontalSizingOption;
	}

	@Override
	public VerticalSizing getVerticalSizingOption() {
		return this.verticalSizingOption;
	}

	@Override
	public void setActive(boolean active) {
	}
}
