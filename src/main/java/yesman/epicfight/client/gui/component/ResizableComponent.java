package yesman.epicfight.client.gui.component;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface ResizableComponent {
	default void resize(ScreenRectangle screenRectangle) {
		//System.out.println(this.getX1() +" "+ this.getX2() +" "+ this.getY1() +" "+ this.getY2() +" "+ this.getVerticalSizingOption() +" "+ this.getHorizontalSizingOption() );
		
		if (this.getHorizontalSizingOption() != null) {
			this.getHorizontalSizingOption().resizeFunction.resize(this, screenRectangle, this.getX1(), this.getX2());
		}
		
		if (this.getVerticalSizingOption() != null) {
			this.getVerticalSizingOption().resizeFunction.resize(this, screenRectangle, this.getY1(), this.getY2());
		}
	}
	
	void setX(int x);
	void setY(int y);
	void setWidth(int width);
	void setHeight(int height);
	
	int getX1();
	int getX2();
	int getY1();
	int getY2();
	HorizontalSizingOption getHorizontalSizingOption();
	VerticalSizingOption getVerticalSizingOption();
	
	public static enum HorizontalSizingOption {
		LEFT_WIDTH((component, screenRectangle, v1, v2) -> {
			component.setX(v1);
			component.setWidth(v2);
		}), LEFT_RIGHT((component, screenRectangle, v1, v2) -> {
			int end = screenRectangle.right() - v2;
			int width = Math.max(end - v1, 0);
			component.setX(v1);
			component.setWidth(width);
			//System.out.println(v1 +" "+ v2 + " " + end +" "+ width);
		}), WIDTH_RIGHT((component, screenRectangle, v1, v2) -> {
			int end = screenRectangle.right() - v2;
			int start = Math.max(end - v1, 0);
			component.setX(start);
			component.setWidth(v1);
		});
		
		ResizeFunction resizeFunction;
		
		HorizontalSizingOption(ResizeFunction resizeFunction) {
			this.resizeFunction = resizeFunction;
		}
	}
	
	public static enum VerticalSizingOption {
		TOP_HEIGHT((component, screenRectangle, v1, v2) -> {
			component.setY(v1);
			component.setHeight(v2);
		}), TOP_BOTTOM((component, screenRectangle, v1, v2) -> {
			int end = screenRectangle.bottom() - v2;
			int height = Math.max(end - v1, 0);
			component.setY(v1);
			component.setHeight(height);
		}), HEIGHT_BOTTOM((component, screenRectangle, v1, v2) -> {
			int end = screenRectangle.bottom() - v2;
			int start = Math.max(end - v1, 0);
			component.setY(start);
			component.setHeight(v1);
		});
		
		ResizeFunction resizeFunction;
		
		VerticalSizingOption(ResizeFunction resizeFunction) {
			this.resizeFunction = resizeFunction;
		}
	}
	
	@FunctionalInterface
	interface ResizeFunction {
		public void resize(ResizableComponent component, ScreenRectangle screenRectangle, int v1, int v2);
	}
}