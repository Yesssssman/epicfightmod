package yesman.epicfight.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import yesman.epicfight.config.Option.DoubleOption;

public class ColorSlider extends AbstractSliderButton {
	private static final int[] COLOR_ARRAY = { 0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000 };
	private final DoubleOption colorOption;
	
	public ColorSlider(int x, int y, int width, int height, Component message, double defaultValue, DoubleOption option) {
		super(x, y, width, height, message, defaultValue);
		this.colorOption = option;
	}
	
	@Override
	public void renderButton(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		Font fontrenderer = minecraft.font;
		RenderSystem.enableBlend();
		
		for (int i = 0; i < 6; i++) {
			this.fillGradient(PoseStack, this.x + (this.width * i / 6), this.y, this.x + (this.width * (i + 1) / 6), this.y + this.height, COLOR_ARRAY[i], COLOR_ARRAY[i+1]);
		}
		
		this.renderBg(PoseStack, minecraft, mouseX, mouseY);
		int j = getFGColor();
		drawCenteredString(PoseStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
	}
	
	@Override
	protected void renderBg(PoseStack PoseStack, Minecraft minecraft, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		int i = (this.isHoveredOrFocused() ? 2 : 1) * 20;
		int minX = this.x + (int) (this.value * (double) (this.width - 8));
		this.blit(PoseStack, minX, this.y, 0, 46 + i, 4, 20);
		this.blit(PoseStack, minX + 4, this.y, 196, 46 + i, 4, 20);
		fill(PoseStack, minX + 1, this.y + 1, minX + 7, this.y + 19, toColorInteger(this.value));
	}
	
	@Override
	protected void applyValue() {
		this.colorOption.setValue(this.value);
	}

	@Override
	protected void updateMessage() {
		
	}
	
	public static int toColorInteger(double value) {
		int packedColor = 0;
		
		for (int i = 0; i < 6; i++) {
			double min = 1.0D / 6.0D * i;
			double max = 1.0D / 6.0D * (i + 1);
			if (value >= min && value <= max) {
				double lerpFactor = (value - min) / (max - min);
				int colorA = COLOR_ARRAY[i];
				int colorB = COLOR_ARRAY[i + 1];
				int f = colorA >> 24 & 255;
				int f1 = colorA >> 16 & 255;
				int f2 = colorA >> 8 & 255;
				int f3 = colorA & 255;
				int f4 = colorB >> 24 & 255;
				int f5 = colorB >> 16 & 255;
				int f6 = colorB >> 8 & 255;
				int f7 = colorB & 255;
				int r = (int) Mth.lerp(lerpFactor, f, f4);
				int g = (int) Mth.lerp(lerpFactor, f1, f5);
				int b = (int) Mth.lerp(lerpFactor, f2, f6);
				int a = (int) Mth.lerp(lerpFactor, f3, f7);
				packedColor = r << 24 | g << 16 | b << 8 | a;
			}
		}
		
		return packedColor;
	}
	
	@Override
	protected void fillGradient(PoseStack poseStack, int x1, int y1, int x2, int y2, int colorA, int colorB) {
		poseStack.pushPose();
		int width = x2 - x1;
		int height = y2 - y1;
		int newX = x1 + width / 2;
		int newY = y1 + height / 2;
		poseStack.translate(newX, newY, 0.0F);
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
		super.fillGradient(poseStack, -height/2, -width/2, height/2, width/2, colorA, colorB);
		poseStack.popPose();
	}
}