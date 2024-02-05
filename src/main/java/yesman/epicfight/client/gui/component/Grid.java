package yesman.epicfight.client.gui.component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import yesman.epicfight.api.utils.ParseUtil;

@OnlyIn(Dist.CLIENT)
public class Grid extends ObjectSelectionList<Grid.Row> implements ResizableComponent {
	private final Screen owner;
	private final Map<String, Column<?>> columns = Maps.newHashMap();
	private final List<ResizableButton> rowEditButtons = Lists.newArrayList();
	private final boolean transparentBackground;
	private AbstractWidget editingWidget;
	private Column<?> editingColumn;
	private int rowpostition;
	
	public Grid(GridBuilder gb) {
		super(gb.minecraft, gb.x2, gb.y2, gb.y1, gb.y1 + gb.y2, gb.rowHeight);
		
		this.owner = gb.owner;
		this.transparentBackground = gb.transparentBackground;
		this.horizontalSizingOption = gb.horizontalSizing;
		this.verticalSizingOption = gb.verticalSizing;
		
		this.xParam1 = gb.x1;
		this.yParam1 = gb.y1;
		this.xParam2 = gb.x2;
		this.yParam2 = gb.y2;
		
		this.resize(gb.minecraft.screen.getRectangle());
		
		gb.columns.entrySet().stream().map((entry) -> {
			entry.getValue().size = (int)(entry.getValue().size * (this.width / (float)gb.sizeTotal));
			return Pair.of(entry.getKey(), entry.getValue());
		}).forEach((pair) -> this.columns.put(pair.getFirst(), pair.getSecond()));
		
		this.setLeftPos(gb.x1);
		this.setRenderTopAndBottom(false);
		
		if (gb.rowEditable) {
			int x1 = this.x1 - 12;
			int x2 = 12;
			int xCorrect1 = 12;
			int xCorrect2 = 0;
			
			if (gb.buttonHorizontalSizing == HorizontalSizing.WIDTH_RIGHT) {
				x1 = 12;
				x2 = this.owner.width - this.x1;
				xCorrect1 = 0;
				xCorrect2 = 12;
			}
			
			this.rowEditButtons.add(ResizableButton.builder(Component.literal("+"), (button) -> gb.onAddPress.accept(this, button)).pos(x1 - xCorrect1, this.y0 - 12).size(x2 - xCorrect2, 12).horizontalSizing(gb.buttonHorizontalSizing).build());
			this.rowEditButtons.add(ResizableButton.builder(Component.literal("x"), (button) -> gb.onRemovePress.accept(this, button)).pos(x1, this.y0 - 12).size(x2, 12).horizontalSizing(gb.buttonHorizontalSizing).build());
		}
	}
	
	public int addRow() {
		return this.addRow(children().size());
	}
	
	public int addRow(int rowposition) {
		this.editingColumn = null;
		this.editingWidget = null;
		Row row = new Row();
		
		this.children().add(rowposition, row);
		
		for (Map.Entry<String, Column<?>> entry : this.columns.entrySet()) {
			row.setValue(entry.getKey(), entry.getValue().defaultVal);
		}
		
		return rowposition;
	}
	
	public int addRowWithDefaultValues(Object... defaultValues) {
		return addRow(this.children().size(), defaultValues);
	}
	
	public int addRow(int rowposition, Object... defaultValues) {
		this.editingColumn = null;
		this.editingWidget = null;
		Row row = new Row();
		
		for (Map.Entry<String, Column<?>> entry : this.columns.entrySet()) {
			row.setValue(entry.getKey(), entry.getValue().defaultVal);
		}
		
		for (int i = 0; i < defaultValues.length; i+=2) {
			row.setValue((String)defaultValues[i], defaultValues[i + 1]);
		}
		
		this.children().add(rowposition, row);
		
		return rowposition;
	}
	
	public int removeRow() {
		return this.removeRow(this.rowpostition);
	}
	
	public int removeRow(int row) {
		if (row < 0) {
			return -1;
		}
		
		if (this.children().size() == 0) {
			return -1;
		}
		
		if (this.rowpostition == row) {
			this.editingColumn = null;
			this.editingWidget = null;
		}
		
		this.children().remove(row);
		this.rowpostition = Math.min(row, this.children().size() - 1);
		
		return this.rowpostition;
	}
	
	public void setSelected(int rowposition) {
		this.setSelected(this.children().get(rowposition));
	}
	
