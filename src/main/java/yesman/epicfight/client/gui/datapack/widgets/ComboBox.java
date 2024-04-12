package yesman.epicfight.client.gui.datapack.widgets;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.Maps;

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
public class ComboBox<T> extends AbstractWidget implements DataBindingComponent<T> {
	private final ComboItemList comboItemList;
	private final Font font;
	private final int maxRows;
	
	private Consumer<T> responder;
	private boolean listOpened;
	
	public ComboBox(Screen parent, Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, int maxRows, Component title, Collection<T> items, Function<T, String> displayStringMapper, Consumer<T> responder) {
		super(x1, y1, x2, y2, title);
		
		this.font = font;
		this.maxRows = Math.min(maxRows, items.size());
		this.responder = responder;
		this.comboItemList = new ComboItemList(parent.getMinecraft(), this.maxRows, 15);
		
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
		if (this.listOpened) {
			if (this.comboItemList.isMouseOver(x, y)) {
				return true;
			}
		}
		
		return this.active && this.visible && x >= (double)this.getX() && y >= (double) this.getY() && x < (double) (this.getX() + this.width) && y < (double) (this.getY() + this.height * (this.maxRows + 1));
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
		int entryHeight = 15;
		int possibleTopPosition = this.getY() - (entryHeight * this.maxRows + 1);
		int possibleBottomPosition = this.getY() + this.height + entryHeight * this.maxRows + 1;
		int bottomSpace = Minecraft.getInstance().getWindow().getGuiScaledHeight() - possibleBottomPosition;
		int topSpace = possibleTopPosition;
		
		if (bottomSpace < topSpace) {
			this.comboItemList.updateSize(this.width, entryHeight * this.maxRows, this.getY() - (entryHeight * this.maxRows + 1), this.getY() - 1);
		} else {
			this.comboItemList.updateSize(this.width, entryHeight * this.maxRows, this.getY() + this.height + 1, this.getY() + this.height + entryHeight * this.maxRows + 1);
		}
		
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
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int outlineColor = this.isFocused() ? -1 : this.isActive() ? -6250336 : -12566463;
		
		guiGraphics.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, outlineColor);
		guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
		
		String correctedString = this.font.plainSubstrByWidth(this.comboItemList.getSelected() == null ? "" : this.comboItemList.getSelected().displayName, this.width - 10);
		
		int fontColor = this.isActive() ? 16777215 : 4210752;
		
		guiGraphics.drawString(this.font, Component.literal(correctedString), this.getX() + 4, this.getY() + this.height / 2 - this.font.lineHeight / 2 + 1, fontColor, false);
		guiGraphics.drawString(this.font, Component.literal("▼"), this.getX() + this.width - 8, this.getY() + this.height / 2 - this.font.lineHeight / 2 + 1, fontColor, false);
		
		if (this.listOpened) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 10);
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
		private final Map<T, ComboItemEntry> entryMap = Maps.newHashMap();
		
		public ComboItemList(Minecraft minecraft, int maxRows, int itemHeight) {
			super(minecraft, ComboBox.this.width, ComboBox.this.height, 0, itemHeight * maxRows, itemHeight);
			
			this.setRenderTopAndBottom(false);
			this.setRenderHeader(false, 0);
			this.setRenderBackground(false);
		}
		
		public void addEntry(T item, String displayName) {
			ComboItemEntry entry = new ComboItemEntry(item, displayName);
			this.entryMap.put(item, entry);
			this.addEntry(entry);
		}
		
		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			guiGraphics.fill(this.x0 - 1, this.y0 - 1, this.x1 + 1, this.y1 + 1, -1);
			guiGraphics.fill(this.x0, this.y0, this.x1, this.y1, -16777216);
			
			super.render(guiGraphics, mouseX, mouseY, partialTicks);
		}
		
		public void setSelected(T item) {
			this.setSelected(this.entryMap.get(item));
		}
		
		@Override
		public int getRowWidth() {
			return this.width;
		}
		
		@Override
		protected int getScrollbarPosition() {
			return this.x1 - 6;
		}
		
		@Override
		public int getMaxScroll() {
			return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0));
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
					ComboBox.this.responder.accept(this.item);
					
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
	public void setResponder(Consumer<T> responder) {
		this.responder = responder;
	}
	
	@Override
	public void setValue(T value) {
		this.comboItemList.setSelected(value);
		
		if (this.responder != null) {
			this.responder.accept(value);
		}
	}
	
	@Override
	public T getValue() {
		return this.comboItemList.getSelected() == null ? null : this.comboItemList.getSelected().item;
	}
	
	@Override
	public void reset() {
		this.comboItemList.setSelected((T)null);
	}
	
	@Override
	public void tick() {
	}
}
