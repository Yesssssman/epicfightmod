package yesman.epicfight.client.gui.datapack.widgets;

import java.util.function.Consumer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CheckBox extends AbstractWidget implements DataBindingComponent<Boolean> {
	private final Font font;
	private final boolean defaultVal;
	
	private Consumer<Boolean> responder;
	private Boolean value;
	
	public CheckBox(Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, boolean defaultVal, Component title, Consumer<Boolean> responder) {
		super(x1, y1, x2, y2, title);
		
		this.font = font;
		this.defaultVal = defaultVal;
		this.responder = responder;
		this.setValue(defaultVal);
		
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.horizontalSizingOption = horizontal;
		this.verticalSizingOption = vertical;
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean flag = this.clicked(x, y);
				
				if (flag) {
					this.onClick(x, y);
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	protected boolean clicked(double x, double y) {
		return this.active && this.visible && x >= (double)this.getX() && y >= (double) this.getY() && x < (double) (this.getX() + this.width) && y < (double) (this.getY() + this.height);
	}
	
	@Override
	public void onClick(double x, double y) {
		this.setValue(this.value == null ? !this.defaultVal : !this.value.booleanValue());
	}
	
	@Override
	public boolean isMouseOver(double x, double y) {
		int rectangleLength = Math.min(this.getWidth(), this.getHeight());
		return this.active && this.visible && x >= (double)this.getX() && y >= (double)this.getY() && x < (double)(this.getX() + rectangleLength) && y < (double)(this.getY() + rectangleLength);
	}
	
	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int rectangleLength = Math.min(this.getWidth(), this.getHeight());
		int outlineColor = this.isFocused() ? -1 : this.isActive() ? -6250336 : -12566463;
		
		guiGraphics.fill(this.getX(), this.getY(), this.getX() + rectangleLength, this.getY() + rectangleLength, outlineColor);
		guiGraphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + rectangleLength - 1, this.getY() + rectangleLength - 1, -16777216);
		
		if (this.value == null ? this.defaultVal : this.value.booleanValue()) {
			guiGraphics.fill(this.getX() + 2, this.getY() + 2, this.getX() + rectangleLength - 2, this.getY() + rectangleLength - 2, -1);
		}
		
		int fontColor = this.isActive() ? 16777215 : 4210752;
		
		guiGraphics.drawString(this.font, this.getMessage(), this.getX() + rectangleLength + 4, this.getY() + this.height / 2 - this.font.lineHeight / 2 + 1, fontColor, false);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementInput) {
		narrationElementInput.add(NarratedElementType.TITLE, this.createNarrationMessage());
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
	public void setResponder(Consumer<Boolean> responder) {
		this.responder = responder;
	}
	
	@Override
	public void setValue(Boolean value) {
		this.value = value;
		
		if (this.responder != null) {
			this.responder.accept(value == null ? this.defaultVal : value.booleanValue());
		}
	}
	
	@Override
	public Boolean getValue() {
		return this.value;
	}
	
	@Override
	public void reset() {
		this.setValue(this.defaultVal);
	}
	
	@Override
	public void tick() {
	}

}