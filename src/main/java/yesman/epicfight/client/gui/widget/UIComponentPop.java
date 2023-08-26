package yesman.epicfight.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.widget.UIComponent.PassiveUIComponent;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.config.ClientConfig.AlignDirection;
import yesman.epicfight.config.ClientConfig.HorizontalBasis;
import yesman.epicfight.config.ClientConfig.VerticalBasis;
import yesman.epicfight.config.Option;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class UIComponentPop<T extends UIComponent> extends Screen implements ContainerEventHandler {
	protected final T parentWidget;
	protected int width;
	protected int height;
	public int x;
	public int y;
	private boolean enable;
	
	public UIComponentPop(int width, int height, T parentWidget) {
		super(new TextComponent(""));
		
		this.width = width;
		this.height = height;
		this.parentWidget = parentWidget;
		
		this.init();
	}
	
	@Override
	public void init() {
		this.clearWidgets();
		
		this.addRenderableWidget(createButton(this.x + 10, this.y - 2, 11, 8, (button) -> {
			this.parentWidget.verticalBasis.setValue(VerticalBasis.TOP);
			this.parentWidget.yCoord.setValue(VerticalBasis.TOP.saveCoordGetter.apply(this.parentWidget.parentScreen.height, this.y));
		}));
		
		this.addRenderableWidget(createButton(this.x - 2, this.y + 11, 11, 7, (button) -> {
			this.parentWidget.horizontalBasis.setValue(HorizontalBasis.LEFT);
			this.parentWidget.xCoord.setValue(HorizontalBasis.LEFT.saveCoordGetter.apply(this.parentWidget.parentScreen.width, this.x));
		}));
		
		this.addRenderableWidget(createButton(this.x + 22, this.y + 11, 11, 7, (button) -> {
			this.parentWidget.horizontalBasis.setValue(HorizontalBasis.RIGHT);
			this.parentWidget.xCoord.setValue(HorizontalBasis.RIGHT.saveCoordGetter.apply(this.parentWidget.parentScreen.width, this.x));
		}));
		
		this.addRenderableWidget(createButton(this.x + 10, this.y + 24, 11, 8, (button) -> {
			this.parentWidget.verticalBasis.setValue(VerticalBasis.BOTTOM);
			this.parentWidget.yCoord.setValue(VerticalBasis.BOTTOM.saveCoordGetter.apply(this.parentWidget.parentScreen.height, this.y));
		}));
		
		this.addRenderableWidget(createButton(this.x + 10, this.y + 11, 11, 7, (button) -> {
			this.parentWidget.verticalBasis.setValue(VerticalBasis.CENTER);
			this.parentWidget.horizontalBasis.setValue(HorizontalBasis.CENTER);
			this.parentWidget.xCoord.setValue(HorizontalBasis.CENTER.saveCoordGetter.apply(this.parentWidget.parentScreen.width, this.x));
			this.parentWidget.yCoord.setValue(VerticalBasis.CENTER.saveCoordGetter.apply(this.parentWidget.parentScreen.height, this.y));
		}));
	}
	
	public static Button createButton(int x, int y, int width, int height, Button.OnPress onpress) {
		Button bt = new Button(x, y, width, height, new TextComponent(""), onpress);
		bt.setBlitOffset(400);
		
		return bt;
	}
	
	public void openPop() {
		this.enable = true;
		this.init();
	}
	
	public void closePop() {
		this.enable = false;
	}
	
	protected boolean isHoverd(double x, double y) {
		return this.enable && x >= this.x && y >= this.y && x < (this.x + this.width) && y < (this.y + this.height);
	}
	
	public boolean isOpen() {
		return this.enable;
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int pressType) {
		if (this.enable) {
			boolean clicked = false;
			
			for (GuiEventListener listener : this.children()) {
				clicked |= listener.mouseClicked(x, y, pressType);
			}
			
			return clicked;
		} else {
			return false;
		}
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		if (this.enable) {
			boolean popupOut = mouseX < this.x - 3 || mouseY < this.y - 3 || mouseX >= this.x + this.width + 3 || mouseY >= this.y + this.height + 3;
			boolean parentOut = mouseX < this.parentWidget.x - 3 || mouseY < this.parentWidget.y - 3
					|| mouseX >= this.parentWidget.x + this.parentWidget.getWidth() + 3 || mouseY >= this.parentWidget.y + this.parentWidget.getHeight() + 3;
			
			if (popupOut && parentOut) {
				this.enable = false;
			}
			
			this.renderPopup(poseStack, this.x, this.y, this.width, this.height);
			super.render(poseStack, mouseX, mouseY, partialTicks);
		}
	}
	
	protected void renderPopup(PoseStack poseStack, int x, int y, int width, int height) {
		int i = width;
		int j = height;
		int j2 = x;
		int k2 = y;
		
		poseStack.pushPose();
		
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		Matrix4f matrix4f = poseStack.last().pose();
		
		int backgroundStart = 0xf0100010;
		int backgroundEnd = 0xf0100010;
		int boarderStart = 0x505000FF;
		int boarderEnd = 0x5028007F;
		fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, 400, backgroundStart, backgroundStart);
		fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, 400, backgroundEnd, backgroundEnd);
		fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 400, backgroundStart, backgroundEnd);
		fillGradient(matrix4f, bufferbuilder, j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, 400, backgroundStart, backgroundEnd);
		fillGradient(matrix4f, bufferbuilder, j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, 400, backgroundStart, backgroundEnd);
		fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 400, boarderStart, boarderEnd);
		fillGradient(matrix4f, bufferbuilder, j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 400, boarderStart, boarderEnd);
		fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 400, boarderStart, boarderStart);
		fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 400, boarderEnd, boarderEnd);
		RenderSystem.enableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		
		poseStack.popPose();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class PassivesUIComponentPop extends UIComponentPop<PassiveUIComponent> {
		public PassivesUIComponentPop(int width, int height, PassiveUIComponent parentWidget) {
			super(width, height, parentWidget);
		}
		
		@Override
		protected void renderPopup(PoseStack poseStack, int x, int y, int width, int height) {
			super.renderPopup(poseStack, x, y + 14, width, height - 14);
		}
		
		@Override
		public void init() {
			super.init();
			
			for (GuiEventListener gui : this.children()) {
				if (gui instanceof AbstractWidget widget) {
					widget.y += 14;
				}
			}
			
			this.addRenderableWidget(new AlignButton(this.x - 3, this.y, 12, 10, this.parentWidget.horizontalBasis, this.parentWidget.verticalBasis, this.parentWidget.alignDirection, (button) -> {
				AlignDirection newAlignDirection = AlignDirection.values()[(this.parentWidget.alignDirection.getValue().ordinal() + 1) % AlignDirection.values().length];
				this.parentWidget.alignDirection.setValue(newAlignDirection);
			}));
		}
		
		public static class AlignButton extends Button {
			private static final ResourceLocation BATTLE_ICONS = new ResourceLocation(EpicFightMod.MODID, "textures/gui/battle_icons.png");
			private Option<HorizontalBasis> horBasis;
			private Option<VerticalBasis> verBasis;
			private Option<AlignDirection> alignDirection;
			
			public AlignButton(int x, int y, int width, int height, Option<HorizontalBasis> horBasis, Option<VerticalBasis> verBasis, Option<AlignDirection> alignDirection, OnPress onpress) {
				super(x, y, width, height, new TextComponent(""), onpress);
				
				this.horBasis = horBasis;
				this.verBasis = verBasis;
				this.alignDirection = alignDirection;
			}
			
			@Override
			public void renderButton(PoseStack poseStack, int x, int y, float partialTicks) {
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, BATTLE_ICONS);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				
				Vec2[] texCoords = new Vec2[4];
				
				float startX;
				float startY;
				float width;
				float height;
				
				if (this.isHovered) {
					startX = 132 / 255.0F;
					startY = 0;
					width = 36 / 255.0F;
					height = 36 / 255.0F;
					
					//GuiComponent.blit(poseStack, this.x, this.y, this.width, this.height, 132, 0, 36, 36, 255, 255);
				} else {
					startX = 97 / 255.0F;
					startY = 2 / 255.0F;
					width = 31 / 255.0F;
					height = 31 / 255.0F;
					
					//GuiComponent.blit(poseStack, this.x, this.y, this.width, this.height, 97, 2, 31, 31, 255, 255);
				}
				
				Vec2 uv0 = new Vec2(startX, startY);
				Vec2 uv1 = new Vec2(startX + width, startY);
				Vec2 uv2 = new Vec2(startX + width, startY + height);
				Vec2 uv3 = new Vec2(startX, startY + height);
				
				texCoords[0] = uv0;
				texCoords[1] = uv1;
				texCoords[2] = uv2;
				texCoords[3] = uv3;
				
				if (this.alignDirection.getValue() == ClientConfig.AlignDirection.HORIZONTAL) {
					if (this.horBasis.getValue() == ClientConfig.HorizontalBasis.LEFT) {
						texCoords[0] = uv1;
						texCoords[1] = uv2;
						texCoords[2] = uv3;
						texCoords[3] = uv0;
					} else {
						texCoords[0] = uv3;
						texCoords[1] = uv0;
						texCoords[2] = uv1;
						texCoords[3] = uv2;
					}
				} else {
					if (this.verBasis.getValue() == ClientConfig.VerticalBasis.BOTTOM) {
						texCoords[0] = uv2;
						texCoords[1] = uv3;
						texCoords[2] = uv0;
						texCoords[3] = uv1;
					}
				}
				
				this.blitRotate(poseStack, texCoords);
			}
			
			public void blitRotate(PoseStack poseStack, Vec2[] texCoords) {
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
				bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
				bufferbuilder.vertex(poseStack.last().pose(), this.x, this.y, this.getBlitOffset()).uv(texCoords[0].x, texCoords[0].y).endVertex();
				bufferbuilder.vertex(poseStack.last().pose(), this.x + this.width, this.y, this.getBlitOffset()).uv(texCoords[1].x, texCoords[1].y).endVertex();
				bufferbuilder.vertex(poseStack.last().pose(), this.x + this.width, this.y + this.height, this.getBlitOffset()).uv(texCoords[2].x, texCoords[2].y).endVertex();
				bufferbuilder.vertex(poseStack.last().pose(), this.x, this.y + this.height, this.getBlitOffset()).uv(texCoords[3].x, texCoords[3].y).endVertex();
				bufferbuilder.end();
				BufferUploader.end(bufferbuilder);
			}
		}
	}
}