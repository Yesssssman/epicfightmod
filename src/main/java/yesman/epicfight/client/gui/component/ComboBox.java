package yesman.epicfight.client.gui.component;

import java.util.List;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ComboBox<T> extends AbstractWidget implements ResizableComponent {
	private final ComboItemList comboItemList;
	private final Font font;
	private final int rows;
	
	private boolean listOpened;
	
	public ComboBox(Screen parent, Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, int maxRows, Component title, List<T> items, Function<T, String> displayStringMapper) {
		super(x1, x2, y1, y2, title);
		
		this.font = font;
		this.rows = maxRows;
		
		this.comboItemList = new ComboItemList(parent.getMinecraft(), maxRows, 15);
		
		for (T item : items) {
			this.comboItemList.addEntry(item, displayStringMapper.apply(item));
		}
		
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
			if (this.listOpened && this.comboItemList.mouseClicked(x, y, button)) {
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				this.listOpened = false;
				return true;
			} else {
				if (this.isValidClickButton(button)) {
					boolean flag = this.clicked(x, y);
					
					if (flag) {
						this.onClick(x, y);
						return true;
					}
				}
			}
			
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double amount) {
		if (this.listOpened) {
			return this.comboItemList.mouseScrolled(x, y, amount);
		}
		
		return false;
	}
	
	@Override
	protected boolean clicked(double x, double y) {
		return this.active && this.visible && x >= (double)this.getX() && y >= (double) this.getY() && x < (double) (this.getX() + this.width) && y < (double) (this.getY() + this.height);
	}
	
	@Override
	public boolean isMouseOver(double x, double y) {
		return this.active && this.visible && x >= (double)this.getX() && y >= (double) this.getY() && x < (double) (this.getX() + this.width) && y < (double) (this.getY() + this.height * (this.rows + 1));
	}
	
	@Override
	public void setX(int x) {
		super.setX(x);
		this.relocateComboList();
	}
	
	@Override
	public void setY(int y) {
		super.setY(y);
		this.relocateComboList();
	}
	
	private void relocateComboList() {
		this.comboItemList.updateSize(this.width, this.height * this.rows, this.getY() + this.height + 1, this.getY() + this.height * this.rows + 1);
		this.comboItemList.setLeftPos(this.getX());
	}
	
	@Override
	public void onClick(double x, double y) {
		if (this.arrowClicked(x, y)) {
			this.playDownSound(Minecraft.getInstance().getSoundManager());
			this.listOpened = !this.listOpened;
		} else {
			if (this.listOpened) {
				this.listOpened = false;
				this.playDownSound(Minecraft.getInstance().getSoundManager());
			}
		}
	}
	
	private boolean arrowClicked(double x, double y) {
		int openPressed = this.getX() + this.width - 14;
		
		return this.active && this.visible && x >= (double)openPressed && y >= (double) this.getY() && x < (double) (this.getX() + this.width) && y < (double) (this.getY() + this.height);
	}
	
	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int outlineColor = this.isFocused() ? -1 : -6250336;
		
		guiGraphics.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, outlineColor);
		guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
		
		String correctedString = this.font.plainSubstrByWidth(this.comboItemList.getSelected() == null ? "" : this.comboItemList.getSelected().displayName, this.width - 10);
		
		guiGraphics.drawString(this.font, Component.literal(correctedString), this.getX() + 4, this.getY() + this.height / 2 - this.font.lineHeight / 2 + 1, 16777215, false);
		guiGraphics.drawString(this.font, Component.literal("▼"), this.getX() + this.width - 8, this.getY() + this.height / 2 - this.font.lineHeight / 2 + 1, 16777215, false);
		
		if (this.listOpened) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 1);
			this.comboItemList.render(guiGraphics, mouseX, mouseY, partialTicks);
			guiGraphics.pose().popPose();
		}
	}
	
	@Override
	protected MutableComponent createNarrationMessage() {
		Component component = this.getMessage();
		return Component.translatable("gui.epicfight.narrate.comboBox", component);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementInput) {
		narrationElementInput.add(NarratedElementType.TITLE, this.createNarrationMessage());
	}
	
	@OnlyIn(Dist.CLIENT)
	class ComboItemList extends ObjectSelectionList<ComboItemList.ComboItemEntry> {
		public ComboItemList(Minecraft minecraft, int maxRows, int itemHeight) {
			super(minecraft, ComboBox.this.width, ComboBox.this.height, 0, itemHeight * maxRows, itemHeight);
			
			this.setRenderTopAndBottom(false);
			this.setRenderHeader(false, 0);
			this.setRenderBackground(false);
		}
		
		public void addEntry(T item, String displayName) {
			this.addEntry(new ComboItemEntry(item, displayName));
		}
		
		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			guiGraphics.fill(this.x0 - 1, this.y0 - 1, this.x1 + 1, this.y1 + 1, -1);
			guiGraphics.fill(this.x0, this.y0, this.x1, this.y1, -16777216);
			
			super.render(guiGraphics, mouseX, mouseY, partialTicks);
		}
		
		@Override
		public int getRowWidth() {
			return this.width;
		}
		
		@Override
		protected int getScrollbarPosition() {
			return this.x1 - 6;
		}

		@OnlyIn(Dist.CLIENT)
		class ComboItemEntry extends ObjectSelectionList.Entry<ComboItemList.ComboItemEntry> {
			private final T item;
			private final String displayName;
			
			protected ComboItemEntry(T item, String displayName) {
				this.item = item;
				this.displayName = displayName;
			}
			
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					ComboItemList.this.setSelected(this);
					
					return true;
				} else {
					return false;
				}
			}
			
			@Override
			public Component getNarration() {
				return Component.empty();
			}
			
			@Override
			public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				guiGraphics.drawString(ComboBox.this.font, this.displayName, left + 2, top + 1, 16777215, false);
			}
			
			public T getItem() {
				return this.item;
			}
		}
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
}
