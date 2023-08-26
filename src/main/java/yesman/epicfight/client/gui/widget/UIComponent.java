package yesman.epicfight.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.screen.UISetupScreen;
import yesman.epicfight.client.gui.widget.UIComponentPop.PassivesUIComponentPop;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.config.ClientConfig.AlignDirection;
import yesman.epicfight.config.ClientConfig.HorizontalBasis;
import yesman.epicfight.config.ClientConfig.VerticalBasis;
import yesman.epicfight.config.Option;

@OnlyIn(Dist.CLIENT)
public class UIComponent extends Button {
	protected final UISetupScreen parentScreen;
	protected final ResourceLocation texture;
	protected int texU;
	protected int texV;
	protected int texW;
	protected int texH;
	protected int resolutionDivW;
	protected int resolutionDivH;
	protected int draggingTime;
	protected float r;
	protected float g;
	protected float b;
	private double pressX;
	private double pressY;
	public final Option<Integer> xCoord;
	public final Option<Integer> yCoord;
	public final Option<HorizontalBasis> horizontalBasis;
	public final Option<VerticalBasis> verticalBasis;
	
	public UIComponentPop<?> popupScreen;
	
	public UIComponent(int x, int y, Option<Integer> xCoord, Option<Integer> yCoord, Option<HorizontalBasis> horizontalBasis, Option<VerticalBasis> verticalBasis
			, int width, int height, int texU, int texV, int texW, int texH, int resolutionDivW, int resolutionDivH, int r, int g, int b
			, UISetupScreen parentScreen, ResourceLocation texture) {
		
		super(x, y, width, height, new TextComponent(""), (button) -> {}, NO_TOOLTIP);
		
		this.texture = texture;
		this.texU = texU;
		this.texV = texV;
		this.texW = texW;
		this.texH = texH;
		this.resolutionDivW = resolutionDivW;
		this.resolutionDivH = resolutionDivH;
		this.r = r / 255.0F;
		this.g = g / 255.0F;
		this.b = b / 255.0F;
		
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.horizontalBasis = horizontalBasis;
		this.verticalBasis = verticalBasis;
		this.parentScreen = parentScreen;
		this.popupScreen = new UIComponentPop<>(30, 30, this);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				this.draggingTime = 0;
				
				if (this.clicked(mouseX, mouseY)) {
					this.parentScreen.beginToDrag(this);
					this.pressX = mouseX - this.x;
					this.pressY = mouseY - this.y;
					this.playDownSound(Minecraft.getInstance().getSoundManager());
					
					if (!this.popupScreen.isHoverd(x, y)) {
						this.popupScreen.closePop();
					}
					
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}
	
	@Override
	protected void onDrag(double x, double y, double dx, double dy) {
		if (this.parentScreen.isDraggingComponent(this)) {
			this.x = (int)(x - this.pressX);
			this.y = (int)(y - this.pressY);
			this.draggingTime++;
		}
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.isValidClickButton(button)) {
			this.onRelease(mouseX, mouseY);
			this.parentScreen.endDragging();
			
			int xCoord = this.horizontalBasis.getValue().saveCoordGetter.apply(this.parentScreen.width, (int)x);
			int yCoord = this.verticalBasis.getValue().saveCoordGetter.apply(this.parentScreen.height, (int)y);
			
			this.xCoord.setValue(xCoord);
			this.yCoord.setValue(yCoord);
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void onRelease(double x, double y) {
		if (!this.popupScreen.isOpen() && this.draggingTime < 2) {
			if (x + this.popupScreen.width > this.parentScreen.width) {
				this.popupScreen.x = (int)x - this.popupScreen.width;
			} else {
				this.popupScreen.x = (int)x;
			}
			
			if (y + this.popupScreen.height > this.parentScreen.height) {
				this.popupScreen.y = (int)y - this.popupScreen.height;
			} else {
				this.popupScreen.y = (int)y;
			}
			
			this.popupScreen.openPop();
		}
	}
	
	public void drawOutline(PoseStack poseStack) {
		float screenX = this.x - 1;
		float screenXEnd = (this.x + this.width) + 1;
		float screenY = this.y - 1;
		float screenYEnd = (this.y + this.height) + 1;
		
        RenderSystem.disableCull();
		RenderSystem.lineWidth(2.0F);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		
		RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		
		bufferbuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
		bufferbuilder.vertex(poseStack.last().pose(), screenX, screenY, 0).color(69, 166, 244, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), screenXEnd, screenY, 0).color(69, 166, 244, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
		
		bufferbuilder.vertex(poseStack.last().pose(), screenXEnd, screenY, 0).color(69, 166, 244, 255).normal(0.0F, -1.0F, 0.0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), screenXEnd, screenYEnd, 0).color(69, 166, 244, 255).normal(0.0F, -1.0F, 0.0F).endVertex();
		
		bufferbuilder.vertex(poseStack.last().pose(), screenXEnd, screenYEnd, 0).color(69, 166, 244, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), screenX, screenYEnd, 0).color(69, 166, 244, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();
		
		bufferbuilder.vertex(poseStack.last().pose(), screenX, screenYEnd, 0).color(69, 166, 244, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
		bufferbuilder.vertex(poseStack.last().pose(), screenX, screenY, 0).color(69, 166, 244, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
		
		if (this.horizontalBasis.getValue() == ClientConfig.HorizontalBasis.CENTER) {
			bufferbuilder.vertex(poseStack.last().pose(), screenX + (screenXEnd - screenX) / 2.0F, screenY + (screenYEnd - screenY) / 2.0F, 0).color(69, 166, 244, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.vertex(poseStack.last().pose(), this.parentScreen.width / 2, screenY + (screenYEnd - screenY) / 2.0F, 0).color(69, 166, 244, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
		} else if (this.horizontalBasis.getValue() == ClientConfig.HorizontalBasis.LEFT) {
			bufferbuilder.vertex(poseStack.last().pose(), screenX, screenY, 0).color(69, 166, 244, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.vertex(poseStack.last().pose(), 0, screenY, 0).color(69, 166, 244, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
		} else if (this.horizontalBasis.getValue() == ClientConfig.HorizontalBasis.RIGHT) {
			bufferbuilder.vertex(poseStack.last().pose(), screenX, screenY, 0).color(69, 166, 244, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.vertex(poseStack.last().pose(), this.parentScreen.width, screenY, 0).color(69, 166, 244, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
		}
		
		if (this.verticalBasis.getValue() == ClientConfig.VerticalBasis.CENTER) {
			bufferbuilder.vertex(poseStack.last().pose(), screenX + (screenXEnd - screenX) / 2.0F, screenY + (screenYEnd - screenY) / 2.0F, 0).color(69, 166, 244, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.vertex(poseStack.last().pose(), screenX + (screenXEnd - screenX) / 2.0F, this.parentScreen.height / 2, 0).color(69, 166, 244, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
		} else if (this.verticalBasis.getValue() == ClientConfig.VerticalBasis.TOP) {
			bufferbuilder.vertex(poseStack.last().pose(), screenX, screenY, 0).color(69, 166, 244, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.vertex(poseStack.last().pose(), screenX, 0, 0).color(69, 166, 244, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
		} else if (this.verticalBasis.getValue() == ClientConfig.VerticalBasis.BOTTOM) {
			bufferbuilder.vertex(poseStack.last().pose(), screenX, screenY, 0).color(69, 166, 244, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.vertex(poseStack.last().pose(), screenX, this.parentScreen.height, 0).color(69, 166, 244, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
		}
		
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
	}
	
	@Override
	public void renderButton(PoseStack poseStack, int x, int y, float partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, this.texture);
		RenderSystem.setShaderColor(this.r, this.g, this.b, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		
		GuiComponent.blit(poseStack, this.x, this.y, this.width, this.height, this.texU, this.texV, this.texW, this.texH, this.resolutionDivW, this.resolutionDivH);
		
		if (this.isHoveredOrFocused() || this.popupScreen.isOpen()) {
			this.drawOutline(poseStack);
		}
		
		if (this.popupScreen.isOpen()) {
			this.popupScreen.render(poseStack, x, y, partialTicks);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class PassiveUIComponent extends UIComponent {
		public final Option<AlignDirection> alignDirection;
		protected final ResourceLocation texture2;
		
		public PassiveUIComponent(int x, int y, Option<Integer> xCoord, Option<Integer> yCoord, Option<HorizontalBasis> horizontalBasis, Option<VerticalBasis> verticalBasis, Option<AlignDirection> alignDirection
				, int width, int height, int texU, int texV, int texW, int texH, int resolutionDivW, int resolutionDivH, int r, int g, int b, UISetupScreen parentScreen, ResourceLocation texture, ResourceLocation texture2) {
			super(x, y, xCoord, yCoord, horizontalBasis, verticalBasis, width, height, texU, texV, texW, texH, resolutionDivW, resolutionDivH, r, g, b, parentScreen, texture);
		
			this.popupScreen = new PassivesUIComponentPop(30, 44, this);
			this.alignDirection = alignDirection;
			this.texture2 = texture2;
		}
		
		@Override
		public void renderButton(PoseStack poseStack, int x, int y, float partialTicks) {
			Vec2i startPos = this.alignDirection.getValue().startCoordGetter.get(this.x, this.y, this.width, this.height, 2, this.horizontalBasis.getValue(), this.verticalBasis.getValue());
			
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, this.texture);
			RenderSystem.setShaderColor(this.r, this.g, this.b, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			
			GuiComponent.blit(poseStack, startPos.x, startPos.y, this.width, this.height, this.texU, this.texV, this.texW, this.texH, this.resolutionDivW, this.resolutionDivH);
			
			if (this.isHoveredOrFocused() || this.popupScreen.isOpen()) {
				this.drawOutline(poseStack);
			}
			
			if (this.popupScreen.isOpen()) {
				this.popupScreen.render(poseStack, x, y, partialTicks);
			}
			
			Vec2i nextPos = this.alignDirection.getValue().nextPositionGetter.getNext(this.horizontalBasis.getValue(), this.verticalBasis.getValue(), startPos, this.width, this.height);
			
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, this.texture2);
			RenderSystem.setShaderColor(this.r, this.g, this.b, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			GuiComponent.blit(poseStack, nextPos.x, nextPos.y, this.width, this.height, this.texU, this.texV, this.texW, this.texH, this.resolutionDivW, this.resolutionDivH);
		}
	}
}
