package yesman.epicfight.client.gui.datapack.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorPreviewWidget extends AbstractWidget implements ResizableComponent {
	private int packedColor;
	
	public ColorPreviewWidget(int x1, int x2, int y1, int y2, HorizontalSizing horizontalSizingOption, VerticalSizing verticalSizingOption, Component title) {
		super(x1, y1, x2, y2, title);
		
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.horizontalSizingOption = horizontalSizingOption;
		this.verticalSizingOption = verticalSizingOption;
	}
	
	public void setColor(int r, int g, int b) {
		this.packedColor = (0xFF000000 | r << 16 | g << 8 | b);
	}
	
	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -1);
		guiGraphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.getWidth() - 1, this.getY() + this.getHeight() - 1, -16777216);
		guiGraphics.fill(this.getX() + 2, this.getY() + 2, this.getX() + this.getWidth() - 2, this.getY() + this.getHeight() - 2, this.packedColor);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
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
		this.active = active;
	}
	
	@Override
	public void tick() {
	}
}
