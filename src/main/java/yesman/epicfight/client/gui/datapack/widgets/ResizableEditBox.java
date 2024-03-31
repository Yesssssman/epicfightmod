package yesman.epicfight.client.gui.datapack.widgets;

import javax.annotation.Nullable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class ResizableEditBox extends EditBox implements DataBindingComponent<String> {
	public ResizableEditBox(Font font, int x, int y, int width, int height, Component title, HorizontalSizing horizontalSizingOption, VerticalSizing verticalSizingOption) {
		super(font, x, y, width, height, title);
		
		this.x1 = x;
		this.x2 = width;
		this.y1 = y;
		this.y2 = height;
		this.horizontalSizingOption = horizontalSizingOption;
		this.verticalSizingOption = verticalSizingOption;
	}
	
	public ResizableEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox editbox, Component title, HorizontalSizing horizontalSizingOption, VerticalSizing verticalSizingOption) {
		super(font, x, y, width, height, editbox, title);
		
		this.x1 = x;
		this.x2 = width;
		this.y1 = y;
		this.y2 = height;
		this.horizontalSizingOption = horizontalSizingOption;
		this.verticalSizingOption = verticalSizingOption;
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
	public void reset() {
		this.setValue("");
	}
}