	@Override
	public void setSelected(@Nullable Grid.Row row) {
		super.setSelected(row);
		this.rowpostition = this.children().indexOf(row);
	}
	
	public <T> T getValue(int rowposition, String columnName) {
		return this.children().get(rowposition).getValue(columnName);
	}
	
	public <T> void setValue(int rowposition, String columnName, T value) {
		Row row = this.children().get(rowposition);
		row.setValue(columnName, value);
	}
	
	public void setGridFocus(int rowposition, String columnName) {
		this.setSelected(rowposition);
		
		int startX = 0;
		this.editingColumn = null;
		
		for (Map.Entry<String, Column<?>> entry : this.columns.entrySet()) {
			if (entry.getKey() == columnName) {
				this.editingColumn = entry.getValue();
				break;
			}
			
			startX += entry.getValue().size;
		}
		
		if (this.editingColumn == null) {
			this.editingWidget = null;
		} else {
			if (this.editingColumn.editable) {
				this.editingWidget = this.editingColumn.createEditWidget(this.owner, this.owner.getMinecraft().font, this.x0 + startX + 2, this.getRowTop(rowposition) - 2, this.itemHeight - 3,
																			this.getSelected(), columnName, this.getSelected().getValue(columnName));
				this.editingWidget.setFocused(true);
			}
		}
	}
	
	public List<ResizableButton> getRowEditButtons() {
		return this.rowEditButtons;
	}
	
	private void relocateButtons() {
		int x = this.x1 - 24;
		int y = this.y0 - 14;
		
		for (AbstractWidget widget : this.rowEditButtons) {
			widget.setX(x);
			widget.setY(y);
			
			x += 12;
		}
	}
	
	@Override
	public void resize(ScreenRectangle screenRectangle) {
		if (this.getHorizontalSizingOption() != null) {
			this.getHorizontalSizingOption().resizeFunction.resize(this, screenRectangle, this.getX1(), this.getX2());
		}
		
		if (this.getVerticalSizingOption() != null) {
			this.getVerticalSizingOption().resizeFunction.resize(this, screenRectangle, this.getY1(), this.getY2());
		}
		
		this.getRowEditButtons().forEach(button -> button.resize(screenRectangle));
	}
	
	@Override
	public void updateSize(int width, int height, int y0, int y1) {
		this.width = width;
		this.height = height;
		this.y0 = y0;
		this.y1 = y1;
		this.x0 = 0;
		this.x1 = width;
		
		this.relocateButtons();
	}
	
	@Override
	public void setLeftPos(int x) {
		this.x0 = x;
		this.x1 = x + this.width;
		
		this.relocateButtons();
	}

	@Override
	public int getRowLeft() {
		return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
	}
	
	@Override
	public int getRowRight() {
		return this.getRowLeft() + this.getRowWidth();
	}
	
	@Override
	protected int getRowTop(int p_93512_) {
		return this.y0 - (int) this.getScrollAmount() + p_93512_ * this.itemHeight + this.headerHeight;
	}
	
	@Override
	protected int getRowBottom(int p_93486_) {
		return this.getRowTop(p_93486_) + this.itemHeight;
	}
	
	@Override
	public void setFocused(boolean focused) {
		if (!focused) {
			this.editingColumn = null;
			this.editingWidget = null;
		}
	}
	
	@Override
	public boolean isFocused() {
		return this.owner.getFocused() == this;
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.rowEditButtons.forEach((button) -> button.render(guiGraphics, mouseX, mouseY, partialTicks));
		
		int color = this.isFocused() ? -1 : -6250336;
		
		guiGraphics.fill(this.x0, this.y0, this.x1, this.y1, color);
		this.renderList(guiGraphics, mouseX, mouseY, partialTicks);
		
		if (this.editingWidget != null) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 1);
			
