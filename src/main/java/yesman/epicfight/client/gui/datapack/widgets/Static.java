package yesman.epicfight.client.gui.datapack.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Static extends AbstractWidget implements ResizableComponent {
	private Font font;
	
	public Static(Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, Component message) {
		super(x1, y1, x2, y2, message);
		
		this.font = font;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.horizontalSizingOption = horizontal;
		this.verticalSizingOption = vertical;
	}
	
	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		String correctedString = this.getMessage() == null ? "" : this.font.plainSubstrByWidth(this.getMessage().getString(), this.getWidth());
		guiGraphics.drawString(this.font, correctedString, this.getX(), this.getY() + this.height / 2 - this.font.lineHeight / 2, 16777215, false);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
	}
	
	@Override
	public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
		return false;
	}
	
	@Override
	public boolean mouseReleased(double p_93684_, double p_93685_, int p_93686_) {
		return false;
	}

	public boolean mouseDragged(double p_93645_, double p_93646_, int p_93647_, double p_93648_, double p_93649_) {
		return false;
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