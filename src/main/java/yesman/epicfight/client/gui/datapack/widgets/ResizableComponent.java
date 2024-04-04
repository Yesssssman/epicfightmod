package yesman.epicfight.client.gui.datapack.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ResizableComponent extends GuiEventListener, NarratableEntry {
	default void resize(ScreenRectangle screenRectangle) {
		if (this.getHorizontalSizingOption() != null) {
			this.getHorizontalSizingOption().resizeFunction.resize(this, screenRectangle, this.getX1(), this.getX2());
		}
		
		if (this.getVerticalSizingOption() != null) {
			this.getVerticalSizingOption().resizeFunction.resize(this, screenRectangle, this.getY1(), this.getY2());
		}
	}
	
	default ResizableComponent relocateX(ScreenRectangle screenrect, int screenX) {
		this.setX(screenX);
		
		if (this.getHorizontalSizingOption() == HorizontalSizing.WIDTH_RIGHT) {
			this.setX2(screenrect.right() - (screenX + this.getWidth()));
		} else {
			this.setX1(screenX);
		}
		
		return this;
	}
	
	default ResizableComponent relocateY(ScreenRectangle screenrect, int screenY) {
		this.setY(screenY);
		
		if (this.getVerticalSizingOption() == VerticalSizing.HEIGHT_BOTTOM) {
			this.setY2(screenrect.bottom() - (screenY + this.getHeight()));
		} else {
			this.setY1(screenY);
		}
		
		return this;
	}
	
	int getX1();
	int getX2();
	int getY1();
	int getY2();
	void setX1(int x1);
	void setX2(int x2);
	void setY1(int y1);
	void setY2(int y2);
	HorizontalSizing getHorizontalSizingOption();
	VerticalSizing getVerticalSizingOption();
	
	@OnlyIn(Dist.CLIENT)
	public static enum HorizontalSizing {
		LEFT_WIDTH((component, screenRectangle, v1, v2) -> {
			component.setX(v1);
			component.setWidth(v2);
		}), LEFT_RIGHT((component, screenRectangle, v1, v2) -> {
			int end = screenRectangle.right() - v2;
			int width = Math.max(end - v1, 0);
			component.setX(v1);
			component.setWidth(width);
		}), WIDTH_RIGHT((component, screenRectangle, v1, v2) -> {
			int end = screenRectangle.right() - v2;
			int start = Math.max(end - v1, 0);
			component.setX(start);
			component.setWidth(v1);
		});
		
		ResizeFunction resizeFunction;
		
		HorizontalSizing(ResizeFunction resizeFunction) {
			this.resizeFunction = resizeFunction;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static enum VerticalSizing {
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
		
		VerticalSizing(ResizeFunction resizeFunction) {
			this.resizeFunction = resizeFunction;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@FunctionalInterface
	interface ResizeFunction {
		public void resize(ResizableComponent component, ScreenRectangle screenRectangle, int v1, int v2);
	}
	
	/*****************************************
	 *        Vanilla Widget Functions       *
	 *****************************************/
	void tick();
	
	void setActive(boolean active);
	
	void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);
	
	int getX();
	int getY();
	int getWidth();
	int getHeight();
	void setX(int x);
	void setY(int y);
	void setWidth(int width);
	void setHeight(int height);
}