			this.editingWidget.setY(this.getRowTop(this.rowpostition) + 2);
			//this.editingWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
			guiGraphics.pose().popPose();
		}
	}
	
	@Override
	protected void renderList(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int rowLeft = this.getRowLeft() - 1;
		int rowWidth = this.getRowWidth();
		int itemHeight = this.itemHeight;
		int itemCount = this.getItemCount();
		int rowBottom = this.y0;
		
		for (int rowIndex = 0; rowIndex < itemCount; ++rowIndex) {
			int rowTop = this.getRowTop(rowIndex);
			rowBottom = this.getRowTop(rowIndex) + this.itemHeight;
			
			if (rowBottom >= this.y0 && rowTop <= this.y1) {
				this.renderItem(guiGraphics, mouseX, mouseY, partialTicks, rowIndex, rowLeft, rowTop, rowWidth, itemHeight);
			}
		}
		
		if (rowBottom < this.y1) {
			if (this.transparentBackground) {
				guiGraphics.setColor(0.12F, 0.12F, 0.12F, 1.0F);
				guiGraphics.blit(Screen.BACKGROUND_LOCATION, rowLeft, rowBottom + 1, rowLeft + rowWidth - 2, this.y1 - 1, rowWidth - 2, this.y1 - rowBottom - 2, 32, 32);
				guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			} else {
				guiGraphics.fill(rowLeft, rowBottom + 1, rowLeft + rowWidth - 2, this.y1 - 1, -16777216);
			}
		}
	}
	
	@Override
	protected void renderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int rowPosition, int rowLeft, int rowTop, int rowRight, int itemHeight) {
		Row row = this.getEntry(rowPosition);
		//System.out.println(rowLeft +" "+ rowRight);
		if (this.isSelectedItem(rowPosition)) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 1);
			this.renderSelection(guiGraphics, rowTop, rowRight, itemHeight, 0, 0); // color params are not used
			row.render(guiGraphics, rowPosition, rowTop, rowLeft, rowRight, itemHeight, mouseX, mouseY, false, partialTicks);
			guiGraphics.pose().popPose();
		} else {
			row.render(guiGraphics, rowPosition, rowTop, rowLeft, rowRight, itemHeight, mouseX, mouseY, false, partialTicks);
		}
	}
	
	@Override
	protected void renderSelection(GuiGraphics guiGraphics, int rowTop, int rowRight, int itemHeight, int color, int color2) {
		guiGraphics.fill(this.x0, rowTop, this.x1, rowTop + itemHeight + 1, -1);
	}
	
	@Override
	public boolean isMouseOver(double x, double y) {
		double y0 = this.rowEditButtons.size() > 0 ? this.y0 - 12 : this.y0;
		
		return y >= y0 && y <= (double)this.y1 && x >= (double)this.x0 && x <= (double)this.x1;
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		for (Button editButton : this.rowEditButtons) {
			if (editButton.mouseClicked(x, y, button)) {
				return true;
			}
		}
		
		if (!this.isMouseOver(x, y)) {
			return false;
		}
		
		if (this.editingWidget != null) {
			if (this.editingWidget.mouseClicked(x, y, button)) {
				return true;
			}
		}
		
		return super.mouseClicked(x, y, button);
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double button) {
		if (this.isFocused() && this.getMaxScroll() > 0) {
			this.setScrollAmount(this.getScrollAmount() - button * (double) this.itemHeight / 2.0D);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean keyPressed(int keycode, int p_100876_, int p_100877_) {
		if (this.editingWidget != null) {
			return this.editingWidget.keyPressed(keycode, p_100876_, p_100877_);
		}
		
		return super.keyPressed(keycode, p_100876_, p_100877_);
	}
	
	@Override
	public boolean charTyped(char c, int i) {
		if (this.editingWidget != null) {
			return this.editingWidget.charTyped(c, i);
		}
		
		return super.charTyped(c, i);
	}

	public void tick() {
		if (this.editingWidget instanceof EditBox editBox) {
			editBox.tick();
		}
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
	class Row extends ObjectSelectionList.Entry<Grid.Row> {
		private Map<String, Object> values = Maps.newLinkedHashMap();
		
		private Row() {
			for (String columnName : Grid.this.columns.keySet()) {
				this.values.put(columnName, null);
			}
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getValue(String columnName) {
			return (T)this.values.get(columnName);
		}
		
		public <T> void setValue(String columnName, T value) {
			if (!Grid.this.columns.containsKey(columnName)) {
				throw new IllegalArgumentException("There's no column named " + columnName + " in Grid");
			}
			
			this.values.put(columnName, value);
		}
		
		@Override
		public Component getNarration() {
			return Component.translatable("narrator.select");
		}
		
		@Override
		public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
			int startX = Grid.this.x0;
			
			for (Map.Entry<String, Object> entry : this.values.entrySet()) {
				Column<?> column = Grid.this.columns.get(entry.getKey());
				
				if (Grid.this.transparentBackground) {
			        guiGraphics.setColor(0.12F, 0.12F, 0.12F, 1.0F);
					guiGraphics.blit(Screen.BACKGROUND_LOCATION, startX + 1, top + 1, startX + column.size - 1, top + height, column.size - 2, height - 1, 32, 32);
					guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
				} else {
					guiGraphics.fill(startX + 1, top + 1, startX + column.size - 1, top + height, -16777216);
				}
				
				startX += column.size;
				
				String displayText = column.toVisualText(entry.getValue());
				String correctedString = Grid.this.minecraft.font.plainSubstrByWidth(displayText, column.size - 1);
				
				guiGraphics.drawString(Grid.this.minecraft.font, correctedString, left + 2, top + Grid.this.itemHeight / 2 - 4, 16777215, false);
			}
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0) {
				int rowposition = Grid.this.children().indexOf(this);
				
				if (Grid.this.getSelected() == this) {
					if (Grid.this.editingColumn == null) {
						Grid.this.setGridFocus(rowposition, this.getColumn(mouseX));
					} else {
						Grid.this.setGridFocus(rowposition, null);
					}
				} else {
					Grid.this.setGridFocus(rowposition, this.getColumn(mouseX));
				}
				
				Grid.this.setSelected(this);
				
				return true;
			} else {
				return false;
			}
		}
		
		public String getColumn(double mouseX) {
			double x = 0.0D;
			
			for (Map.Entry<String, Column<?>> entry : Grid.this.columns.entrySet()) {
				x += entry.getValue().size;
				
				if (mouseX < x) {
					return entry.getKey();
				}
			}
			
			return null;
		}
	}
	
	public static GridBuilder builder(Screen owner) {
		return new GridBuilder(owner);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class GridBuilder {
		private final Minecraft minecraft;
		private final Screen owner;
		private final Map<String, Column<?>> columns = Maps.newLinkedHashMap();
		private int x1;
		private int y1;
		private int x2;
		private int y2;
		private int rowHeight;
		private int sizeTotal;
		private boolean rowEditable;
		private boolean transparentBackground;
		private BiConsumer<Grid, Button> onAddPress;
		private BiConsumer<Grid, Button> onRemovePress;
		private HorizontalSizing horizontalSizing = HorizontalSizing.LEFT_WIDTH;
		private VerticalSizing verticalSizing = null;
		private HorizontalSizing buttonHorizontalSizing = HorizontalSizing.LEFT_WIDTH;
		
		private GridBuilder(Screen owner) {
			this.owner = owner;
			this.minecraft = owner.getMinecraft();
		}
		
		public GridBuilder addEditboxColumn(String name, String defaultValue, boolean editable, int size) {
			this.columns.put(name, new EditBoxColumn(defaultValue, editable, size));
			this.sizeTotal += size;
			
			return this;
		}
		
		public <T> GridBuilder addComboColumn(String name, Function<T, String> toVisualText, T defaultValue, T[] enums, boolean editable, int size) {
			this.columns.put(name, new ComboColumn<>(toVisualText, defaultValue, Arrays.asList(enums), editable, size));
			this.sizeTotal += size;
			
			return this;
		}
		
		public <T> GridBuilder addPopupColumn(String name, Function<T, String> toVisualText, T defaultValue, IForgeRegistry<T> registry, boolean editable, int size) {
			this.columns.put(name, new RegistryPopupColumn<>(toVisualText, defaultValue, registry, editable, size));
			this.sizeTotal += size;
			
			return this;
		}
		
		public GridBuilder xy1(int x1, int y1) {
			this.x1 = x1;
			this.y1 = y1;
			return this;
		}
		
		public GridBuilder xy2(int x2, int y2) {
			this.x2 = x2;
			this.y2 = y2;
			return this;
		}
		
		public GridBuilder rowHeight(int rowHeight) {
			this.rowHeight = rowHeight;
			return this;
		}
		
		public GridBuilder transparentBackground(boolean transparentBackground) {
			this.transparentBackground = transparentBackground;
			return this;
		}
		
		public GridBuilder rowEditable(boolean rowEditable) {
			this.rowEditable = rowEditable;
			return this;
		}
		
		public GridBuilder onAddPress(BiConsumer<Grid, Button> onAddPress) {
			this.onAddPress = onAddPress;
			return this;
		}
		
		public GridBuilder onRemovePress(BiConsumer<Grid, Button> OnRemovePress) {
			this.onRemovePress = OnRemovePress;
			return this;
		}
		
		public GridBuilder horizontalSizing(HorizontalSizing horizontalSizing) {
			this.horizontalSizing = horizontalSizing;
			return this;
		}
		
		public GridBuilder verticalSizing(VerticalSizing verticalSizing) {
			this.verticalSizing = verticalSizing;
			return this;
		}
		
		public GridBuilder buttonHorizontalSizing(HorizontalSizing buttonHorizontalSizing) {
			this.buttonHorizontalSizing = buttonHorizontalSizing;
			return this;
		}
		
		public Grid build() {
			return new Grid(this);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private static abstract class Column<T> {
		final Function<T, String> toVisualText;
		final T defaultVal;
		final boolean editable;
		int size;
		
		protected Column(Function<T, String> toVisualText, T defaultVal, boolean editable, int size) {
			this.toVisualText = toVisualText;
			this.defaultVal = defaultVal;
			this.size = size;
			this.editable = editable;
		}
		
		@SuppressWarnings("unchecked")
		public String toVisualText(Object object) {
			return this.toVisualText.apply((T)object);
		}
		
		public abstract AbstractWidget createEditWidget(Screen owner, Font font, int x, int y, int height, Row row, String colName, T value);
	}
	
	@OnlyIn(Dist.CLIENT)
	private static class EditBoxColumn extends Column<String> {
		protected EditBoxColumn(String defaultVal, boolean editable, int size) {
			super((string) -> string, defaultVal, editable, size);
		}
		
		@Override
		public AbstractWidget createEditWidget(Screen owner, Font font, int x, int y, int height, Row row, String colName, String value) {
			EditBox editbox = new EditBox(font, x, y, this.size - 4, height, Component.literal("grid.editbox"));
			editbox.setValue(value);
			editbox.setResponder((string) -> row.setValue(colName, string));
			
			return editbox;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private static class ComboColumn<T> extends Column<T> {
		final List<T> enums;
		
		protected ComboColumn(Function<T, String> toVisualText, T defaultVal, List<T> enums, boolean editable, int size) {
			super(toVisualText, defaultVal, editable, size);
			
			this.enums = enums;
		}
		
		@Override
		public AbstractWidget createEditWidget(Screen owner, Font font, int x, int y, int height, Row row, String colName, T value) {
			return new ComboBox<>(owner, font, x, y, this.size - 4, height, null, null, 8, Component.literal("grid.comboEdit"), this.enums, (e) -> ParseUtil.makeFirstLetterToUpper(e.toString()));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private static class RegistryPopupColumn<T> extends Column<T> {
		final IForgeRegistry<T> registry;
		
		protected RegistryPopupColumn(Function<T, String> toVisualText, T defaultVal, IForgeRegistry<T> registry, boolean editable, int size) {
			super(toVisualText, defaultVal, editable, size);
			
			this.registry = registry;
		}
		
		@Override
		public AbstractWidget createEditWidget(Screen owner, Font font, int x, int y, int height, Row row, String colName, T value) {
			return new PopupBox<>(owner, font, x, y, this.size - 4, height, null, null, Component.literal("grid.popupEdit"), this.registry);
		}
	}
	
	/*******************************************************************
	 * @ResizableComponent                                             *
	 *******************************************************************/
	private final int xParam1;
	private final int yParam1;
	private final int xParam2;
	private final int yParam2;
	private final HorizontalSizing horizontalSizingOption;
	private final VerticalSizing verticalSizingOption;
	
	@Override
	public void relocateX(int x) {
		this.x0 = x;
		this.x1 = x + this.width;
		
		this.relocateButtons();
	}
	
	@Override
	public void relocateY(int y) {
		this.y0 = y;
		this.y1 = y + this.height;
		
		this.relocateButtons();
	}
	
	@Override
	public void setX(int x) {
		this.x0 = x;
		this.x1 = this.x0 + width;
		
		this.relocateButtons();
	}
	
	@Override
	public void setY(int y) {
		this.y0 = y;
		this.y1 = this.y0 + height;
		
		this.relocateButtons();
	}
	
	@Override
	public void setWidth(int width) {
		this.x1 = this.x0 + width;
		this.width = width;
		
		this.relocateButtons();
	}
	
	@Override
	public void setHeight(int height) {
		this.y1 = this.y0 + height;
		this.height = height;
		
		this.relocateButtons();
	}
	
	@Override
	public int getX() {
		return this.x0;
	}
	
	@Override
	public int getY() {
		return this.y0;
	}
	
	@Override
	public int getX1() {
		return this.xParam1;
	}

	@Override
	public int getX2() {
		return this.xParam2;
	}

	@Override
	public int getY1() {
		return this.yParam1;
	}

	@Override
	public int getY2() {
		return this.yParam2;
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