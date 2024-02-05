package yesman.epicfight.client.gui.component;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ResizableButton extends Button implements ResizableComponent {
	public ResizableButton(ResizableButton.Builder builder) {
		super(builder);
		
		this.x1 = builder.x1;
		this.x2 = builder.x2;
		this.y1 = builder.y1;
		this.y2 = builder.y2;
		this.horizontalSizingOption = builder.horizontalSizing;
		this.verticalSizingOption = builder.verticalSizing;
	}
	
	public static ResizableButton.Builder builder(Component title, Button.OnPress onPress) {
		return new ResizableButton.Builder(title, onPress);
	}

	/*******************************************************************
	 * @ResizableComponent variables                                   *
	 *******************************************************************/
	private final int x1;
	private final int x2;
	private final int y1;
	private final int y2;
	private final HorizontalSizing horizontalSizingOption;
	private final VerticalSizing verticalSizingOption;
	
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
	
	@OnlyIn(Dist.CLIENT)
	public static class Builder extends Button.Builder {
		private int x1;
		private int x2;
		private int y1;
		private int y2;
		private HorizontalSizing horizontalSizing = HorizontalSizing.LEFT_WIDTH;
		private VerticalSizing verticalSizing = null;
		
		public Builder(Component title, Button.OnPress onPress) {
			super(title, onPress);
		}

		public Builder x1(int x1) {
			this.x1 = x1;
			return this;
		}
		
		public Builder x2(int x2) {
			this.x2 = x2;
			return this;
		}
		
		public Builder y1(int y1) {
			this.y1 = y1;
			return this;
		}
		
		public Builder y2(int y2) {
			this.y2 = y2;
			return this;
		}
		
		public Builder horizontalSizing(HorizontalSizing horizontalSizing) {
			this.horizontalSizing = horizontalSizing;
			return this;
		}
		
		public Builder verticalSizing(VerticalSizing verticalSizing) {
			this.verticalSizing = verticalSizing;
			return this;
		}
		
		@Override
		public ResizableButton.Builder pos(int x, int y) {
			super.pos(x, y);
			this.x1 = x;
			this.y1 = y;
			
			return this;
		}
		
		@Override
		public ResizableButton.Builder width(int width) {
			this.x2 = width;
			return this;
		}
		
		@Override
		public ResizableButton.Builder size(int width, int height) {
			super.size(width, height);
			
			this.x2 = width;
			this.y2 = height;
			
			return this;
		}
		
		@Override
		public ResizableButton.Builder bounds(int x, int y, int width, int height) {
			return this.pos(x, y).size(width, height);
		}
		
		@Override
		public ResizableButton build() {
			return new ResizableButton(this);
		}
	}
}