package yesman.epicfight.client.gui.datapack.widgets;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.CubicBezierCurve;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.particle.TrailParticle;
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
	private final ModelRenderTarget modelRenderTarget;
	private final List<StaticAnimation> animationsToPlay = Lists.newArrayList();
	private final List<CustomTrailParticle> trailParticles = Lists.newArrayList();
	private final CheckBox showColliderCheckbox = new CheckBox(Minecraft.getInstance().font, 0, 60, 0, 10, null, null, true, Component.translatable("datapack_edit.model_player.collider"), null);
	private final CheckBox showItemCheckbox = new CheckBox(Minecraft.getInstance().font, 0, 40, 0, 10, null, null, true, Component.translatable("datapack_edit.model_player.item"), null);
	private final CheckBox showTrailCheckbox = new CheckBox(Minecraft.getInstance().font, 0, 40, 0, 10, null, null, true, Component.translatable("datapack_edit.model_player.trail"), null);
	
	private double zoom = -3.0D;
	private float xRot = 0.0F;
	private float yRot = 180.0F;
	private float xMove = 0.0F;
	private float yMove = 0.0F;
	private int index;
	
	private Armature armature;
	private AnimatedMesh mesh;
	private Collider collider;
	private TrailInfo trailInfo;
	private Item item;
	
	public AnimatedModelPlayer(int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical) {
		super(x1, y1, x2, y2, Component.literal("datapack_edit.weapon_type.combo.animation_player"));
		
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.horizontalSizingOption = horizontal;
		this.verticalSizingOption = vertical;
		
		this.modelRenderTarget = new ModelRenderTarget();
		
		this.resize(Minecraft.getInstance().screen.getRectangle());
		
		this.modelRenderTarget.setClearColor(0.1552F, 0.1552F, 0.1552F, 1.0F);
		this.modelRenderTarget.clear(Minecraft.ON_OSX);
	}
	
	public Armature getArmature() {
		return this.armature;
	}
	
	public AnimatedMesh getMesh() {
		return this.mesh;
	}
	
	public void setArmature(Armature armature) {
		this.armature = armature.deepCopy();
	}
	
	public void setMesh(AnimatedMesh mesh) {
		this.mesh = mesh;
	}
	
	public void setCollider(Collider collider) {
		this.collider = collider;
	}
	
	public void setTrailInfo(TrailInfo trailInfo) {
		this.trailInfo = trailInfo;
	}
	
	public void setItemToRender(Item item) {
		this.item = item;
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
	
	@Override
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
				
				if (this.trailInfo != null) {
					toPlay.getProperty(ClientAnimationProperties.TRAIL_EFFECT).ifPresent(trailInfos -> {
						for (TrailInfo info : trailInfos) {
							if (info.hand != InteractionHand.MAIN_HAND) {
								continue;
							}
							
							TrailInfo combinedTrailInfo = this.trailInfo.overwrite(info);
							
							if (combinedTrailInfo.playable()) {
								CustomTrailParticle trail = new CustomTrailParticle(this.armature.searchJointByName(combinedTrailInfo.joint), toPlay, combinedTrailInfo);
								this.trailParticles.add(trail);
							}
						}
					});
				}
			}
		}
		
		float elapsedTime = this.animationPlayer.getElapsedTime();
		Pose pose = this.animationPlayer.getAnimation().getRawPose(elapsedTime);
		this.armature.setPose(pose);
		
		this.trailParticles.forEach((trail) -> trail.tick());
		this.trailParticles.removeIf((trail) -> !trail.isAlive());
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean flag = this.clicked(x, y);
				
				if (flag) {
					this.playDownSound(Minecraft.getInstance().getSoundManager());
					
					this.showColliderCheckbox.mouseClicked(x, y, button);
					this.showItemCheckbox.mouseClicked(x, y, button);
					this.showTrailCheckbox.mouseClicked(x, y, button);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
		if (button == 0) {
			this.xRot = (float)Mth.clamp(this.xRot + dy * 2.5D, -180.0D, 180.0D);
			this.yRot += dx * 2.5D;
		} else if (button == 2) {
			this.xMove += (float)dx * 0.015F * -this.zoom;
			this.yMove += -(float)dy * 0.015F * -this.zoom;
		}
		
		return true;
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double amount) {
		this.zoom = Mth.clamp(this.zoom + amount * 0.5D, -10.0D, -0.5D);
		return true;
	}
	
	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		partialTicks = minecraft.getPartialTick();
		minecraft.getMainRenderTarget().unbindWrite();
		
		ScreenRectangle screenrectangle = null;
		boolean scissorApplied = guiGraphics.scissorStack.stack.size() > 0;
		
		// If scissor test is enabled, remove it.
		if (scissorApplied) {
			screenrectangle = guiGraphics.scissorStack.stack.peekLast();
			guiGraphics.disableScissor();
		}
		
		this.modelRenderTarget.clear(true);
		this.modelRenderTarget.bindWrite(true);
		this.armature.initializeTransform();
		
		Pose pose = this.armature.getPose(partialTicks);
		
		OpenMatrix4f[] poseMatrices = this.armature.getAllPoseTransform(partialTicks);
		guiGraphics.pose().pushPose();
		
		ShaderInstance prevShader = RenderSystem.getShader();
		Matrix4f oldProjection = RenderSystem.getProjectionMatrix();
		RenderSystem.setShader(EpicFightShaders::getPositionColorNormalShader);
		
		Matrix4f perspective = (new Matrix4f()).setPerspective(70.0F, (float)this.width / (float)this.height, 0.05F, 100.0F);
		
		RenderSystem.setProjectionMatrix(perspective, VertexSorting.DISTANCE_TO_ORIGIN);
		RenderSystem.getModelViewStack().pushPose();
		RenderSystem.getModelViewStack().setIdentity();
		RenderSystem.applyModelViewMatrix();
		
		guiGraphics.pose().translate(this.xMove, this.yMove - 1.0D, this.zoom);
		guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(this.xRot));
		guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.yRot));
		
		RenderSystem.enableDepthTest();
		this.mesh.initialize();
		
		Tesselator tesselator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
		this.mesh.draw(guiGraphics.pose(), bufferbuilder, AnimatedMesh.DrawingFunction.ENTITY_SOLID, -1, 0.9411F, 0.9411F, 0.9411F, 1.0F, -1, this.armature, poseMatrices);
		BufferUploader.drawWithShader(bufferbuilder.end());
		
		if (this.item != null && this.showItemCheckbox.getValue()) {
			BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			ItemStack itemstack = new ItemStack(this.item);
			
			OpenMatrix4f correction = new OpenMatrix4f().translate(0F, 0F, -0.13F).rotateDeg(-90.0F, Vec3f.X_AXIS);
			OpenMatrix4f handTransform = correction.mulFront(this.armature.getBindedTransformFor(pose, this.armature.searchJointByName("Tool_R")));
			OpenMatrix4f transposed = handTransform.transpose(null);
			
			guiGraphics.pose().pushPose();
			
			MathUtils.translateStack(guiGraphics.pose(), handTransform);
			MathUtils.rotateStack(guiGraphics.pose(), transposed);
			MathUtils.scaleStack(guiGraphics.pose(), transposed);
			
			BakedModel model = itemRenderer.getItemModelShaper().getItemModel(this.item);
			BakedModel overridedModel = model.getOverrides().resolve(model, itemstack, null, null, 0);
			DynamicTexture light = Minecraft.getInstance().gameRenderer.lightTexture().lightTexture;
			
			// Update light color
			light.getPixels().setPixelRGBA(0, 0, 0xFFFFFFFF);
			light.upload();
			
			itemRenderer.render(itemstack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, guiGraphics.pose(), bufferSource, 0, OverlayTexture.NO_OVERLAY, overridedModel);
			bufferSource.endBatch();
			
			guiGraphics.pose().popPose();
		}
		
		if (!this.trailParticles.isEmpty() && this.showTrailCheckbox.getValue()) {
			RenderSystem.setShader(GameRenderer::getParticleShader);
			DynamicTexture light = Minecraft.getInstance().gameRenderer.lightTexture().lightTexture;
			
			// Update light color
			light.getPixels().setPixelRGBA(0, 0, 0xFFFFFFFF);
			light.upload();
			
			for (CustomTrailParticle trail : this.trailParticles) {
				ParticleRenderType particleRendertype = trail.getRenderType();
				particleRendertype.begin(bufferbuilder, Minecraft.getInstance().textureManager);
				trail.render(bufferbuilder, null, partialTicks);
				particleRendertype.end(tesselator);
			}
		}
		
		if (this.collider != null && this.showColliderCheckbox.getValue()) {
			DynamicAnimation animation = this.animationPlayer.getAnimation();
			
			if (animation instanceof AttackAnimation attackanimation) {
				float elapsedTime = this.animationPlayer.getPrevElapsedTime() + (this.animationPlayer.getElapsedTime() - this.animationPlayer.getPrevElapsedTime()) * partialTicks;
				Phase phase = attackanimation.getPhaseByTime(elapsedTime);
				RenderType renderType = this.collider.getRenderType();
				RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
				
				bufferbuilder.begin(renderType.mode(), renderType.format);
				
				for (Pair<Joint, Collider> pair : phase.getColliders()) {
					int pathIndex = this.armature.searchPathIndex(pair.getFirst().getName());
					Pose prevPose;
					Pose currentPose;
					
					if (pathIndex == -1) {
						prevPose = new Pose();
						currentPose = new Pose();
						prevPose.putJointData("Root", JointTransform.empty());
						currentPose.putJointData("Root", JointTransform.empty());
					} else {
						prevPose = animation.getRawPose(this.animationPlayer.getPrevElapsedTime());
						currentPose = animation.getRawPose(this.animationPlayer.getElapsedTime());
					}
					
					this.collider.drawInternal(guiGraphics.pose(), bufferbuilder, this.armature, pair.getFirst(), prevPose, currentPose, partialTicks, -1);
				}
				
				RenderSystem.lineWidth(3.0F);
				RenderSystem.disableCull();
				BufferUploader.drawWithShader(bufferbuilder.end());
				RenderSystem.lineWidth(1.0F);
				RenderSystem.enableCull();
			}
		}
		
		guiGraphics.pose().popPose();
		
		RenderSystem.setProjectionMatrix(oldProjection, VertexSorting.ORTHOGRAPHIC_Z);
		RenderSystem.getModelViewStack().popPose();
		RenderSystem.applyModelViewMatrix();
		
		RenderSystem.setShader(() -> prevShader);
		this.modelRenderTarget.unbindWrite();
		
		minecraft.getMainRenderTarget().bindWrite(true);
		
		//RenderSystem.disableDepthTest();
		
		if (scissorApplied) {
			guiGraphics.enableScissor(screenrectangle.left(), screenrectangle.top(), screenrectangle.right(), screenrectangle.bottom());
		}
		
		this.modelRenderTarget.blitToScreen(guiGraphics);
		
		// Visibility control widget
		int top = this.getY() + 6;
		int right = this.getX() + this.getWidth() - 2;
		
		if (this.trailInfo != null) {
			right -= this.showTrailCheckbox.getWidth();
			
			this.showTrailCheckbox.setX(right);
			this.showTrailCheckbox.setY(top);
			this.showTrailCheckbox.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
		}
		
		if (this.item != null) {
			right -= this.showItemCheckbox.getWidth();
			
			this.showItemCheckbox.setX(right);
			this.showItemCheckbox.setY(top);
			this.showItemCheckbox.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
		}
		
		if (this.collider != null) {
			right -= this.showColliderCheckbox.getWidth();
			
			this.showColliderCheckbox.setX(right);
			this.showColliderCheckbox.setY(top);
			this.showColliderCheckbox.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
		}
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
		
		this.modelRenderTarget.resize(this.getWidth() * (int)guiScale, this.getHeight() * (int)guiScale, true);
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
		
		private void blitToScreen(GuiGraphics guiGraphics) {
			Minecraft minecraft = Minecraft.getInstance();
			ShaderInstance shaderinstance = minecraft.gameRenderer.blitShader;
			shaderinstance.setSampler("DiffuseSampler", this.colorTextureId);
			shaderinstance.apply();
			
			float guiScale = (float)Minecraft.getInstance().getWindow().getGuiScale();
			float left = AnimatedModelPlayer.this.getX() * guiScale;
			float top = AnimatedModelPlayer.this.getY() * guiScale;
			float right = left + AnimatedModelPlayer.this.getWidth() * guiScale;
			float bottom = top + AnimatedModelPlayer.this.getHeight() * guiScale;
			
			float u = (float) this.viewWidth / (float) this.width;
			float v = (float) this.viewHeight / (float) this.height;
			
			Matrix4f matrix4f = guiGraphics.pose().last().pose();
			RenderSystem.enableDepthTest();
			
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, -10);
			
			Tesselator tesselator = RenderSystem.renderThreadTesselator();
			BufferBuilder bufferbuilder = tesselator.getBuilder();
			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			bufferbuilder.vertex(matrix4f, left, bottom, 0.0F).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
			bufferbuilder.vertex(matrix4f, right, bottom, 0.0F).uv(u, 0.0F).color(255, 255, 255, 255).endVertex();
			bufferbuilder.vertex(matrix4f, right, top, 0.0F).uv(u, v).color(255, 255, 255, 255).endVertex();
			bufferbuilder.vertex(matrix4f, left, top, 0.0F).uv(0.0F, v).color(255, 255, 255, 255).endVertex();
			BufferUploader.draw(bufferbuilder.end());
			shaderinstance.clear();
			
			guiGraphics.pose().popPose();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private class CustomTrailParticle extends TrailParticle {
		@SuppressWarnings("deprecation")
		protected CustomTrailParticle(Joint joint, StaticAnimation animation, TrailInfo trailInfo) {
			super(AnimatedModelPlayer.this.armature, joint, animation, trailInfo);
		}
		
		@Override
		public void tick() {
			AnimationPlayer animPlayer = AnimatedModelPlayer.this.animationPlayer;
			this.visibleTrailEdges.removeIf(v -> !v.isAlive());
			
			if (this.animationEnd) {
				if (this.lifetime-- == 0) {
					this.remove();
				}
			} else {
				if (this.animation != animPlayer.getAnimation().getRealAnimation() || animPlayer.getElapsedTime() > this.trailInfo.endTime) {
					this.animationEnd = true;
					this.lifetime = this.trailInfo.trailLifetime;
				}
			}
			
			if (this.trailInfo.fadeTime > 0.0F && this.trailInfo.endTime < animPlayer.getElapsedTime()) {
				return;
			}
			
			boolean isTrailInvisible = animPlayer.getAnimation() instanceof LinkAnimation || animPlayer.getElapsedTime() <= this.trailInfo.startTime;
			boolean isFirstTrail = this.visibleTrailEdges.isEmpty();
			boolean needCorrection = (!isTrailInvisible && isFirstTrail);
			
			if (needCorrection) {
				float startCorrection = Math.max((this.trailInfo.startTime - animPlayer.getPrevElapsedTime()) / (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()), 0.0F);
				this.startEdgeCorrection = this.trailInfo.interpolateCount * 2 * startCorrection;
			}
			
			TrailInfo trailInfo = this.trailInfo;
			Pose prevPose = AnimatedModelPlayer.this.armature.getPrevPose();
			Pose middlePose = AnimatedModelPlayer.this.armature.getPose(0.5F);
			Pose currentPose = AnimatedModelPlayer.this.armature.getCurrentPose();
			OpenMatrix4f prevJointTf = AnimatedModelPlayer.this.armature.getBindedTransformFor(prevPose, this.joint);
			OpenMatrix4f middleJointTf = AnimatedModelPlayer.this.armature.getBindedTransformFor(middlePose, this.joint);
			OpenMatrix4f currentJointTf = AnimatedModelPlayer.this.armature.getBindedTransformFor(currentPose, this.joint);
			Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start);
			Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end);
			Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, trailInfo.start);
			Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, trailInfo.end);
			Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, trailInfo.start);
			Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, trailInfo.end);
			
			List<Vec3> finalStartPositions;
			List<Vec3> finalEndPositions;
			boolean visibleTrail;
			
			if (isTrailInvisible) {
				finalStartPositions = Lists.newArrayList();
				finalEndPositions = Lists.newArrayList();
				finalStartPositions.add(prevStartPos);
				finalStartPositions.add(middleStartPos);
				finalEndPositions.add(prevEndPos);
				finalEndPositions.add(middleEndPos);
				
				this.invisibleTrailEdges.clear();
				visibleTrail = false;
			} else {
				List<Vec3> startPosList = Lists.newArrayList();
				List<Vec3> endPosList = Lists.newArrayList();
				TrailEdge edge1;
				TrailEdge edge2;
				
				if (isFirstTrail) {
					int lastIdx = this.invisibleTrailEdges.size() - 1;
					edge1 = this.invisibleTrailEdges.get(lastIdx);
					edge2 = new TrailEdge(prevStartPos, prevEndPos, -1);
				} else {
					edge1 = this.visibleTrailEdges.get(this.visibleTrailEdges.size() - (this.trailInfo.interpolateCount / 2 + 1));
					edge2 = this.visibleTrailEdges.get(this.visibleTrailEdges.size() - 1);
					edge2.lifetime++;
				}
				
				startPosList.add(edge1.start);
				endPosList.add(edge1.end);
				startPosList.add(edge2.start);
				endPosList.add(edge2.end);
				startPosList.add(middleStartPos);
				endPosList.add(middleEndPos);
				startPosList.add(currentStartPos);
				endPosList.add(currentEndPos);
				
				finalStartPositions = CubicBezierCurve.getBezierInterpolatedPoints(startPosList, 1, 3, this.trailInfo.interpolateCount);
				finalEndPositions = CubicBezierCurve.getBezierInterpolatedPoints(endPosList, 1, 3, this.trailInfo.interpolateCount);
				
				if (!isFirstTrail) {
					finalStartPositions.remove(0);
					finalEndPositions.remove(0);
				}
				
				visibleTrail = true;
			}
			
			this.makeTrailEdges(finalStartPositions, finalEndPositions, visibleTrail ? this.visibleTrailEdges : this.invisibleTrailEdges);
		}
		
		@Override
		public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
			if (this.visibleTrailEdges.isEmpty()) {
				return;
			}
			
			TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
	        AbstractTexture abstracttexture = texturemanager.getTexture(this.trailInfo.texturePath);
	        RenderSystem.bindTexture(abstracttexture.getId());
	        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		    RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		    RenderSystem.setShaderTexture(0, abstracttexture.getId());
			
			PoseStack poseStack = new PoseStack();
			this.setupPoseStack(poseStack, camera, partialTick);
			Matrix4f matrix4f = poseStack.last().pose();
			int edges = this.visibleTrailEdges.size() - 1;
			boolean startFade = this.visibleTrailEdges.get(0).lifetime == 1;
			boolean endFade = this.visibleTrailEdges.get(edges).lifetime == this.trailInfo.trailLifetime;
			float startEdge = (startFade ? this.trailInfo.interpolateCount * 2 * partialTick : 0.0F) + this.startEdgeCorrection;
			float endEdge = endFade ? Math.min(edges - (this.trailInfo.interpolateCount * 2) * (1.0F - partialTick), edges - 1) : edges - 1;
			float interval = 1.0F / (endEdge - startEdge);
			float fading = 1.0F;
			
			if (this.animationEnd) {
				if (TrailInfo.isValidTime(this.trailInfo.fadeTime)) {
					fading = ((float)this.lifetime / (float)this.trailInfo.trailLifetime);
				} else {
					fading = Mth.clamp((this.lifetime + (1.0F - partialTick)) / this.trailInfo.trailLifetime, 0.0F, 1.0F);
				}
			}
			
			float partialStartEdge = interval * (startEdge % 1.0F);
			float from = -partialStartEdge;
			float to = -partialStartEdge + interval;
			
			for (int i = (int)(startEdge); i < (int)endEdge + 1; i++) {
				TrailEdge e1 = this.visibleTrailEdges.get(i);
				TrailEdge e2 = this.visibleTrailEdges.get(i + 1);
				Vector4f pos1 = new Vector4f((float)e1.start.x, (float)e1.start.y, (float)e1.start.z, 1.0F);
				Vector4f pos2 = new Vector4f((float)e1.end.x, (float)e1.end.y, (float)e1.end.z, 1.0F);
				Vector4f pos3 = new Vector4f((float)e2.end.x, (float)e2.end.y, (float)e2.end.z, 1.0F);
				Vector4f pos4 = new Vector4f((float)e2.start.x, (float)e2.start.y, (float)e2.start.z, 1.0F);
				
				pos1.mul(matrix4f);
				pos2.mul(matrix4f);
				pos3.mul(matrix4f);
				pos4.mul(matrix4f);
				
				float alphaFrom = Mth.clamp(from, 0.0F, 1.0F);
				float alphaTo = Mth.clamp(to, 0.0F, 1.0F);
				
				vertexConsumer.vertex(pos1.x(), pos1.y(), pos1.z()).uv(from, 1.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaFrom * fading).uv2(0).endVertex();
				vertexConsumer.vertex(pos2.x(), pos2.y(), pos2.z()).uv(from, 0.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaFrom * fading).uv2(0).endVertex();
				vertexConsumer.vertex(pos3.x(), pos3.y(), pos3.z()).uv(to, 0.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaTo * fading).uv2(0).endVertex();
				vertexConsumer.vertex(pos4.x(), pos4.y(), pos4.z()).uv(to, 1.0F).color(this.rCol, this.gCol, this.bCol, this.alpha * alphaTo * fading).uv2(0).endVertex();
				
				from += interval;
				to += interval;
			}
		}
		
		@Override
		protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
			float x = (float)AnimatedModelPlayer.this.xMove;
			float y = (float)AnimatedModelPlayer.this.yMove;
			float z = (float)AnimatedModelPlayer.this.zoom;
			float xRot = AnimatedModelPlayer.this.xRot;
			float yRot = AnimatedModelPlayer.this.yRot;
			
			poseStack.translate(x, y - 1.0D, z);
			poseStack.mulPose(QuaternionUtils.XP.rotationDegrees(xRot));
			poseStack.mulPose(QuaternionUtils.YP.rotationDegrees(yRot));
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
