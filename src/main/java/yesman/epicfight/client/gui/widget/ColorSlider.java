package yesman.epicfight.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import yesman.epicfight.config.Option.DoubleOption;

public class ColorSlider extends AbstractSlider {
	private static final int[] COLOR_ARRAY = { 0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000 };
	private final DoubleOption colorOption;
	
	public ColorSlider(int x, int y, int width, int height, ITextComponent message, double defaultValue, DoubleOption option) {
		super(x, y, width, height, message, defaultValue);
		this.colorOption = option;
	}
	
	@Override
	public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontrenderer = minecraft.fontRenderer;
		RenderSystem.enableBlend();
		
		for (int i = 0; i < 6; i++) {
			this.fillGradient(matrixStack, this.x + (this.width * i / 6), this.y, this.x + (this.width * (i + 1) / 6), this.y + this.height,
					COLOR_ARRAY[i], COLOR_ARRAY[i+1]);
		}
		
		this.renderBg(matrixStack, minecraft, mouseX, mouseY);
		int j = getFGColor();
		drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY) {
		minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int i = (this.isHovered() ? 2 : 1) * 20;
		int minX = this.x + (int) (this.sliderValue * (double) (this.width - 8));
		this.blit(matrixStack, minX, this.y, 0, 46 + i, 4, 20);
		this.blit(matrixStack, minX + 4, this.y, 196, 46 + i, 4, 20);
		fill(matrixStack, minX + 1, this.y + 1, minX + 7, this.y + 19, toColorInteger(this.sliderValue));
	}
	
	@Override
	protected void func_230979_b_() {
		this.colorOption.setValue(this.sliderValue);
	}

	@Override
	protected void func_230972_a_() {
		
	}
	
	public static int toColorInteger(double sliderValue) {
		int packedColor = 0;
		
		for (int i = 0; i < 6; i++) {
			double min = 1.0D / 6.0D * i;
			double max = 1.0D / 6.0D * (i + 1);
			if (sliderValue >= min && sliderValue <= max) {
				double lerpFactor = (sliderValue - min) / (max - min);
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
				int r = (int) MathHelper.lerp(lerpFactor, f, f4);
				int g = (int) MathHelper.lerp(lerpFactor, f1, f5);
				int b = (int) MathHelper.lerp(lerpFactor, f2, f6);
				int a = (int) MathHelper.lerp(lerpFactor, f3, f7);
				packedColor = r << 24 | g << 16 | b << 8 | a;
			}
		}
		
		return packedColor;
	}
	
	@Override
	protected void fillGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int colorA, int colorB) {
		matrixStack.push();
		int width = x2 - x1;
		int height = y2 - y1;
		int newX = x1 + width / 2;
		int newY = y1 + height / 2;
		matrixStack.translate(newX, newY, 0.0F);
		matrixStack.rotate(new Quaternion(new Vector3f(0.0F, 0.0F, 1.0F), -90.0F, true));
		super.fillGradient(matrixStack, -height/2, -width/2, height/2, width/2, colorA, colorB);
		matrixStack.pop();
	}
}